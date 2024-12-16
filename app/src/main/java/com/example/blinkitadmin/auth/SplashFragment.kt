package com.example.blinkitadmin.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.blinkitadmin.R
import com.example.blinkitadmin.Utility
import com.example.blinkitadmin.activity.AdminHomeActivity
import com.example.blinkitadmin.databinding.FragmentSplashBinding

class SplashFragment : Fragment() {
    private lateinit var binding : FragmentSplashBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSplashBinding.inflate(layoutInflater)

        Utility.setStatusAndNavigationBarColor(requireActivity(), requireContext(), R.color.splash_yellow, R.color.splash_yellow)

        Handler(Looper.getMainLooper()).postDelayed({
            if(Utility.isUserLoggedIn(requireContext())) {
                val homeIntent = Intent(requireContext(), AdminHomeActivity::class.java)
                startActivity(homeIntent)
                requireActivity().finish()
            } else {
                findNavController().navigate(R.id.action_splashFragment_to_loginFragment2)
            }
        }, 5000)

        return binding.root
    }

}