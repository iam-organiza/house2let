package com.example.house_rental.tenant.landing.ui.house_request

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModel
import com.example.house_rental.R
import com.example.house_rental.data.model.HouseRequest
import com.example.house_rental.data.model.HouseRequestResponse
import com.example.house_rental.data.repo.MainRepository
import com.example.house_rental.databinding.FragmentHouseRequestBinding
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class HouseRequestViewModel @Inject constructor(@ApplicationContext private val context: Context, private val repository: MainRepository) : ViewModel() {
    private lateinit var binding: FragmentHouseRequestBinding
    private lateinit var preferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var authorization: String
    private var houseId: Int? = null

    fun setBinding(_binding: FragmentHouseRequestBinding) {
        binding = _binding
        preferences = context.getSharedPreferences("sub_details", Context.MODE_PRIVATE)
        editor = preferences.edit()

        binding.apply {
            houseRequestFragmentSubmitButton.setOnClickListener {
                showLoading()
                if (!validateHouseRequestForm()) {
                    removeLoading()
                } else {
                    val housePurpose = houseRequestFragmentHousePurposeInput.text.toString()
                    sendHouseRequest(authorization, houseId!!, housePurpose)
                }
            }
        }
    }

    private fun sendHouseRequest(authorization: String, houseId: Int, housePurpose: String) {
        println("""
            authorization: $authorization, 
            houseId: $houseId, 
            housePurpose: $housePurpose
        """.trimIndent())
        println("ready")
        repository.requestRent(authorization, HouseRequest(houseId, housePurpose)).enqueue(
            object: Callback<HouseRequestResponse> {
                override fun onResponse(
                    call: Call<HouseRequestResponse>,
                    response: Response<HouseRequestResponse>
                ) {
                    println(response)
                    if (response.code().toString()[0].toString().toInt() == 2) {
                        val body = response.body()

                        if (body != null) {
                            val houseRequestResponse = HouseRequestResponse(
                                data = body.data,
                                message = body.message,
                                status = body.status,
                                success = body.success
                            )

                            displayErrorText(houseRequestResponse.message, R.color.bg_success)
                            clearErrorText(5000)
                            removeLoading()
                            resetRequestForm()
                        } else {
                            displayErrorText("Empty response body")
                            clearErrorText()
                            removeLoading()
                        }
                    } else {
                        displayErrorText("You have a pending request on this house already.")
                        clearErrorText(5000)
                        removeLoading()
                    }
                }

                override fun onFailure(call: Call<HouseRequestResponse>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            }
        )
    }

    fun resetRequestForm() {
        binding.apply {
            houseRequestFragmentHousePurposeInput.text?.clear()
        }
    }

    private fun validateHouseRequestForm(): Boolean {
        binding.apply {
            val housePurpose = houseRequestFragmentHousePurposeInput.text.toString()

            if (housePurpose.isEmpty()) {
                displayErrorText("Fill in form correctly.")
                clearErrorText()
                return false
            }

            val user_avatar = preferences.getString("user_avatar", "")
            val user_occupation = preferences.getString("user_occupation", "")
//            val user_source_of_income = preferences.getString("user_source_of_income", "")
            val refLetterString = preferences.getString("ref_letter", "")

            println(refLetterString)

            if (user_avatar.isNullOrEmpty() || user_occupation.isNullOrEmpty() || refLetterString.isNullOrEmpty()) {
                displayErrorText("Complete your profile before making a house request")
                clearErrorText(5000)

//                AlertDialog.Builder(context)
//                    .setTitle("Incomplete profile")
//                    .setMessage("Please complete your profile before making a house request.")
//                    .setPositiveButton(
//                        "Settings"
//                    ) { _, _ ->
//                        println("redirect to settings")
//                    }
//                    .setNegativeButton("Cancel", null)
//                    .show()
                return false
            }

            return true
        }
    }

    fun clearErrorText(delay: Long = 3000, callback: () -> Unit = {}) {
        Handler(Looper.getMainLooper()).postDelayed({
            binding.apply {
                houseRequestFragmentErrorContainer.visibility = View.GONE
                houseRequestFragmentErrorText.text = ""
            }
            callback()
        }, delay)
    }

    fun displayErrorText(target: CharSequence?, color: Int = R.color.bg_danger) {
        binding.apply {
            houseRequestFragmentErrorContainer.visibility = View.VISIBLE

            houseRequestFragmentErrorContainer.setBackgroundColor(context.getColor(color))
            houseRequestFragmentErrorText.text = target
        }
    }

    fun showLoading() {
        binding.houseRequestFragmentSubmitButton.apply {
            text = "Sending..."
            isEnabled = false
        }
    }

    fun removeLoading() {
        binding.houseRequestFragmentSubmitButton.apply {
            text = "Send"
            isEnabled = true
        }
    }

    fun setHouseId(_houseId: Int) {
        houseId = _houseId
    }

    fun setAuthorization(_authorization: String) {
        authorization = _authorization
    }


}