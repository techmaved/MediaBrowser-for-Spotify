package com.example.streamtobluetoothplayer

import android.os.IBinder
import io.github.kaaes.spotify.webapi.core.models.Pager
import io.github.kaaes.spotify.webapi.core.models.SavedTrack
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
}