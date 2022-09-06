package com.example.house_rental.tenant.landing.ui.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.example.house_rental.R
import com.example.house_rental.data.model.UpdateProfileResponse
import com.example.house_rental.data.repo.MainRepository
import com.example.house_rental.databinding.FragmentSettingsBinding
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.net.URL
import javax.inject.Inject


@HiltViewModel
class SettingsViewModel @Inject constructor(@ApplicationContext private val context: Context, private val repository: MainRepository) : ViewModel() {
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var preferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private var picture: MultipartBody.Part? = null
    private var refLetter: MultipartBody.Part? = null
    private lateinit var authorization: String

    fun setBinding(_binding: FragmentSettingsBinding) {
        binding = _binding
        preferences = context.getSharedPreferences("sub_details", Context.MODE_PRIVATE)
        editor = preferences.edit()

        binding.apply {
            val avatar = preferences.getString("user_avatar", "")

            if (!avatar.isNullOrEmpty()) {
                val url = URL(avatar)

//                val path = FileUtils.getPath(uri, context)!!
                val file = File(url.toURI().toString())
                println("file.name")
                println(file.name)

                picture = MultipartBody.Part.createFormData(
                    name = "image",
                    filename = file.name,
                    body = file.asRequestBody("image/*".toMediaTypeOrNull())
                )

                settingsFragmentPlaceholderImage.visibility = View.VISIBLE
                Glide.with(context)
                    .load(avatar)
                    .fitCenter()
                    .skipMemoryCache(true)//for caching the image url in case phone is offline
                    .into(settingsFragmentPlaceholderImage)
            }

            val firstname = preferences.getString("user_first_name", "")
            val lastname = preferences.getString("user_last_name", "")
            val occupation = preferences.getString("user_occupation", "")
//            val sourceOfIncome = preferences.getString("user_source_of_income", "")
            val refLetterString = preferences.getString("ref_letter", "")

            settingsFragmentFirstnameInput.setText(firstname?.trim('"'))
            settingsFragmentLastnameInput.setText(lastname?.trim('"'))
            settingsFragmentOccupationInput.setText(occupation?.trim('"'))
//            settingsFragmentSourceOfIncomeInput.setText(sourceOfIncome?.trim('"'))
            println("refLetterString")
            println(refLetterString)
            settingsFragmentSelectedLetterOfReferenceName.setText(refLetterString)

            settingsFragmentSubmitButton.setOnClickListener {
                showLoading()
                if (!validateProfileForm()) {
                    removeLoading()
                } else {
                    val updatedFirstName = settingsFragmentFirstnameInput.text.toString()
                    val updatedLastName = settingsFragmentLastnameInput.text.toString()
                    val updatedOccupation = settingsFragmentOccupationInput.text.toString()
//                    val updatedSourceOfIncome = settingsFragmentSourceOfIncomeInput.text.toString()
                    updateProfile(authorization, picture!!, updatedOccupation, refLetter!!, updatedFirstName, updatedLastName)
                }
            }
        }
    }

    fun setAuthorization(target: String) {
        authorization = target
    }

    private fun updateProfile(authorization: String, image: MultipartBody.Part, occupation: String, file: MultipartBody.Part, firstName: String, lastName: String) {
        println("""
            "authorization": $authorization,
            "image": $image,
            "occupation": $occupation,
            "ref_letter": $file,
            "firstName": $firstName,
            "lastName": $lastName,
        """.trimIndent())

        repository.updateProfile(authorization, image, occupation, file, firstName, lastName).enqueue(
            object: Callback<UpdateProfileResponse> {
                override fun onResponse(
                    call: Call<UpdateProfileResponse>,
                    response: Response<UpdateProfileResponse>
                ) {
                    if (response.code().toString()[0].toString().toInt() == 2) {
                        val body = response.body();

                        if (body != null) {
                            val loginResponse = UpdateProfileResponse(
                                data = body.data,
                                message = body.message,
                                status = body.status,
                                success = body.success
                            )

                            println("Login Response: ")
                            println(loginResponse)

                            val updatedUserData = loginResponse.data
                            println("updatedUserData: ")
                            println(updatedUserData)

                            editor.putString("user_first_name", updatedUserData.user.firstName)
                            editor.putString("user_last_name", updatedUserData.user.lastName)
                            editor.putString("user_avatar", updatedUserData.UserProfile.image)
                            editor.putString("user_occupation", updatedUserData.UserProfile.occupation)
                            editor.putString("ref_letter", updatedUserData.user.refLetter)
//                            editor.putString("user_source_of_income", updatedUserData.UserProfile.incomeSource)
                            editor.commit()
                            displayErrorText("Saved.", R.color.bg_success)
//                            clearErrorText()
                            removeLoading()

                        } else {
                            displayErrorText("Response body is empty.")
//                            clearErrorText()
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

                override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                    Log.d("onFailure", "failed")
                    displayErrorText("Error please try again")
                    clearErrorText()
                    removeLoading()
                }

            }
        )
    }

    fun setPicture(file: MultipartBody.Part?) {
        picture = file
    }

    fun setFile(file: MultipartBody.Part?) {
        refLetter = file
    }

    private fun validateProfileForm(): Boolean {
        binding.apply {
            val firstname = settingsFragmentFirstnameInput.text.toString().trim()
            val lastname = settingsFragmentLastnameInput.text.toString().trim()
            val occupation = settingsFragmentOccupationInput.text.toString().trim()
//            val sourceOfIncome = settingsFragmentSourceOfIncomeInput.text.toString().trim()

            if (firstname.isEmpty() || lastname.isEmpty() || occupation.isEmpty() || refLetter == null || picture == null) {
                displayErrorText("Fill in form correctly.")
                clearErrorText()
                return false
            }

            return true
        }
    }

    fun clearErrorText(delay: Long = 3000, callback: () -> Unit = {}) {
        Handler(Looper.getMainLooper()).postDelayed({
            binding.apply {
                settingsFragmentErrorContainer.visibility = View.GONE
                settingsFragmentErrorText.text = ""
            }
            callback()
        }, delay)
    }

    fun displayErrorText(target: CharSequence?, color: Int = R.color.bg_danger) {
        binding.apply {
            settingsFragmentErrorContainer.visibility = View.VISIBLE

            settingsFragmentErrorContainer.setBackgroundColor(context.getColor(color))
            settingsFragmentErrorText.text = target
        }
    }

    fun showLoading() {
        binding.settingsFragmentSubmitButton.apply {
            text = "Saving..."
            isEnabled = false
        }
    }

    fun removeLoading() {
        binding.settingsFragmentSubmitButton.apply {
            text = "Save"
            isEnabled = true
        }
    }

}