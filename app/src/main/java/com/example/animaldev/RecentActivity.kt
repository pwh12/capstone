package com.example.animalDev


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.animalDev.data.Animal
import com.example.animalDev.databinding.ActivityRecentBinding
import com.example.animalDev.databinding.AnimalListCardviewBinding


class RecentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecentBinding
    private lateinit var databaseHelper: DatabaseHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecentBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}

//https://chat.openai.com/share/c6c816d0-e6e6-4e7b-87f4-19155e84ac75