package com.example.house_rental.landlord.landing.ui.dashboard

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.house_rental.R
import com.example.house_rental.databinding.FragmentLandlordDashboardBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LandlordDashboardFragment : Fragment() {
    private val viewModel: LandlordDashboardViewModel by viewModels()
    private lateinit var binding: FragmentLandlordDashboardBinding
    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLandlordDashboardBinding.inflate(layoutInflater)
        preferences = requireContext().getSharedPreferences("sub_details", Context.MODE_PRIVATE)

        viewModel.setBinding(binding)
        binding.apply {
            val name = "Welcome ${preferences.getString("user_first_name", "")} ${preferences.getString("user_last_name", "")}"
            landlordDashboardFragmentWelcomeLabel.text = name
        }

        println(getAuthorization())
        viewModel.getWallet(getAuthorization())

        return binding.root
    }

    private fun getToken(): String {
        return preferences.getString("user_token", "")!!
    }

    private fun getAuthorization(): String {
        return "Bearer ${getToken()}"
    }


}