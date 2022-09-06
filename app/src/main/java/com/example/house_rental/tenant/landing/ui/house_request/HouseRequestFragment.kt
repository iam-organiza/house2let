package com.example.house_rental.tenant.landing.ui.house_request

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.house_rental.databinding.FragmentHouseRequestBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HouseRequestFragment : Fragment() {
    private lateinit var binding: FragmentHouseRequestBinding
    private val viewModel: HouseRequestViewModel by viewModels()
    private val args: HouseRequestFragmentArgs by navArgs()
    private lateinit var preferences: SharedPreferences

    companion object {
        fun newInstance() = HouseRequestFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHouseRequestBinding.inflate(layoutInflater)
        preferences = requireContext().getSharedPreferences("sub_details", Context.MODE_PRIVATE)

        viewModel.setBinding(binding)
        viewModel.setHouseId(args.houseId)
        viewModel.setAuthorization(getAuthorization())

        println(getAuthorization())
        println("House Request Id: ")
        println(args.houseId)

        return binding.root
    }

    private fun getToken(): String {
        return preferences.getString("user_token", "...")!!
    }

    private fun getAuthorization(): String {
        return "Bearer ${getToken()}"
    }

}