package com.example.streamtobluetoothplayer

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.PendingIntent.getActivity
import android.app.TaskStackBuilder
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media3.common.AudioAttributes
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.Util
import androidx.media3.datasource.DataSourceBitmapLoader
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.CacheBitmapLoader
import androidx.media3.session.LibraryResult
import androidx.media3.session.LibraryResult.RESULT_ERROR_NOT_SUPPORTED
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture

class PlaybackService : MediaLibraryService() {
    private val librarySessionCallback = CustomMediaLibrarySessionCallback()
    private val service = SpotifyWebApiService

    private lateinit var player: ExoPlayer
    private lateinit var mediaLibrarySession: MediaLibrarySession

    companion object {
        private const val CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_ON =
            "android.media3.session.demo.SHUFFLE_ON"
        private const val CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_OFF =
            "android.media3.session.demo.SHUFFLE_OFF"
        private const val NOTIFICATION_ID = 123
        private const val CHANNEL_ID = "demo_session_notification_channel_id"
        private val immutableFlag = if (Build.VERSION.SDK_INT >= 23) FLAG_IMMUTABLE else 0
    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun onCreate() {
        super.onCreate()
        initializeSessionAndPlayer()
        setListener(MediaSessionServiceListener())
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession {
        return mediaLibrarySession
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        if (!player.playWhenReady || player.mediaItemCount == 0) {
            stopSelf()
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun onDestroy() {
        mediaLibrarySession.setSessionActivity(getBackStackedActivity())
        mediaLibrarySession.release()
        player.release()
        clearListener()
        super.onDestroy()
    }

    private inner class CustomMediaLibrarySessionCallback : MediaLibrarySession.Callback {

        @SuppressLint("UnsafeOptInUsageError")
        override fun onConnect(session: MediaSession, controller: MediaSession.ControllerInfo): MediaSession.ConnectionResult {
            val availableSessionCommands =
                MediaSession.ConnectionResult.DEFAULT_SESSION_AND_LIBRARY_COMMANDS.buildUpon()

            return MediaSession.ConnectionResult.AcceptedResultBuilder(session)
                .setAvailableSessionCommands(availableSessionCommands.build())
                .build()
        }

        override fun onCustomCommand(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
            customCommand: SessionCommand,
            args: Bundle
        ): ListenableFuture<SessionResult> {
            if (CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_ON == customCommand.customAction) {
                // Enable shuffling.
                player.shuffleModeEnabled = true
                // Change the custom layout to contain the `Disable shuffling` command.
            } else if (CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_OFF == customCommand.customAction) {
                // Disable shuffling.
                player.shuffleModeEnabled = false
                // Change the custom layout to contain the `Enable shuffling` command.
            }
            return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
        }

        override fun onGetLibraryRoot(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            params: LibraryParams?
        ): ListenableFuture<LibraryResult<MediaItem>> {
            if (params != null && params.isRecent) {
                // The service currently does not support playback resumption. Tell System UI by returning
                // an error of type 'RESULT_ERROR_NOT_SUPPORTED' for a `params.isRecent` request. See
                // https://github.com/androidx/media/issues/355
                return Futures.immediateFuture(LibraryResult.ofError(RESULT_ERROR_NOT_SUPPORTED))
            }
            return Futures.immediateFuture(LibraryResult.ofItem(MediaItemTree.getRootItem(), params))
        }

        override fun onGetItem(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            mediaId: String
        ): ListenableFuture<LibraryResult<MediaItem>> {
            val item =
                MediaItemTree.getItem(mediaId)
                    ?: return Futures.immediateFuture(
                        LibraryResult.ofError(LibraryResult.RESULT_ERROR_BAD_VALUE)
                    )
            return Futures.immediateFuture(LibraryResult.ofItem(item, /* params= */ null))
        }

        override fun onSubscribe(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            parentId: String,
            params: LibraryParams?
        ): ListenableFuture<LibraryResult<Void>> {
            val children =
                MediaItemTree.getChildren(parentId)
                    ?: return Futures.immediateFuture(
                        LibraryResult.ofError(LibraryResult.RESULT_ERROR_BAD_VALUE)
                    )
            session.notifyChildrenChanged(browser, parentId, children.size, params)
            return Futures.immediateFuture(LibraryResult.ofVoid())
        }

        override fun onGetChildren(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            parentId: String,
            page: Int,
            pageSize: Int,
            params: LibraryParams?
        ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> {
            val children =
                MediaItemTree.getChildren(parentId)
                    ?: return Futures.immediateFuture(
                        LibraryResult.ofError(LibraryResult.RESULT_ERROR_BAD_VALUE)
                    )

            return Futures.immediateFuture(LibraryResult.ofItemList(children, params))
        }

        override fun onAddMediaItems(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo,
            mediaItems: List<MediaItem>
        ): ListenableFuture<List<MediaItem>> {
            val updatedMediaItems: List<MediaItem> =
                mediaItems.map { mediaItem ->
                    MediaItemTree.getItem(mediaItem.mediaId) ?: mediaItem
                }
            return Futures.immediateFuture(updatedMediaItems)
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun initializeSessionAndPlayer() {
        player =
            ExoPlayer.Builder(this)
                .setAudioAttributes(AudioAttributes.DEFAULT, /* handleAudioFocus= */ true)
                .build()
        MediaItemTree.initialize()

        mediaLibrarySession =
            MediaLibrarySession.Builder(this, player, librarySessionCallback)
                .setSessionActivity(getSingleTopActivity())
                .setBitmapLoader(CacheBitmapLoader(DataSourceBitmapLoader(/* context= */ this)))
                .build()
    }


    private fun getSingleTopActivity(): PendingIntent {
        return getActivity(
            this,
            0,
            Intent(this, PlayerActivity::class.java),
            immutableFlag or FLAG_UPDATE_CURRENT
        )
    }

    private fun getBackStackedActivity(): PendingIntent {
        return TaskStackBuilder.create(this).run {
            addNextIntent(Intent(this@PlaybackService, MainActivity::class.java))
            addNextIntent(Intent(this@PlaybackService, PlayerActivity::class.java))
            getPendingIntent(0, immutableFlag or FLAG_UPDATE_CURRENT)
        }
    }

    private fun ignoreFuture(customLayout: ListenableFuture<SessionResult>) {
        /* Do nothing. */
    }

    @SuppressLint("UnsafeOptInUsageError")
    private inner class MediaSessionServiceListener : Listener {

        /**
         * This method is only required to be implemented on Android 12 or above when an attempt is made
         * by a media controller to resume playback when the {@link MediaSessionService} is in the
         * background.
         */
        @SuppressLint("MissingPermission") // TODO: b/280766358 - Request this permission at runtime.
        override fun onForegroundServiceStartNotAllowedException() {
            val notificationManagerCompat = NotificationManagerCompat.from(this@PlaybackService)
            ensureNotificationChannel(notificationManagerCompat)
            val pendingIntent =
                TaskStackBuilder.create(this@PlaybackService).run {
                    addNextIntent(Intent(this@PlaybackService, MainActivity::class.java))
                    getPendingIntent(0, immutableFlag or FLAG_UPDATE_CURRENT)
                }
            val builder =
                NotificationCompat.Builder(this@PlaybackService, CHANNEL_ID)
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
            notificationManagerCompat.notify(NOTIFICATION_ID, builder.build())
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun ensureNotificationChannel(notificationManagerCompat: NotificationManagerCompat) {
        if (Util.SDK_INT < 26 || notificationManagerCompat.getNotificationChannel(CHANNEL_ID) != null) {
            return
        }

        val channel =
            NotificationChannel(
                CHANNEL_ID,
                "notifiaction",
                NotificationManager.IMPORTANCE_DEFAULT
            )
        notificationManagerCompat.createNotificationChannel(channel)
    }
}