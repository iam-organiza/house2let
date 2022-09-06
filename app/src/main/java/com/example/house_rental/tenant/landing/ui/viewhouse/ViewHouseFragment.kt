package com.example.house_rental.tenant.landing.ui.viewhouse

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.house_rental.R
import com.example.house_rental.databinding.FragmentViewHouseBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewHouseFragment : Fragment(), OnMapReadyCallback {
    private val args: ViewHouseFragmentArgs by navArgs()
    private val viewModel: ViewHouseViewModel by viewModels()
    private lateinit var binding: FragmentViewHouseBinding
    private lateinit var mMap: GoogleMap

    companion object {
        fun newInstance() = ViewHouseFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentViewHouseBinding.inflate(layoutInflater)

        viewModel.setBinding(binding)

        println("House ID:")
        println(args.houseId)
        val map = childFragmentManager.findFragmentById(R.id.viewHouseFragment_house_map) as SupportMapFragment
        viewModel.getHouse(args.houseId) {
            map.getMapAsync(this)
        }

        binding.viewHouseFragmentSendRequestButton.setOnClickListener {
            val action = ViewHouseFragmentDirections.actionViewHouseFragmentToHouseRequestFragment(args.houseId);
            Navigation.findNavController(binding.root).navigate(action)
        }

        return binding.root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        viewModel.house?.lat
        val sydney = LatLng(viewModel.house?.lat!!.toDouble(), viewModel.house?.lng!!.toDouble())
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15f))
    }

}