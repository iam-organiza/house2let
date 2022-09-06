package com.example.house_rental.landlord.landing.ui.upload_house

import android.content.ClipDescription
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.example.house_rental.R
import com.example.house_rental.data.model.HouseAddress
import com.example.house_rental.data.model.HouseDetailsPayload
import com.example.house_rental.data.model.HouseUploadResponse
import com.example.house_rental.data.model.LoginResponse
import com.example.house_rental.data.repo.MainRepository
import com.example.house_rental.databinding.FragmentUploadHouseBinding
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Part
import java.io.File
import javax.inject.Inject

@HiltViewModel
class UploadHouseViewModel @Inject constructor(@ApplicationContext private val context: Context, private val repository: MainRepository): ViewModel() {
    private lateinit var binding: FragmentUploadHouseBinding
    private val uriList = arrayListOf<Uri>()
    private val adapter = SelectedImagesAdapter(uriList)
    val imagePartList = arrayListOf<MultipartBody.Part>()
    var houseAddress: HouseAddress? = null
//    val imagePartList = arrayListOf<RequestBody>()

    fun setBinding(_binding: FragmentUploadHouseBinding) {
        binding = _binding
        binding.apply {
            uploadHouseFragmentSelectedImagesRecyclerView.layoutManager = GridLayoutManager(context, 3)
            uploadHouseFragmentSelectedImagesRecyclerView.adapter = adapter
        }
    }

    fun addUri(uri: Uri) {
        uriList.add(uri)
    }

    fun clearUri() {
        uriList.clear()
        notifyDataSetChanged()
    }

    fun notifyDataSetChanged() {
        adapter.notifyDataSetChanged()
    }

    fun showLoading() {
        binding.uploadHouseFragmentSubmitButton.apply {
            text = "Uploading..."
            isEnabled = false
        }
    }

    fun removeLoading() {
        binding.uploadHouseFragmentSubmitButton.apply {
            text = "Upload"
            isEnabled = true
        }
    }

    private fun clearErrorText(delay: Long = 3000, callback: () -> Unit = {}) {
        Handler(Looper.getMainLooper()).postDelayed({
            binding.apply {
                uploadHouseFragmentErrorContainer.visibility = View.GONE
                uploadHouseFragmentErrorText.text = ""
            }
            callback()
        }, delay)
    }

    private fun displayErrorText(target: CharSequence?, color: Int = R.color.bg_danger) {
        binding.apply {
            uploadHouseFragmentErrorContainer.visibility = View.VISIBLE

            uploadHouseFragmentErrorContainer.setBackgroundColor(context.getColor(color))
            uploadHouseFragmentErrorText.text = target
        }
    }

    fun resetHouseUploadForm() {
        binding.apply {
            uploadHouseFragmentTitleInput.text?.clear()
            houseAddress = null
            uploadHouseFragmentAddressTextView.setText(R.string.enter_house_address)
//            uploadHouseFragmentAddressInput.text?.clear()
            uploadHouseFragmentPriceInput.text?.clear()
            uploadHouseFragmentDescriptionInput.text?.clear()
            uploadHouseFragmentClearImagesLabel.performClick()
        }
    }

    fun validateHouseUploadForm(): Boolean {
        binding.apply {
            val title = uploadHouseFragmentTitleInput.text.toString().trim().lowercase()
//            val address = uploadHouseFragmentAddressInput.text.toString().trim()
            val price = uploadHouseFragmentPriceInput.text.toString().trim()
            val description = uploadHouseFragmentDescriptionInput.text.toString().trim()

            if (title.isEmpty() || price.isEmpty() || description.isEmpty() || imagePartList.size == 0) {
                displayErrorText("Fill in form correctly.")
                clearErrorText()
                return false
            }

            if (!price.isDigitsOnly()) {
                displayErrorText("Only digits allowed for price.")
                clearErrorText()
                return false
            }

            return true
        }
    }

    fun uploadHouse(
        authorization: String,
        ownerId: Int,
        address: String,
        lat: Double,
        lng: Double,
        description: String,
        price: Int,
        title: String,
        images: ArrayList<MultipartBody.Part>
    ) {
        repository.uploadHouse(
            authorization,
            ownerId,
            address,
            lat,
            lng,
            description,
            price,
            title,
            images
        ).enqueue(
            object: Callback<HouseUploadResponse>{
                override fun onResponse(
                    call: Call<HouseUploadResponse>,
                    response: Response<HouseUploadResponse>
                ) {

                    if (response.code().toString()[0].toString().toInt() == 2) {
                        val houseUploadResponse = response.body()
                        displayErrorText("Uploaded Successful.", R.color.bg_success)
                        clearErrorText()
                        resetHouseUploadForm()
                        removeLoading()
                    } else {
                        displayErrorText(response.message())
                        clearErrorText()
                        removeLoading()
                    }
                }

                override fun onFailure(call: Call<HouseUploadResponse>, t: Throwable) {
                    Log.d("onFailure", "failed")
                    displayErrorText("Error please try again")
                    clearErrorText()
                    removeLoading()
                }

            }
        )
    }
}
