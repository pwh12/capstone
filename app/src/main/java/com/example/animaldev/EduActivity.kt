package com.example.animalDev

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.animalDev.databinding.ActivityEduBinding
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

class EduActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEduBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEduBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.eduBtn0.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://apms.epis.or.kr/home/kor/learn/online/index.do?menuPos=5&idx=&act=&searchValue1=30&searchValue2=&searchValue3=&searchKeyword=&pageIndex=1"))
            startActivity(intent)
        }

        binding.eduBack.setOnClickListener{
            onBackPressed()
        }

        val youtubePlayerView: YouTubePlayerView = binding.youtubePlayerView

        lifecycle.addObserver(youtubePlayerView)

        youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer) {
                val videoId = "iHVSHjpIvxU"
                youTubePlayer.cueVideo(videoId, 0f)

            }
        })
    }
}
