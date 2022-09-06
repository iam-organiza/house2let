package com.example.house_rental.commons.ui.forgot_password

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.house_rental.data.model.RequestReset
import com.example.house_rental.data.model.ResendOTP
import com.example.house_rental.data.model.VerifyEmail
import com.example.house_rental.databinding.FragmentForgotPasswordBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordFragment : Fragment() {
    private val viewModel: ForgotPasswordViewModel by viewModels()
    private lateinit var binding: FragmentForgotPasswordBinding

    companion object {
        fun newInstance() = ForgotPasswordFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentForgotPasswordBinding.inflate(layoutInflater)

        viewModel.setBinding(binding)

        binding.apply {
            forgotPasswordFragmentResetButton.setOnClickListener {
                viewModel.showLoading()
                if (!viewModel.validateResetPasswordForm()) {
                    viewModel.removeLoading()
                } else {
                    val email = forgotPasswordFragmentEmailInput.text.toString().trim()

                    val requestReset = RequestReset(
                        email = email
                    )

                    viewModel.requestReset(requestReset)
                }
            }
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

}