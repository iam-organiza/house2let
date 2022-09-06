package com.example.house_rental.commons.ui.signup

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Patterns
import android.view.View
import androidx.compose.ui.res.colorResource
import androidx.lifecycle.ViewModel
import androidx.navigation.Navigation
import com.example.house_rental.R
import com.example.house_rental.data.model.RegistrationResponse
import com.example.house_rental.data.model.UserDetails
import com.example.house_rental.data.repo.MainRepository
import com.example.house_rental.databinding.FragmentSignupBinding
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(@ApplicationContext private val context: Context, private val repository: MainRepository): ViewModel() {
    private lateinit var binding: FragmentSignupBinding

    fun setBinding(_binding: FragmentSignupBinding) {
        binding = _binding
    }

    fun registerUser(userDetails: UserDetails) {
        repository.registerUser(userDetails).enqueue(
            object: Callback<ResponseBody>{
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    Log.d("onResponse", ":")
                    if (response.code().toString()[0].toString().toInt() == 2) {
                        removeLoading()
                        resetRegistrationForm()

                        clearErrorText(0) {
                            val action = SignupFragmentDirections.actionSignupFragmentToVerifyEmailFragment(userDetails.email)
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

    fun showLoading() {
        binding.signupFragmentSubmitButton.apply {
            text = "Creating..."
            isEnabled = false
        }
    }

    fun removeLoading() {
        binding.signupFragmentSubmitButton.apply {
            text = "Create Account"
            isEnabled = true
        }
    }

    fun navigateLoginFragment() {
        Navigation.findNavController(binding.root).navigate(R.id.action_signupFragment_to_loginFragment)
    }

    fun resetRegistrationForm() {
        binding.apply {
            signupFragmentFirstnameInput.text?.clear()
            signupFragmentLastnameInput.text?.clear()
            signupFragmentEmailInput.text?.clear()
            signupFragmentPasswordInput.text?.clear()
            signupFragmentConfirmPasswordInput.text?.clear()
        }
    }

    fun validateRegistrationForm(): Boolean {
        binding.apply {
            val firstname = signupFragmentFirstnameInput.text.toString().trim()
            val lastname = signupFragmentLastnameInput.text.toString().trim()
            val email = signupFragmentEmailInput.text.toString().trim().lowercase()
            val password = signupFragmentPasswordInput.text.toString().trim()
            val confirmPassword = signupFragmentConfirmPasswordInput.text.toString().trim()

            if (firstname.isEmpty() || lastname.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                displayErrorText("Fill in form correctly.")
                clearErrorText()
                return false
            }

            if (password != confirmPassword) {
                displayErrorText("Passwords do not match.")
                clearErrorText()
                return false
            }

            if (!validateEmail(email)) {
                displayErrorText("Invalid email.")
                clearErrorText()
                return false
            }

            if (!validatePassword(password)) {
                displayErrorText("Password must be at least 8 characters in length with 1 uppercase, 1 lowercase, 1 number and 1 symbol")
                clearErrorText()
                return false
            }

            return true
        }
    }

    fun validateFirstName(firstname: String) {
        if (firstname.length <= 3) {

        }
    }

    fun validateLastName() {

    }

    private fun validateEmail(target: CharSequence?): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(target!!).matches()
    }

    private fun validatePassword(target: CharSequence?): Boolean {
        if (target != null) {
            return target.matches(Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&\\-+=().])(?=\\S+$).{8,20}$"))
        }

        return false
    }

    private fun clearErrorText(delay: Long = 3000, callback: () -> Unit = {}) {
        Handler(Looper.getMainLooper()).postDelayed({
            binding.apply {
                signupFragmentErrorContainer.visibility = View.GONE
                signupFragmentErrorText.text = ""
            }
            callback()
        }, delay)
    }

    private fun displayErrorText(target: CharSequence?, color: Int = R.color.bg_danger) {
        binding.apply {
            signupFragmentErrorContainer.visibility = View.VISIBLE

            signupFragmentErrorContainer.setBackgroundColor(context.getColor(color))
            signupFragmentErrorText.text = target
        }
    }

}