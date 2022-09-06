package com.example.house_rental.commons.ui.reset_password

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.navigation.Navigation
import com.example.house_rental.R
import com.example.house_rental.commons.ui.forgot_password.ForgotPasswordFragmentDirections
import com.example.house_rental.data.model.RequestResetResponse
import com.example.house_rental.data.model.Reset
import com.example.house_rental.data.repo.MainRepository
import com.example.house_rental.databinding.FragmentResetPasswordBinding
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(@ApplicationContext private val context: Context, private val repository: MainRepository) : ViewModel() {
    private lateinit var binding: FragmentResetPasswordBinding
    private lateinit var email: String

    fun setBinding(_binding: FragmentResetPasswordBinding) {
        binding = _binding
    }

    fun setEmail(_email: String) {
        email = _email
    }

    fun clearErrorText(delay: Long = 3000, callback: () -> Unit = {}) {
        Handler(Looper.getMainLooper()).postDelayed({
            binding.apply {
                resetPasswordFragmentErrorContainer.visibility = View.GONE
                resetPasswordFragmentErrorText.text = ""
            }
            callback()
        }, delay)
    }

    fun displayErrorText(target: CharSequence?, color: Int = R.color.bg_danger) {
        binding.apply {
            resetPasswordFragmentErrorContainer.visibility = View.VISIBLE

            resetPasswordFragmentErrorContainer.setBackgroundColor(context.getColor(color))
            resetPasswordFragmentErrorText.text = target
        }
    }

    fun showLoading(target: CharSequence? = "Reseting...") {
        binding.resetPasswordFragmentResetButton.apply {
            text = target
            isEnabled = false
        }
    }

    fun removeLoading() {
        binding.resetPasswordFragmentResetButton.apply {
            text = "Reset Password"
            isEnabled = true
        }
    }

    fun validateResetPasswordForm(): Boolean {
        binding.apply {
            val otp = resetPasswordFragmentOtpInput.text.toString()
            val password = resetPasswordFragmentNewPasswordInput.text.toString().trim()

            if (otp.isEmpty() || password.isEmpty()) {
                displayErrorText("Please fill in form correctly.")
                clearErrorText()
                return false
            }

            return true
        }
    }

    fun resetPassword(reset: Reset) {
        repository.resetPassword(reset).enqueue(
            object: Callback<RequestResetResponse> {
                override fun onResponse(
                    call: Call<RequestResetResponse>,
                    response: Response<RequestResetResponse>
                ) {
                    Log.d("onResponse", ":")
                    println(response)
                    if (response.code().toString()[0].toString().toInt() == 2) {
                        resetPasswordForm()
                        removeLoading()
//                        displayErrorText(response.message(), R.color.bg_success)
//                        clearErrorText()
                        clearErrorText(0) {
                            val action = ResetPasswordFragmentDirections.actionResetPasswordFragmentToLoginFragment(passwordReset = "Password reset successful")
                            Navigation.findNavController(binding.root).navigate(action)
                        }
                    } else {
                        displayErrorText(response.message())
                        clearErrorText()
                        removeLoading()
                    }
                }

                override fun onFailure(call: Call<RequestResetResponse>, t: Throwable) {
                    Log.d("onFailure", "failed")
                    displayErrorText("Error please try again")
                    clearErrorText()
                    removeLoading()
                }
            }
        )
    }

    fun resetPasswordForm() {
        binding.apply {
            resetPasswordFragmentOtpInput.text?.clear()
            resetPasswordFragmentNewPasswordInput.text?.clear()
        }
    }
}