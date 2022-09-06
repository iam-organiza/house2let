package com.example.house_rental.tenant.landing.ui.viewhouses

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import com.example.house_rental.R
import com.example.house_rental.databinding.FragmentViewHousesBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewHousesFragment : Fragment() {
    private lateinit var binding: FragmentViewHousesBinding
    private val viewModel: ViewHousesViewModel by viewModels()

    companion object {
        fun newInstance() = ViewHousesFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentViewHousesBinding.inflate(layoutInflater)

        viewModel.setBinding(binding)

        binding.apply {
            viewHousesFragmentLoadingTextView.visibility = View.VISIBLE
        }

        viewModel.fetchHouses()
        return binding.root
    }

}