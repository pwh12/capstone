package com.example.animalDev

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.animalDev.R
import com.example.animalDev.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var tab1: HomeFragment
    private lateinit var tab2: HomeFragment
    private lateinit var tab3: HomeFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        tab1 = HomeFragment()
        tab2 = HomeFragment()
        tab3 = HomeFragment()

        supportFragmentManager.beginTransaction().add(R.id.frameLayout, tab1).commit()

        binding.bottomNavigationView.setOnClickListener {
                item -> when(item.id){
            R.id.home -> replaceFragment(tab1)
            R.id.like -> replaceFragment(tab1)
            R.id.myPage -> replaceFragment(tab1)
        }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }
}