package com.example.streamtobluetoothplayer.utils

import io.github.kaaes.spotify.webapi.core.models.Pager
import io.github.kaaes.spotify.webapi.core.models.PlaylistSimple
import io.github.kaaes.spotify.webapi.core.models.PlaylistTrack
import io.github.kaaes.spotify.webapi.core.models.SavedTrack
import io.github.kaaes.spotify.webapi.core.models.UserPrivate
import io.github.kaaes.spotify.webapi.core.models.SavedAlbum
import io.github.kaaes.spotify.webapi.retrofit.v2.Spotify
import io.github.kaaes.spotify.webapi.retrofit.v2.SpotifyService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object SpotifyWebApiService {
    private var spotifyService: SpotifyService? = null

    fun initialize(accessToken: String) {
        spotifyService = Spotify.createAuthenticatedService(accessToken)
    }

    fun getLikedTracks(callback: (Pager<SavedTrack>?) -> Unit) {
        spotifyService?.mySavedTracks?.enqueue(object : Callback<Pager<SavedTrack>> {
            override fun onResponse(
                call: Call<Pager<SavedTrack>>,
                response: Response<Pager<SavedTrack>>
            ) {
                if (!response.isSuccessful) {
                    callback(null) // Notify the caller that an error occurred.
                }

                callback(response.body()) // Return the result to the caller.
            }

            override fun onFailure(call: Call<Pager<SavedTrack>>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    fun getPlaylists(callback: (Pager<PlaylistSimple>?) -> Unit) {
        spotifyService?.myPlaylists?.enqueue(object : Callback<Pager<PlaylistSimple>> {
            override fun onResponse(
                call: Call<Pager<PlaylistSimple>>,
                response: Response<Pager<PlaylistSimple>>
            ) {
                if (!response.isSuccessful) {
                    callback(null)
                }

                callback(response.body())
            }

            override fun onFailure(call: Call<Pager<PlaylistSimple>>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    fun getPlaylistTracks(playlistId: String, callback: (Pager<PlaylistTrack>?) -> Unit) {
        var userId = ""
        spotifyService?.me?.enqueue(object : Callback<UserPrivate> {
            override fun onResponse(call: Call<UserPrivate>, response: Response<UserPrivate>) {
                if (!response.isSuccessful) {
                    callback(null)
                }

                userId = response.body()?.id ?: ""
            }

            override fun onFailure(call: Call<UserPrivate>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })


        spotifyService?.getPlaylistTracks(userId, playlistId)?.enqueue(object: Callback<Pager<PlaylistTrack>> {
            override fun onResponse(
                call: Call<Pager<PlaylistTrack>>,
                response: Response<Pager<PlaylistTrack>>
            ) {
                if (!response.isSuccessful) {
                    callback(null)
                }

                callback(response.body())
            }

            override fun onFailure(call: Call<Pager<PlaylistTrack>>, t: Throwable) {
                callback(null)
            }
        })
    }

    fun getSavedAlbums(callback: (Pager<SavedAlbum>?) -> Unit) {
        spotifyService?.mySavedAlbums?.enqueue(object : Callback<Pager<SavedAlbum>> {
            override fun onResponse(
                call: Call<Pager<SavedAlbum>>,
                response: Response<Pager<SavedAlbum>>
            ) {
                if (!response.isSuccessful) {
                    callback(null) // Notify the caller that an error occurred.
                }

                callback(response.body()) // Return the result to the caller.
            }

            override fun onFailure(call: Call<Pager<SavedAlbum>>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }
}