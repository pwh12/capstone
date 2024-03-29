package com.example.animalDev

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.animalDev.databinding.FragmentMenuBinding

class MenuFragment : Fragment() {
    private lateinit var binding: FragmentMenuBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMenuBinding.inflate(inflater, container, false)

        binding.menuBtn0.setOnClickListener {
            // 다른 액티비티를 열기 위한 인텐트 생성
            val intent = Intent(activity, RecentActivity::class.java)
            startActivity(intent)
        }

        binding.menuBtn1.setOnClickListener {
            // 다른 액티비티를 열기 위한 인텐트 생성
            val intent = Intent(activity, EduActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }
}



