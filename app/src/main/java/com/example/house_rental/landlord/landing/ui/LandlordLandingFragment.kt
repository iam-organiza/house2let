package com.example.house_rental.landlord.landing.ui

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import android.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import com.example.house_rental.R
import com.example.house_rental.databinding.FragmentLandlordLandingBinding

class LandlordLandingFragment : Fragment() {
    private lateinit var binding: FragmentLandlordLandingBinding
    private lateinit var preferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLandlordLandingBinding.inflate(layoutInflater)
        preferences = requireContext().getSharedPreferences("sub_details", Context.MODE_PRIVATE)
        editor = preferences.edit()

        binding.apply {
//            landlordLandingFragmentWelcomeLabel.text = "Welcome ${args.name}"

            landlordLandingFragmentToolbarIcHambuger.setOnClickListener {
                openDrawer()
            }

            initDrawer()
        }

        return binding.root
    }

    private fun initDrawer() {
        navHostFragment = childFragmentManager.findFragmentById(R.id.landlordLandingFragment_fragment_container_view) as NavHostFragment
        navController = navHostFragment.navController
        binding.landlordLandingFragmentNavView.setupWithNavController(navController)

        binding.apply {
            landlordLandingFragmentNavView.apply {
                setNavigationItemSelectedListener {
                    when (it.itemId) {
                        R.id.landlordLandingFragment_home_link -> {
                            closeDrawer()
                            navController.navigate(R.id.landlordDashboardFragment)
                        }
                        R.id.landlordLandingFragment_upload_house_link-> {
                            closeDrawer()
                            navController.navigate(R.id.uploadHouseFragment)
                        }
                        R.id.landlordLandingFragment_view_requests_link -> {
                            closeDrawer()
                            navController.navigate(R.id.requestsFragment)
                        }
                        R.id.landlordLandingFragment_logout_link -> {
                            closeDrawer()
                            logout()
                        }
                    }
                    true
                }

                getHeaderView(0).findViewById<ImageView>(R.id.menu_component_close_vector)?.setOnClickListener {
                    closeDrawer()
                }
            }
        }
    }

    private fun openDrawer() {
        binding.apply {
            landlordLandingFragmentDrawerLayout.openDrawer(GravityCompat.START)
        }
    }

    private fun closeDrawer() {
        binding.apply {
            landlordLandingFragmentDrawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    private fun logout() {
        editor.clear()
        editor.commit()
        binding.landlordLandingFragmentToolbar.visibility = View.GONE
        navController.navigate(R.id.nav_graph)
    }
}