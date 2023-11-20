package com.example.streamtobluetoothplayer.utils

import com.adamratzman.spotify.SpotifyClientApi
import com.adamratzman.spotify.models.PlaylistTrack
import com.adamratzman.spotify.models.SavedAlbum
import com.adamratzman.spotify.models.SavedShow
import com.adamratzman.spotify.models.SavedTrack
import com.adamratzman.spotify.models.SimpleEpisode
import com.adamratzman.spotify.models.SimplePlaylist
import com.adamratzman.spotify.models.SimpleTrack

class SpotifyWebApiService {
     fun getAllSavedTracks(): List<SavedTrack> {
        val tracks = mutableListOf<SavedTrack>()
        var limit: Int = 50
        var offset: Int = 0
        var next: String? = ""

         do {
             val savedTracks = guardValidSpotifyApi { api: SpotifyClientApi -> api.library.getSavedTracks(limit, offset) }
             tracks.addAll(savedTracks?.items as Collection<SavedTrack>)

             offset += limit
             next = savedTracks.next
         } while (next != null)

        return tracks
    }

    fun getPlaylists(user: String?): List<SimplePlaylist>? {
        if (user == null) {
             return null
        }

        val playlists = mutableListOf<SimplePlaylist>()
        val limit: Int = 50
        var offset: Int = 0
        var next: String?

        do {
            val userPlaylists = guardValidSpotifyApi { api: SpotifyClientApi -> api.playlists.getUserPlaylists(user, limit, offset) }
            playlists.addAll(userPlaylists?.items as Collection<SimplePlaylist>)

            offset += limit
            next = userPlaylists.next
        } while (next != null)

        return playlists
    }

    fun getSavedAlbums(): List<SavedAlbum> {
        val savedAlbums = mutableListOf<SavedAlbum>()
        val limit = 50
        var offset = 0
        var next: String?

        do {
            val albums = guardValidSpotifyApi { api: SpotifyClientApi -> api.library.getSavedAlbums(limit, offset) }
            savedAlbums.addAll(albums?.items as Collection<SavedAlbum>)

            offset += limit
            next = albums.next
        } while (next != null)

        return savedAlbums
    }

    fun getAlbumTracks(albumId: String): List<SimpleTrack> {
        val albumTracks = mutableListOf<SimpleTrack>()
        val limit = 50
        var offset = 0
        var next: String?

        do {
            val tracks = guardValidSpotifyApi { api: SpotifyClientApi -> api.albums.getAlbumTracks(albumId) }
            albumTracks.addAll(tracks?.items as Collection<SimpleTrack>)

            offset += limit
            next = tracks.next
        } while (next != null)

        return albumTracks
    }

    fun getPlaylistTracks(playlistId: String): List<PlaylistTrack> {
        val playlistTracks = mutableListOf<PlaylistTrack>()
        val limit = 50
        var offset = 0
        var next: String?

        do {
            val tracks = guardValidSpotifyApi { api: SpotifyClientApi -> api.playlists.getPlaylistTracks(playlistId, limit, offset) }
            playlistTracks.addAll(tracks?.items as Collection<PlaylistTrack>)

            offset += limit
            next = tracks.next
        } while (next != null)

        return playlistTracks
    }

    fun getSavedShows(): List<SavedShow> {
        val shows = mutableListOf<SavedShow>()
        val limit = 50
        var offset = 0
        var next: String?

        do {
            val savedShows = guardValidSpotifyApi { api: SpotifyClientApi -> api.library.getSavedShows(limit, offset) }
            shows.addAll(savedShows?.items as Collection<SavedShow>)

            offset += limit
            next = savedShows.next
        } while (next != null)

        return shows
    }

    fun getShowEpisodes(showId: String): List<SimpleEpisode> {
        val episodes = mutableListOf<SimpleEpisode>()
        val limit = 50
        var offset = 0
        var next: String?

        do {
            val showEpisodes = guardValidSpotifyApi { api: SpotifyClientApi -> api.shows.getShowEpisodes(showId, limit, offset) }
            episodes.addAll(showEpisodes?.items as Collection<SimpleEpisode>)

            offset += limit
            next = showEpisodes.next
        } while (next != null)

        return episodes
    }
}