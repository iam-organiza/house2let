package com.example.house_rental.landlord.landing.ui.house_request

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.house_rental.R
import com.example.house_rental.databinding.FragmentRequestBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RequestFragment : Fragment() {
    private lateinit var binding: FragmentRequestBinding
    private val viewModel: RequestViewModel by viewModels()
    private val args: RequestFragmentArgs by navArgs()
    private lateinit var preferences: SharedPreferences

    companion object {
        fun newInstance() = RequestFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRequestBinding.inflate(layoutInflater)
        preferences = requireContext().getSharedPreferences("sub_details", Context.MODE_PRIVATE)

        val activity = requireActivity()
        viewModel.setBinding(binding)

        val authorization = getAuthorization()
        viewModel.setAuthorization(authorization)
        viewModel.setActivity(activity)
        viewModel.getRequest(authorization, args.requestId)

        return binding.root
    }

    private fun getToken(): String {
        return preferences.getString("user_token", "...")!!
    }

    private fun getAuthorization(): String {
        return "Bearer ${getToken()}"
    }
}