package com.example.house_rental.commons.ui.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.house_rental.data.model.UserDetails
import com.example.house_rental.databinding.FragmentSignupBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignupFragment : Fragment() {
    private val viewModel: SignUpViewModel by viewModels()
    private lateinit var binding: FragmentSignupBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignupBinding.inflate(layoutInflater)
        viewModel.setBinding(binding)
        binding.apply {
            signupFragmentLoginLabel.setOnClickListener {
                viewModel.navigateLoginFragment()
            }

            signupFragmentSubmitButton.setOnClickListener {
                viewModel.showLoading()
                if (!viewModel.validateRegistrationForm()) {
                   viewModel.removeLoading()
                } else {
                    val firstname = signupFragmentFirstnameInput.text.toString().trim()
                    val lastname = signupFragmentLastnameInput.text.toString().trim()
                    val email = signupFragmentEmailInput.text.toString().trim()
                    val password = signupFragmentPasswordInput.text.toString().trim()
                    val confirmPassword = signupFragmentConfirmPasswordInput.text.toString().trim()


                    val userDetails = UserDetails(
                        firstName = firstname,
                        lastName = lastname,
                        email = email,
                        password = password
                    )

                    viewModel.registerUser(userDetails)
                }
            }
        }
        return binding.root
    }
}