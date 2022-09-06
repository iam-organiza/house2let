package com.example.house_rental.commons.ui.verify_email

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.house_rental.R
import com.example.house_rental.data.model.ResendOTP
import com.example.house_rental.data.model.VerifyEmail
import com.example.house_rental.databinding.FragmentVerifyEmailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VerifyEmailFragment : Fragment() {
    private val args: VerifyEmailFragmentArgs by navArgs()
    private val viewModel: VerifyEmailViewModel by viewModels()
    private lateinit var binding: FragmentVerifyEmailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentVerifyEmailBinding.inflate(layoutInflater)
        val email = args.email
        println(email)
        viewModel.setBinding(binding)
        binding.apply {
            verifyEmailFragmentSubmitButton.setOnClickListener {
                viewModel.showLoading()
                if (!viewModel.validateOTPForm()) {
                    viewModel.removeLoading()
                } else {
                    val otp = verifyEmailFragmentOtpInput.text.toString().trim()

                    val verifyEmail = VerifyEmail(
                        email = email,
                        otp = otp
                    )

                    viewModel.verifyEmail(verifyEmail)
                }
            }

            verifyEmailFragmentResendOtp.setOnClickListener {
                viewModel.showLoading("Sending OTP...")
                viewModel.resendOTP(ResendOTP(email, type = "REGISTER"))
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (args.email.isNotEmpty()) {
            viewModel.displayErrorText("Account Created", R.color.bg_success)
            viewModel.clearErrorText()
        }
    }
}