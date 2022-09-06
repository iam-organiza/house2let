package com.example.house_rental.commons.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.house_rental.R
import com.example.house_rental.databinding.FragmentWelcomeBinding

class WelcomeFragment : Fragment() {
    private lateinit var binding: FragmentWelcomeBinding
    private lateinit var preferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentWelcomeBinding.inflate(layoutInflater)
        preferences = requireContext().getSharedPreferences("sub_details", Context.MODE_PRIVATE)

        if (preferences.contains("user_token")) {
            val role = preferences.getString("user_role", "")

            if (role == "TENANT") {
                findNavController().navigate(R.id.action_welcomeFragment_to_tenantLandingFragment)
            } else {
                findNavController().navigate(R.id.action_welcomeFragment_to_landlordLandingFragment)
            }
        } else {
            binding.apply {
                welcomeFragmentUsertypeTenantButton.setOnClickListener {
                    navigateToLogin()
                }

                welcomeFragmentUsertypeLandlordButton.setOnClickListener {
                    navigateToLogin()
                }
            }
        }

        return binding.root
    }

    private fun navigateToLogin() {
        Navigation.findNavController(binding.root).navigate(R.id.action_welcomeFragment_to_loginFragment)
    }
}