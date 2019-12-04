package app.hotx.helper

import android.content.Context
import android.net.Uri
import android.view.View
import app.hotx.BuildConfig
import app.hotx.model.PHSmallVideo
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory

/**
 * Created by Grigory Azaryan on 2019-01-31.
 */

class VideoPreviewHelper(val context: Context,
                         val playerView: PlayerView,
                         val video: PHSmallVideo) {
    private val player: ExoPlayer

    init {
        player = ExoPlayerFactory.newSimpleInstance(context, DefaultTrackSelector())
                .also {
                    it.volume = 0f
                    playerView.player = it // Bind to the view.
                }
    }

    fun start() {
        playerView.visibility = View.VISIBLE
//        player.addListener(object : Player.EventListener {
//            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
//                if (playbackState == Player.STATE_READY) playerView.visibility = View.VISIBLE
//            }
//        })
        video.webm.run {
            val uri = Uri.parse(video.webm)
            player.repeatMode = Player.REPEAT_MODE_ONE
            player.prepare(buildMediaSource(uri) as MediaSource)
            player.playWhenReady = true
        }

    }

    private fun buildMediaSource(uri: Uri): ExtractorMediaSource {
        return ExtractorMediaSource.Factory(DefaultHttpDataSourceFactory(BuildConfig.APPLICATION_ID))
                .createMediaSource(uri)
    }

    fun stop() {
        playerView.visibility = View.INVISIBLE
        player.stop()
    }

    fun release() {
        playerView.visibility = View.INVISIBLE
        player.release()
    }

}