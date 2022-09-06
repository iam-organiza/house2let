package com.example.house_rental.landlord.landing.ui.house_requests

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.house_rental.R
import com.example.house_rental.databinding.FragmentRequestsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RequestsFragment : Fragment() {
    private val viewModel: RequestsViewModel by viewModels()
    private lateinit var binding: FragmentRequestsBinding
    private lateinit var preferences: SharedPreferences
    private lateinit var authorization: String

    companion object {
        fun newInstance() = RequestsFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRequestsBinding.inflate(layoutInflater)
        preferences = requireContext().getSharedPreferences("sub_details", Context.MODE_PRIVATE)
        authorization = getAuthorization()

        println(authorization)

        viewModel.setBinding(binding)

        binding.apply {
            requestsFragmentLoadingTextView.visibility = View.VISIBLE
        }

        viewModel.fetchRequests(authorization)

        return binding.root
    }

    private fun getToken(): String {
        return preferences.getString("user_token", "")!!
    }

    private fun getAuthorization(): String {
        return "Bearer ${getToken()}"
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

}