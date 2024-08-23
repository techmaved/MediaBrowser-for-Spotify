# MediaBrowser for Spotify

## Overview
This app provides your Spotify music to other apps with the help of Android Jetpack's Media3 MediaBrowser.
The following content from your library will be shared via the MediaBrowser on your Android device.
* liked Songs
* your Playlists
  * tracks included
* saved Albums
  * tracks included
* Podcasts
  * episodes included

While browsing the media library of MediaBrowser for Spotify select songs/episodes, and they
are going to either on:
* an active device associated to your Spotify account
* on device with your installed Spotify app (fallback)

## Prerequisites
* Spotify account
* Android 7.0
* Spotify Premium

### Development Status
Currently this app is in its development phase. To use it yourself you need to make some changes before running it. Please follow these steps to get it working.
1. Create your own Spotify App with [this](https://developer.spotify.com/documentation/android/tutorials/getting-started#register-your-app) tutorial section
2. Change the Spotify Client ID build config field [here](./app/build.gradle.kts)

## License
This app is licensed under [GPLv3](https://github.com/techmaved/MediaBrowser-for-Spotify/blob/main/LICENSE.md), see LICENSE.md for more information.
