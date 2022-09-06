package com.example.house_rental.commons.ui.verify_login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.house_rental.data.model.VerifyLoginPayload
import com.example.house_rental.databinding.FragmentVerifyLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VerifyLoginFragment : Fragment() {
    private lateinit var binding: FragmentVerifyLoginBinding
    private val viewModel: VerifyLoginViewModel by viewModels()
    private val args: VerifyLoginFragmentArgs by navArgs()

    companion object {
        fun newInstance() = VerifyLoginFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVerifyLoginBinding.inflate(layoutInflater)

        viewModel.setBinding(binding)
//        println(args.email)

        binding.apply {
            verifyLoginFragmentSubmitButton.setOnClickListener {
                viewModel.showLoading()
                if (!viewModel.validateVerifyLoginForm()) {
                    viewModel.removeLoading()
                } else {
                    val otp = verifyLoginFragmentOtpInput.text.toString().trim().lowercase()

                    val loginPayload = VerifyLoginPayload(args.email, otp)

                    viewModel.verifyLogin(loginPayload)
                }
            }
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }
}