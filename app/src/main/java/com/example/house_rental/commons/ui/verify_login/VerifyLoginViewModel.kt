package com.example.house_rental.commons.ui.verify_login

import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.navigation.Navigation
import com.example.house_rental.R
import com.example.house_rental.data.model.LoginPayload
import com.example.house_rental.data.model.LoginResponse
import com.example.house_rental.data.model.LoginResponseX
import com.example.house_rental.data.model.VerifyLoginPayload
import com.example.house_rental.data.repo.MainRepository
import com.example.house_rental.databinding.FragmentLoginBinding
import com.example.house_rental.databinding.FragmentVerifyLoginBinding
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class VerifyLoginViewModel @Inject constructor(@ApplicationContext private val context: Context, private val repository: MainRepository) : ViewModel() {

    private lateinit var binding: FragmentVerifyLoginBinding
    private lateinit var preferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    fun setBinding(_binding: FragmentVerifyLoginBinding) {
        binding = _binding
        preferences = context.getSharedPreferences("sub_details", Context.MODE_PRIVATE)
        editor = preferences.edit()
    }

    fun verifyLogin(verifyLoginPayload: VerifyLoginPayload) {
        println(verifyLoginPayload)
        repository.verifyLogin(verifyLoginPayload).enqueue(
            object: Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {

                    if (response.code().toString()[0].toString().toInt() == 2) {
                        val body = response.body();


                        println(response)
                        if (body != null) {
                            val loginResponse = LoginResponse(
                                data = body.data,
                                message = body.message,
                                status = body.status,
                                success = body.success
                            )

//                            Navigation.findNavController(binding.root).navigate(R.id.action_loginFragment_to_verifyLoginFragment)
                            println("Login Response: ")
                            println(loginResponse)
//
                            val loggedUserData = loginResponse.data
                            editor.putString("user_token", loggedUserData.token)
                            editor.putInt("user_id", loggedUserData.user.id)
                            editor.putString("user_first_name", loggedUserData.user.firstName)
                            editor.putString("user_last_name", loggedUserData.user.lastName)
                            editor.putString("user_role", loggedUserData.user.role)
                            if (editor.commit()) {
                                if (loggedUserData.user.role == "TENANT") {
                                    removeLoading()
                                    resetLoginForm()
                                    clearErrorText(0) {
                                        Navigation.findNavController(binding.root).navigate(R.id.action_verifyLoginFragment_to_tenantLandingFragment)
                                    }
                                }

                                if (loggedUserData.user.role == "LANDLORD") {
                                    removeLoading()
                                    resetLoginForm()
                                    clearErrorText(0) {
                                        Navigation.findNavController(binding.root).navigate(R.id.action_verifyLoginFragment_to_landlordLandingFragment)
                                    }
                                }
                            }
                        } else {
                            displayErrorText("Response body is empty.")
                            clearErrorText()
                            removeLoading()
                        }

                        clearErrorText()
//                        clearErrorText(0) {
//                            val action = LoginFragmentDirections.act
//                            Navigation.findNavController(binding.root).navigate(action)
//                        }
                    } else {
                        println(response)
                        displayErrorText(response.message())
                        clearErrorText()
                        removeLoading()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.d("onFailure", "failed")
                    displayErrorText("Error please try again")
                    clearErrorText()
                    removeLoading()
                }

            }
        )
    }

    fun validateVerifyLoginForm(): Boolean {
        binding.apply {
            val otp = verifyLoginFragmentOtpInput.text.toString().trim()

            if (otp.isEmpty()) {
                displayErrorText("Fill in form correctly.")
                clearErrorText()
                return false
            }

            return true
        }
    }

    fun resetLoginForm() {
        binding.apply {
            verifyLoginFragmentOtpInput.text?.clear()
        }
    }

    fun clearErrorText(delay: Long = 3000, callback: () -> Unit = {}) {
        Handler(Looper.getMainLooper()).postDelayed({
            binding.apply {
                verifyLoginFragmentErrorContainer.visibility = View.GONE
                verifyLoginFragmentErrorText.text = ""
            }
            callback()
        }, delay)
    }

    fun displayErrorText(target: CharSequence?, color: Int = R.color.bg_danger) {
        binding.apply {
            verifyLoginFragmentErrorContainer.visibility = View.VISIBLE

            verifyLoginFragmentErrorContainer.setBackgroundColor(context.getColor(color))
            verifyLoginFragmentErrorText.text = target
        }
    }

    fun showLoading() {
        binding.verifyLoginFragmentSubmitButton.apply {
            text = "Verifying..."
            isEnabled = false
        }
    }

    fun removeLoading() {
        binding.verifyLoginFragmentSubmitButton.apply {
            text = "Verify Login"
            isEnabled = true
        }
    }

}