package com.example.kolsa

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.C.VOLUME_FLAG_PLAY_SOUND
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.RawResourceDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MediaSource
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Player @OptIn(UnstableApi::class)
@Inject constructor(@ApplicationContext context: Context) {
    val player = ExoPlayer.Builder(context).build()
    init {
        player.prepare()
        player.repeatMode = Player.REPEAT_MODE_OFF
        player.setDeviceVolume(1, VOLUME_FLAG_PLAY_SOUND)
    }

    fun setMedia(url: String){
        val link = "https://ref.test.kolsa.ru/$url"
        val mediaItem = MediaItem.fromUri(link)
        player.setMediaItem(mediaItem)
    }
}