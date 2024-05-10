/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.techmaved.mediabrowserforspotify.utils

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaItem.SubtitleConfiguration
import androidx.media3.common.MediaMetadata
import com.adamratzman.spotify.models.PlaylistTrack
import com.adamratzman.spotify.models.SavedAlbum
import com.adamratzman.spotify.models.SavedShow
import com.adamratzman.spotify.models.SimpleEpisode
import com.adamratzman.spotify.models.SimplePlaylist
import com.adamratzman.spotify.models.SimpleTrack
import com.google.common.collect.ImmutableList
import de.techmaved.mediabrowserforspotify.MyApplication
import de.techmaved.mediabrowserforspotify.entities.Browsable
import de.techmaved.mediabrowserforspotify.entities.BrowsableWithMediaItems
import de.techmaved.mediabrowserforspotify.utils.MediaItemType.ALBUM_ID
import de.techmaved.mediabrowserforspotify.utils.MediaItemType.LIKED_SONG_ID
import de.techmaved.mediabrowserforspotify.utils.MediaItemType.PLAYLIST_ID
import de.techmaved.mediabrowserforspotify.utils.MediaItemType.ROOT_ID
import de.techmaved.mediabrowserforspotify.utils.MediaItemType.SHOW_ID
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

/**
 * A sample media catalog that represents media items as a tree.
 *
 * It fetched the data from {@code catalog.json}. The root's children are folders containing media
 * items from the same album/artist/genre.
 *
 * Each app should have their own way of representing the tree. MediaItemTree is used for
 * demonstration purpose only.
 */
object MediaItemTree {
    private var treeNodes: MutableMap<String, MediaItemNode> = mutableMapOf()
    private var titleMap: MutableMap<String, MediaItemNode> = mutableMapOf()
    private var isInitialized = false
    private val spotifyWebApiService = SpotifyWebApiService()
    private val database = AppDatabase.getDatabase(MyApplication.context)

    private class MediaItemNode(val item: MediaItem) {
        private val children: MutableList<MediaItem> = ArrayList()

        fun addChild(childID: String) {
            this.children.add(treeNodes[childID]!!.item)
        }

        fun addChildren(childrenIDs: Array<String>) {
            childrenIDs.forEach { childId ->
                this.children.add(treeNodes[childId]!!.item)
            }
        }

        fun getChildren(): List<MediaItem> {
            return ImmutableList.copyOf(children)
        }
    }

    private fun buildMediaItem(
        title: String,
        mediaId: String,
        isPlayable: Boolean,
        isBrowsable: Boolean,
        mediaType: @MediaMetadata.MediaType Int,
        subtitleConfigurations: List<SubtitleConfiguration> = mutableListOf(),
        album: String? = null,
        artist: String? = null,
        genre: String? = null,
        sourceUri: Uri? = null,
        imageUri: Uri? = null
    ): MediaItem {
        val metadata =
            MediaMetadata.Builder()
                .setAlbumTitle(album)
                .setTitle(title)
                .setArtist(artist)
                .setGenre(genre)
                .setIsBrowsable(isBrowsable)
                .setIsPlayable(isPlayable)
                .setArtworkUri(imageUri)
                .setMediaType(mediaType)
                .build()

        return MediaItem.Builder()
            .setMediaId(mediaId)
            .setSubtitleConfigurations(subtitleConfigurations)
            .setMediaMetadata(metadata)
            .setUri(sourceUri)
            .build()
    }

    fun initialize() {
        if (isInitialized) return
        isInitialized = true

        createInitialMediaTree()
    }

    private fun createInitialMediaTree() {
        treeNodes[ROOT_ID] =
            MediaItemNode(
                buildMediaItem(
                    title = "Root Folder",
                    mediaId = ROOT_ID,
                    isPlayable = false,
                    isBrowsable = true,
                    mediaType = MediaMetadata.MEDIA_TYPE_FOLDER_MIXED
                )
            )
        treeNodes[LIKED_SONG_ID] =
            MediaItemNode(
                buildMediaItem(
                    title = "Liked songs",
                    mediaId = LIKED_SONG_ID,
                    isPlayable = false,
                    isBrowsable = true,
                    mediaType = MediaMetadata.MEDIA_TYPE_MUSIC
                )
            )

        treeNodes[PLAYLIST_ID] = MediaItemNode(
            buildMediaItem(
                title = "Playlists",
                mediaId = PLAYLIST_ID,
                isPlayable = false,
                isBrowsable = true,
                mediaType = MediaMetadata.MEDIA_TYPE_FOLDER_PLAYLISTS
            )
        )

        treeNodes[SHOW_ID] = MediaItemNode(
            buildMediaItem(
                title = "Shows",
                mediaId = SHOW_ID,
                isPlayable = false,
                isBrowsable = true,
                mediaType = MediaMetadata.MEDIA_TYPE_FOLDER_PODCASTS
            )
        )

        treeNodes[ALBUM_ID] = MediaItemNode(
            buildMediaItem(
                title = "Album",
                mediaId = ALBUM_ID,
                isPlayable = false,
                isBrowsable = true,
                mediaType = MediaMetadata.MEDIA_TYPE_FOLDER_ALBUMS
            )
        )

        treeNodes[ROOT_ID]!!.addChildren(arrayOf(LIKED_SONG_ID, PLAYLIST_ID, ALBUM_ID, SHOW_ID))
    }

