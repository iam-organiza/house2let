package com.example.house_rental.commons.ui.reset_password

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.house_rental.R
import com.example.house_rental.commons.ui.verify_email.VerifyEmailFragmentArgs
import com.example.house_rental.data.model.RequestReset
import com.example.house_rental.data.model.Reset
import com.example.house_rental.databinding.FragmentForgotPasswordBinding
import com.example.house_rental.databinding.FragmentResetPasswordBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResetPasswordFragment : Fragment() {
    private val args: ResetPasswordFragmentArgs by navArgs()
    private val viewModel: ResetPasswordViewModel by viewModels()
    private lateinit var binding: FragmentResetPasswordBinding

    companion object {
        fun newInstance() = ResetPasswordFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentResetPasswordBinding.inflate(layoutInflater)

        viewModel.setBinding(binding)
        viewModel.setEmail(args.email)

        binding.apply {
            resetPasswordFragmentResetButton.setOnClickListener {
                viewModel.showLoading()
                if (!viewModel.validateResetPasswordForm()) {
                    viewModel.removeLoading()
                } else {
                    val otp = resetPasswordFragmentOtpInput.text.toString()
                    val password = resetPasswordFragmentNewPasswordInput.text.toString().trim()

                    val reset = Reset(
                        otp = otp,
                        email = args.email,
                        newPassword = password
                    )

                    viewModel.resetPassword(reset)
                }
            }
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (args.email.isNotEmpty()) {
            viewModel.displayErrorText("An OTP has been sent to the provided email", R.color.bg_success)
            viewModel.clearErrorText(5000)
        }
    }

}