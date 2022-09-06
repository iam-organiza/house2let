package com.example.house_rental.commons.ui.verify_email

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.navigation.Navigation
import com.example.house_rental.R
import com.example.house_rental.data.model.RegistrationResponse
import com.example.house_rental.data.model.ResendOTP
import com.example.house_rental.data.model.VerifyEmail
import com.example.house_rental.data.repo.MainRepository
import com.example.house_rental.databinding.FragmentSignupBinding
import com.example.house_rental.databinding.FragmentVerifyEmailBinding
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class VerifyEmailViewModel @Inject constructor(@ApplicationContext private val context: Context, private val repository: MainRepository) : ViewModel() {
    private lateinit var binding: FragmentVerifyEmailBinding

    fun setBinding(_binding: FragmentVerifyEmailBinding) {
        binding = _binding
    }

    fun resendOTP(resendOTP: ResendOTP) {
        repository.resendOTP(resendOTP).enqueue(
            object: Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    Log.d("onResponse", ":")
                    println(response)
                    if (response.code().toString()[0].toString().toInt() == 2) {
                        displayErrorText("OTP Sent.", R.color.bg_success)
                        clearErrorText()
                        removeLoading()
                    } else {
                        displayErrorText(response.message())
                        clearErrorText()
                        removeLoading()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.d("onFailure", "failed")
                    displayErrorText("Error please try again")
                    clearErrorText()
                    removeLoading()
                }
            }
        )
    }

    fun verifyEmail(verifyEmail: VerifyEmail) {
        repository.verifyEmail(verifyEmail).enqueue(
            object: Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    Log.d("onResponse", ":")
                    println(response)
                    if (response.code().toString()[0].toString().toInt() == 2) {
                        resetOTPForm()
                        removeLoading()
                        clearErrorText(0) {
                            val action = VerifyEmailFragmentDirections.actionVerifyEmailFragmentToLoginFragment("Email verified.")
                            Navigation.findNavController(binding.root).navigate(action)
                        }
                    } else {
                        displayErrorText(response.message())
                        clearErrorText()
                        removeLoading()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.d("onFailure", "failed")
                    displayErrorText("Error please try again")
                    clearErrorText()
                    removeLoading()
                }
            }
        )
    }

    fun resetOTPForm() {
        binding.apply {
            verifyEmailFragmentOtpInput.text?.clear()
        }
    }

    fun validateOTPForm(): Boolean {
        binding.apply {
            val otp = verifyEmailFragmentOtpInput.text.toString().trim()

            if (otp.isEmpty()) {
                displayErrorText("Please provide an OTP.")
                clearErrorText()
                return false
            }

            if (!validateOTP(otp)) {
                displayErrorText("Invalid OTP.")
                clearErrorText()
                return false
            }

            return true
        }
    }

    fun showLoading(target: CharSequence? = "Verifying...") {
        binding.verifyEmailFragmentSubmitButton.apply {
            text = target
            isEnabled = false
        }
    }

    fun removeLoading() {
        binding.verifyEmailFragmentSubmitButton.apply {
            text = "Verify Email"
            isEnabled = true
        }
    }

    private fun validateOTP(target: CharSequence?): Boolean {
        if (target != null) {
            return target.matches(Regex("^[0-9]*\$"))
        }

        return false
    }

    fun clearErrorText(delay: Long = 3000, callback: () -> Unit = {}) {
        Handler(Looper.getMainLooper()).postDelayed({
            binding.apply {
                verifyEmailFragmentErrorContainer.visibility = View.GONE
                verifyEmailFragmentErrorText.text = ""
            }
            callback()
        }, delay)
    }

    fun displayErrorText(target: CharSequence?, color: Int = R.color.bg_danger) {
        binding.apply {
            verifyEmailFragmentErrorContainer.visibility = View.VISIBLE

            verifyEmailFragmentErrorContainer.setBackgroundColor(context.getColor(color))
            verifyEmailFragmentErrorText.text = target
        }
    }

}