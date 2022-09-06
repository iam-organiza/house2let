package com.example.house_rental.commons.ui.login

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.house_rental.R
import com.example.house_rental.data.model.LoginPayload
import com.example.house_rental.data.model.UserDetails
import com.example.house_rental.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private val args: LoginFragmentArgs by navArgs()
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var binding: FragmentLoginBinding
    private lateinit var preferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(layoutInflater)
        viewModel.setBinding(binding)
        preferences = requireContext().getSharedPreferences("sub_details", Context.MODE_PRIVATE)

        if (preferences.contains("user_token")) {
            val role = preferences.getString("user_role", "")

            if (role == "TENANT") {
                findNavController().navigate(R.id.action_loginFragment_to_tenantLandingFragment)
            } else {
                findNavController().navigate(R.id.action_loginFragment_to_landlordLandingFragment)
            }
        } else {
            binding.apply {

                loginFragmentSignupLabel.setOnClickListener {
                    navigateSignupFragment()
                }

                loginFragmentForgotPasswordLabel.setOnClickListener {
                    findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
                }

                loginFragmentSubmitButton.setOnClickListener {
                    viewModel.showLoading()
                    if (!viewModel.validateLoginForm()) {
                        viewModel.removeLoading()
                    } else {
                        val email = loginFragmentEmailInput.text.toString().trim().lowercase()
                        val password = loginFragmentPasswordInput.text.toString().trim()

                        val loginPayload = LoginPayload(email, password)

                        viewModel.loginUser(loginPayload)
                    }
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!args.emailVerified.isNullOrEmpty()) {
            viewModel.displayErrorText("Email verified.", R.color.bg_success)
            viewModel.clearErrorText(5000)
        }

        if (!args.passwordReset.isNullOrEmpty()) {
            viewModel.displayErrorText(args.passwordReset, R.color.bg_success)
            viewModel.clearErrorText(5000)
        }
    }

    private fun navigateSignupFragment() {
        Navigation.findNavController(binding.root).navigate(R.id.action_loginFragment_to_signupFragment)
    }


}