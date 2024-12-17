package com.example.blinkitadmin.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.blinkitadmin.R
import com.example.blinkitadmin.Utility
import com.example.blinkitadmin.databinding.ActivityAdminHomeBinding

class AdminHomeActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAdminHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Utility.setStatusAndNavigationBarColor(this, this, R.color.splash_yellow, R.color.background_light)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView3) as NavHostFragment
        val navController = navHostFragment.navController
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController)
    }
}