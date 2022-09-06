package com.example.house_rental.tenant.landing.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import com.example.house_rental.R
import com.example.house_rental.databinding.FragmentTenantLandingBinding

class TenantLandingFragment : Fragment() {
    private lateinit var binding: FragmentTenantLandingBinding
    private lateinit var preferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTenantLandingBinding.inflate(layoutInflater)
        preferences = requireContext().getSharedPreferences("sub_details", Context.MODE_PRIVATE)
        editor = preferences.edit()
//        val name = "${preferences.getString("user_first_name", "")} ${preferences.getString("user_last_name", "")}"

//        binding.apply {
//            tenantLandingFragmentWelcomeLabel.text = "Welcome $name"
//
//
//            tenantLandingFragmentLogoutBtn.setOnClickListener {
//                editor.clear()
//                editor.commit()
//
//                findNavController().navigate(R.id.nav_graph)
//            }
//        }

        binding.apply {
            tenantLandingFragmentToolbarIcHambuger.setOnClickListener {
                openDrawer()
            }

            initDrawer()
        }

        return binding.root
    }

    private fun initDrawer() {
        navHostFragment = childFragmentManager.findFragmentById(R.id.tenantLandingFragment_fragment_container_view) as NavHostFragment
        navController = navHostFragment.navController
        binding.tenantLandingFragmentNavView.setupWithNavController(navController)

        binding.apply {
            tenantLandingFragmentNavView.apply {
                setNavigationItemSelectedListener {
                    when (it.itemId) {
                        R.id.tenantLandingFragment_home_link -> {
                            closeDrawer()
                            navController.navigate(R.id.tenantDashboardFragment)
                        }
                        R.id.tenantLandingFragment_view_houses_link -> {
                            closeDrawer()
                            navController.navigate(R.id.viewHousesFragment)
                        }
                        R.id.tenantLandingFragment_settings_link -> {
                            closeDrawer()
                            navController.navigate(R.id.settingsFragment)
                        }
                        R.id.tenantLandingFragment_logout_link -> {
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
            tenantLandingFragmentDrawerLayout.openDrawer(GravityCompat.START)
        }
    }

    private fun closeDrawer() {
        binding.apply {
            tenantLandingFragmentDrawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    private fun logout() {
        editor.clear()
        editor.commit()
        binding.tenantLandingFragmentToolbar.visibility = View.GONE
        navController.navigate(R.id.nav_graph)
    }
}