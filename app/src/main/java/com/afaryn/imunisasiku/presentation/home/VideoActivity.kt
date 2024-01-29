package com.afaryn.imunisasiku.presentation.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.afaryn.imunisasiku.databinding.ActivityVideoBinding
import com.afaryn.imunisasiku.utils.Constants.PENTINGNYA_IMUNISASI_URI

class VideoActivity : AppCompatActivity() {

    private var _binding: ActivityVideoBinding? = null
    private val binding get() = _binding!!
    private lateinit var player: ExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }
        setUpVideoPlayer()
    }

    private fun setUpVideoPlayer() {
        val videoitem = MediaItem.fromUri(PENTINGNYA_IMUNISASI_URI)
        player = ExoPlayer.Builder(this).build().also {
            it.setMediaItem(videoitem)
            it.prepare()
        }
        binding.playerView.player = player
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
        _binding = null
    }
}