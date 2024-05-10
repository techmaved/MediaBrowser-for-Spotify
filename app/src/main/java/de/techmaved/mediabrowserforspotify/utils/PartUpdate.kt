package de.techmaved.mediabrowserforspotify.utils

import android.net.Uri
import com.adamratzman.spotify.SpotifyClientApi
import de.techmaved.mediabrowserforspotify.MyApplication
import de.techmaved.mediabrowserforspotify.entities.Browsable
import de.techmaved.mediabrowserforspotify.entities.MediaItem
import de.techmaved.mediabrowserforspotify.utils.MediaItemType.ALBUM_ID
import de.techmaved.mediabrowserforspotify.utils.MediaItemType.LIKED_SONG_ID
import de.techmaved.mediabrowserforspotify.utils.MediaItemType.PLAYLIST_ID
import de.techmaved.mediabrowserforspotify.utils.MediaItemType.SHOW_ID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

suspend fun doPartUpdate(updateRecord: MutableMap<String, MutableList<String>>, userName: String): Flow<Int> = flow {
    val database = AppDatabase.getDatabase(MyApplication.context)

    updateRecord.forEach { category ->
        category.value.forEach id@ { id ->
            val browsable = getBrowsable(id, category.key, userName)
            val mediaItems = browsable?.let { getMediaItems(id, category.key, it) }

            if (browsable == null || mediaItems == null) {
                return@id;
            }

            if (database.browsableDao().getBrowsable(browsable.uri) != null) {
                database.browsableDao().delete(browsable.uri)
                emit(-1)

                emit(database.mediaDao().getCountByBrowsable(browsable.uri) * -1)
                database.mediaDao().deleteBrowsableMediaItems(browsable.uri)
            }

            database.browsableDao().inset(browsable)
            emit(1)

            database.mediaDao().insertAll(mediaItems)
            emit(mediaItems.count())
        }
    }
}

suspend fun getBrowsable(id: String, category: String, userName: String): Browsable? {
    var browsable: Browsable? = null

    when (category) {
        ChipType.LIKED_SONGS -> {
            val likedSongs = SpotifyWebApiService().getLikedSongsMirror(userName)

            likedSongs?.let {
                browsable = Browsable(
                    uri = Uri.parse(it.uri.uri),
                    name = it.name,
                    type = LIKED_SONG_ID
                )
            }
        }
        ChipType.ALBUMS -> {
            val album = guardValidSpotifyApi { api: SpotifyClientApi -> api.albums.getAlbum(id) }

            album?.let {
                browsable = Browsable(
                    uri = Uri.parse(it.uri.uri),
                    name = it.name,
                    type = ALBUM_ID
                )
            }
        }
        ChipType.PLAYLISTS -> {
            val playlist = guardValidSpotifyApi { api: SpotifyClientApi -> api.playlists.getPlaylist(id) }

            playlist?.let {
                browsable = Browsable(
                    uri = Uri.parse(it.uri.uri),
                    name = it.name,
                    type = PLAYLIST_ID
                )
            }
        }
        ChipType.SHOWS -> {
            val show = guardValidSpotifyApi { api: SpotifyClientApi -> api.shows.getShow(id) }

            show?.let {
                browsable = Browsable(
                    uri = Uri.parse(it.uri.uri),
                    name = it.name,
                    type = SHOW_ID
                )
            }
        }
    }

    return browsable
}

suspend fun getMediaItems(id: String, category: String, browsable: Browsable): List<MediaItem> {
    val spotifyWebApiService = SpotifyWebApiService()
    val list = emptyList<MediaItem>().toMutableList()

    when (category) {
        ChipType.LIKED_SONGS -> {
            val likedSongsTracks = spotifyWebApiService.getPlaylistTracks(id)
            likedSongsTracks.forEach { likedSongTrack ->
                likedSongTrack.track?.asTrack?.let {track ->
                    list.add(MediaItem(
                        uri = Uri.parse(track.uri.uri),
                        browsableUri = browsable.uri,
                        title = track.name,
                        artist = track.artists.joinToString(", ") { artistSimple -> artistSimple.name ?: "" }
                    ))
                }
            }
        }
        ChipType.ALBUMS -> {
            val albumTracks = spotifyWebApiService.getAlbumTracks(id)
            albumTracks.forEach { albumTrack ->
                    list.add(MediaItem(
                        uri = Uri.parse(albumTrack.uri.uri),
                        browsableUri = browsable.uri,
                        title = albumTrack.name,
                        artist = albumTrack.artists.joinToString(", ") { artistSimple -> artistSimple.name ?: "" }
                    ))
            }
        }
        ChipType.PLAYLISTS-> {
            val playlistTracks = spotifyWebApiService.getPlaylistTracks(id)
            playlistTracks.forEach {playlistTrack ->
                playlistTrack.track?.asTrack?.let {track ->
                    list.add(MediaItem(
                        uri = Uri.parse(track.uri.uri),
                        browsableUri = browsable.uri,
                        title = track.name,
                        artist = track.artists.joinToString(", ") { artistSimple -> artistSimple.name ?: "" }
                    ))
                }
            }
        }
        ChipType.SHOWS -> {
            val showEpisodes = spotifyWebApiService.getShowEpisodes(id)
            showEpisodes.forEach { showEpisode ->
                list.add(MediaItem(
                    uri = Uri.parse(showEpisode.uri.uri),
                    browsableUri = browsable.uri,
                    title = showEpisode.name,
                    artist = browsable.name
                ))

            }
        }
    }

    return list
}