    fun buildFromCache(browsablesWithMediaItems: List<BrowsableWithMediaItems>) {
        createInitialMediaTree()

        browsablesWithMediaItems
            .forEach { browsableWithMediaItems: BrowsableWithMediaItems ->
                treeNodes[browsableWithMediaItems.browsable.uri.toString()] =
                    MediaItemNode(
                        buildMediaItem(
                            title = browsableWithMediaItems.browsable.name,
                            mediaId = browsableWithMediaItems.browsable.uri.toString(),
                            isPlayable = false,
                            isBrowsable = true,
                            mediaType = MediaMetadata.MEDIA_TYPE_PLAYLIST
                        )
                    )

                if (browsableWithMediaItems.browsable.type != LIKED_SONG_ID) {
                    treeNodes[browsableWithMediaItems.browsable.type]!!.addChild(browsableWithMediaItems.browsable.uri.toString())
                }

                browsableWithMediaItems.mediaItems.forEach { mediaItem: de.techmaved.mediabrowserforspotify.entities.MediaItem ->
                    treeNodes[mediaItem.uri.toString()] =
                        MediaItemNode(
                            buildMediaItem(
                                title = mediaItem.title,
                                mediaId = mediaItem.uri.toString(),
                                isPlayable = true,
                                isBrowsable = false,
                                mediaType = MediaMetadata.MEDIA_TYPE_MUSIC,
                                album = null,
                                artist = mediaItem.artist,
                                genre = "",
                                sourceUri = mediaItem.uri,
                                imageUri = mediaItem.browsableUri
                            )
                        )

                    // add songs from liked songs mirror to node
                    if (browsableWithMediaItems.browsable.type != LIKED_SONG_ID) {
                        treeNodes[browsableWithMediaItems.browsable.uri.toString()]!!.addChild(mediaItem.uri.toString())
                    } else {
                        treeNodes[LIKED_SONG_ID]!!.addChild(mediaItem.uri.toString())
                    }
                }
            }
    }

    private fun insertBrowsable(uri: String, name: String, type: String) {
        CoroutineScope(Dispatchers.IO).launch {
            database.browsableDao().inset(
                Browsable(
                    uri = Uri.parse(uri),
                    name = name,
                    type = type
                )
            )
        }
    }

    private fun insertMediaItem(uri: String, browsableUri: String, title: String, artists: String) {
        CoroutineScope(Dispatchers.IO).launch {
            database.mediaDao().inset(
                de.techmaved.mediabrowserforspotify.entities.MediaItem(
                    uri = Uri.parse(uri),
                    browsableUri = Uri.parse(browsableUri),
                    title = title,
                    artist = artists
                )
            )
        }
    }

    suspend fun populateMediaTree(userName: String): Flow<Unit> = channelFlow {
        spotifyWebApiService.getPlaylists(userName)?.forEach { simplePlaylist: SimplePlaylist ->
            if (simplePlaylist.name == spotifyWebApiService.mirrorName) {
                insertBrowsable(
                    uri = simplePlaylist.uri.uri,
                    name = simplePlaylist.name,
                    type = LIKED_SONG_ID
                )

                send(Unit)

                spotifyWebApiService.getPlaylistTracks(simplePlaylist.id).forEach { playlistTrack: PlaylistTrack ->
                    playlistTrack.track?.asTrack?.let {
                        insertMediaItem(
                            uri = it.uri.uri,
                            browsableUri = simplePlaylist.uri.uri,
                            title = it.name,
                            artists = it.artists.joinToString(", ") { artistSimple -> artistSimple.name ?: "" }
                        )
                        send(Unit)
                    }
                }
                return@forEach
            }

            insertBrowsable(
                uri = simplePlaylist.uri.uri,
                name = simplePlaylist.name,
                type = PLAYLIST_ID
            )
            send(Unit)

            spotifyWebApiService.getPlaylistTracks(simplePlaylist.id).forEach { playlistTrack: PlaylistTrack ->
                playlistTrack.track?.asTrack?.let {
                    insertMediaItem(
                        uri = it.uri.uri,
                        browsableUri = simplePlaylist.uri.uri,
                        title = it.name,
                        artists = it.artists.joinToString(", ") { artistSimple -> artistSimple.name ?: "" }
                    )
                    send(Unit)
                }
            }
        }

        spotifyWebApiService.getSavedAlbums().forEach { savedAlbum: SavedAlbum ->
            insertBrowsable(
                uri = savedAlbum.album.uri.uri,
                name = savedAlbum.album.name,
                type = ALBUM_ID
            )

            send(Unit)

            spotifyWebApiService.getAlbumTracks(savedAlbum.album.id).forEach { simpleTrack: SimpleTrack ->
                CoroutineScope(Dispatchers.IO).launch {
                    simpleTrack.toFullTrack().let {
                        if (it != null) {
                            insertMediaItem(
                                uri = it.uri.uri,
                                browsableUri = savedAlbum.album.uri.uri,
                                title = it.name,
                                artists = it.artists.joinToString(", ") { artistSimple -> artistSimple.name ?: "" }
                            )

                            send(Unit)
                        }
                    }
                }
            }
        }

        spotifyWebApiService.getSavedShows().forEach { savedShow: SavedShow ->
            insertBrowsable(
                uri = savedShow.show.uri.uri,
                name = savedShow.show.name,
                type = SHOW_ID
            )

            send(Unit)

            spotifyWebApiService.getShowEpisodes(savedShow.show.id).forEach { simpleEpisode: SimpleEpisode ->
                insertMediaItem(
                    uri = simpleEpisode.uri.uri,
                    browsableUri = savedShow.show.uri.uri,
                    title = simpleEpisode.name,
                    artists = savedShow.show.name
                )

                send(Unit)
            }
        }
    }

    fun getItem(id: String): MediaItem? {
        return treeNodes[id]?.item
    }

    fun getRootItem(): MediaItem {
        return treeNodes[ROOT_ID]!!.item
    }

    fun getChildren(id: String): List<MediaItem>? {
        return treeNodes[id]?.getChildren()
    }
}
