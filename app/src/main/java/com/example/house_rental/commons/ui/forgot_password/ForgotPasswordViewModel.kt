package com.example.house_rental.commons.ui.forgot_password

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.navigation.Navigation
import com.example.house_rental.R
import com.example.house_rental.commons.ui.verify_email.VerifyEmailFragmentDirections
import com.example.house_rental.data.model.RequestReset
import com.example.house_rental.data.model.RequestResetResponse
import com.example.house_rental.data.repo.MainRepository
import com.example.house_rental.databinding.FragmentForgotPasswordBinding
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(@ApplicationContext private val context: Context, private val repository: MainRepository): ViewModel() {
    private lateinit var binding: FragmentForgotPasswordBinding

    fun setBinding(_binding: FragmentForgotPasswordBinding) {
        binding = _binding
    }

    fun showLoading(target: CharSequence? = "Reseting...") {
        binding.forgotPasswordFragmentResetButton.apply {
            text = target
            isEnabled = false
        }
    }

    fun removeLoading() {
        binding.forgotPasswordFragmentResetButton.apply {
            text = "Reset Password"
            isEnabled = true
        }
    }

    fun validateResetPasswordForm(): Boolean {
        binding.apply {
            val email = forgotPasswordFragmentEmailInput.text.toString().trim()

            if (email.isEmpty()) {
                displayErrorText("Please provide email.")
                clearErrorText()
                return false
            }

            return true
        }
    }

    fun clearErrorText(delay: Long = 3000, callback: () -> Unit = {}) {
        Handler(Looper.getMainLooper()).postDelayed({
            binding.apply {
                forgotPasswordFragmentErrorContainer.visibility = View.GONE
                forgotPasswordFragmentErrorText.text = ""
            }
            callback()
        }, delay)
    }

    fun displayErrorText(target: CharSequence?, color: Int = R.color.bg_danger) {
        binding.apply {
            forgotPasswordFragmentErrorContainer.visibility = View.VISIBLE

            forgotPasswordFragmentErrorContainer.setBackgroundColor(context.getColor(color))
            forgotPasswordFragmentErrorText.text = target
        }
    }

    fun requestReset(requestReset: RequestReset) {
            repository.requestReset(requestReset).enqueue(
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
//                            displayErrorText("An OTP has been sent to the provided email", R.color.bg_success)
//                            clearErrorText()
                            clearErrorText(0) {
                                val action = ForgotPasswordFragmentDirections.actionForgotPasswordFragmentToResetPasswordFragment(requestReset.email)
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
            forgotPasswordFragmentEmailInput.text?.clear()
        }
    }

}