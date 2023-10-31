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
package com.example.streamtobluetoothplayer

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaItem.SubtitleConfiguration
import androidx.media3.common.MediaMetadata
import com.google.common.collect.ImmutableList
import io.github.kaaes.spotify.webapi.core.models.TrackSimple

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
  private val spotifyWebApi = SpotifyWebApiService
  private var isInitialized = false
  private const val ROOT_ID = "[rootID]"
  private const val LIKED_SONG_ID = "[albumID]"
  private const val GENRE_ID = "[genreID]"
  private const val ARTIST_ID = "[artistID]"
  private const val ALBUM_PREFIX = "[album]"
  private const val GENRE_PREFIX = "[genre]"
  private const val ARTIST_PREFIX = "[artist]"
  private const val ITEM_PREFIX = "[item]"
  private const val PLAYLIST_ID = "[playlistId]"

  private class MediaItemNode(val item: MediaItem) {
    private val children: MutableList<MediaItem> = ArrayList()

    fun addChild(childID: String) {
      this.children.add(treeNodes[childID]!!.item)
    }

    fun addChildren(childrenIDs: Array<String>) {
      childrenIDs.forEach {childId ->
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
    // create root and folders for album/artist/genre.
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

    treeNodes[ALBUM_PREFIX] = MediaItemNode(
      buildMediaItem(
        title = "Album",
        mediaId = ALBUM_PREFIX,
        isPlayable = false,
        isBrowsable = true,
        mediaType = MediaMetadata.MEDIA_TYPE_FOLDER_ALBUMS
      )
    )

    treeNodes[ROOT_ID]!!.addChildren(arrayOf(LIKED_SONG_ID, PLAYLIST_ID, ALBUM_PREFIX))

    // spotify music obj array list
    //for (i in 0 until mediaList.length()) {
    //  addNodeToTree(mediaList.getJSONObject(i))
    //}

    spotifyWebApi.getLikedTracks { it ->
      it?.items?.iterator()?.forEach {
        addNodeToTree(it.track, LIKED_SONG_ID)
      }
    }

    spotifyWebApi.getPlaylists {playlists ->
      playlists?.items?.iterator()?.forEach {playlist ->
        addBrowsableToTree(playlist.name, playlist.id, PLAYLIST_ID)
        spotifyWebApi.getPlaylistTracks(playlist.id) {playlistTracks ->
          playlistTracks?.items?.iterator()?.forEach {track ->
            // get node and it of it
            addNodeToTree(track.track, PLAYLIST_ID + playlist.id)
          }
        }
      }
    }

    spotifyWebApi.getSavedAlbums {savedAlbumPager ->
      savedAlbumPager?.items?.iterator()?.forEach {savedAlbum  ->
        addBrowsableToTree(savedAlbum.album.name, savedAlbum.album.id, ALBUM_PREFIX)

        savedAlbum.album.tracks.items.iterator().forEach {track ->
          addNodeToTree(track, ALBUM_PREFIX + savedAlbum.album.id)
        }
      }
    }
  }

  private fun addBrowsableToTree(name: String, id: String, parentId: String) {
    // key of such items in tree
    val idInTree = parentId + id

    treeNodes[idInTree] =
      MediaItemNode(
        buildMediaItem(
          title = name,
          mediaId = idInTree,
          isPlayable = false,
          isBrowsable = true,
          mediaType = MediaMetadata.MEDIA_TYPE_PLAYLIST
        )
      )

    titleMap[name.lowercase()] = treeNodes[idInTree]!!
    treeNodes[parentId]!!.addChild(idInTree)
  }

  private fun addNodeToTree(mediaItem: TrackSimple, parentId: String) {
    val id = mediaItem.id
    val title = mediaItem.name
    val artist = mediaItem.artists.joinToString(", ") { artistSimple -> artistSimple.name }
    val genre = ""
    val sourceUri = Uri.parse(mediaItem.uri)
    val imageUri = Uri.parse("")
    // key of such items in tree
    val idInTree = ITEM_PREFIX + id

    treeNodes[idInTree] =
      MediaItemNode(
        buildMediaItem(
          title = title,
          mediaId = idInTree,
          isPlayable = true,
          isBrowsable = false,
          mediaType = MediaMetadata.MEDIA_TYPE_MUSIC,
          album = null,
          artist = artist,
          genre = genre,
          sourceUri = sourceUri,
          imageUri = imageUri
        )
      )

    titleMap[title.lowercase()] = treeNodes[idInTree]!!
    treeNodes[parentId]!!.addChild(idInTree)
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

  fun getRandomItem(): MediaItem {
    var curRoot = getRootItem()
    while (curRoot.mediaMetadata.isBrowsable == true) {
      val children = getChildren(curRoot.mediaId)!!
      curRoot = children.random()
    }
    return curRoot
  }

  fun getItemFromTitle(title: String): MediaItem? {
    return titleMap[title]?.item
  }
}
