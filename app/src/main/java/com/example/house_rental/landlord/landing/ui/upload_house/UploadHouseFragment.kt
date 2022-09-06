package com.example.house_rental.landlord.landing.ui.upload_house

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.house_rental.BuildConfig
import com.example.house_rental.MainActivity
import com.example.house_rental.R
import com.example.house_rental.data.model.HouseAddress
import com.example.house_rental.databinding.FragmentUploadHouseBinding
import com.example.house_rental.utils.FileUtils
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


@AndroidEntryPoint
class UploadHouseFragment : Fragment() {
    private val viewModel: UploadHouseViewModel by viewModels()
    private lateinit var binding: FragmentUploadHouseBinding
    private val imageNames = arrayListOf<String>()
    private lateinit var preferences: SharedPreferences
    private val googleApiKey = BuildConfig.GOOGLE_API_KEY

    companion object {
        private const val IMAGES_REQUEST_CODE = 100
        const val AUTOCOMPLETE_REQUEST_CODE = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentUploadHouseBinding.inflate(layoutInflater)
        viewModel.setBinding(binding)
        // Initialize the SDK with the Google Maps Platform API key
//        BuildConfig.GOOGLE_API_KEY
        Places.initialize(requireContext(), googleApiKey)

        binding.apply {
            preferences = requireContext().getSharedPreferences("sub_details", Context.MODE_PRIVATE)

            uploadHouseFragmentAddressTextViewContainer.setOnClickListener {
                val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
//
//            // Start the autocomplete intent.
                val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                    .build(requireContext())
                requireActivity().startActivityFromFragment(this@UploadHouseFragment, intent, AUTOCOMPLETE_REQUEST_CODE)
            }

            uploadHouseFragmentImagesLayout.setOnClickListener {
                val activity = (activity as MainActivity)
                if (activity.isPermissionsAllowed()) {
                    choosePictures()
                } else {
                    activity.askForPermissions()
                }

            }

            uploadHouseFragmentClearImagesLabel.setOnClickListener {
                uploadHouseFragmentSelectedImagesNames.setText(R.string.select_image_s)
                imageNames.clear()
                viewModel.imagePartList.clear()
                viewModel.clearUri()
            }

            uploadHouseFragmentSubmitButton.setOnClickListener {
                viewModel.showLoading()

                if (!viewModel.validateHouseUploadForm()) {
                    viewModel.removeLoading()
                } else {
                    val title = uploadHouseFragmentTitleInput.text.toString()
//                    val address = uploadHouseFragmentAddressInput.text.toString()
                    val price = uploadHouseFragmentPriceInput.text.toString()
                    val description = uploadHouseFragmentDescriptionInput.text.toString()
                    val lat = 1.05623
                    val lng = 1.87654

                    val token = getAuthorization()
                    val ownerId = preferences.getInt("user_id", 0)

//                    val json = """
//                            {
//                                "ownerId": "$ownerId"
//                                "title":"$title",
//                                "address":"$address",
//                                "price":"$price",
//                                "description":"$description",
//                                "lat":"$lat",
//                                "lng":"$lng",
//                            }
//                    """.trimIndent()



                    if (viewModel.houseAddress != null) {

//                        println("""
//                            {
//                                "ownerId": "$ownerId"
//                                "title":"$title",
//                                "address":"${viewModel.houseAddress!!.name}",
//                                "price":"$price",
//                                "description":"$description",
//                                "lat":"${viewModel.houseAddress!!.lat}",
//                                "lng":"${viewModel.houseAddress!!.lng}",
//                            }
//                        """)
                        viewModel.uploadHouse(
                            token,
                            ownerId,
                            viewModel.houseAddress!!.name,
                            viewModel.houseAddress!!.lat,
                            viewModel.houseAddress!!.lng,
                            description,
                            price.toInt(),
                            title,
                            viewModel.imagePartList
                        )
                    } else {

                    }



                }

            }

//            // Initialize the SDK with the Google Maps Platform API key
//            Places.initialize(requireContext(), "AIzaSyDgMyNhWG-wZAi0vmX714J_eZLzUdJ9bSw")
//
//
//
//            // Set the fields to specify which types of place data to
//            // return after the user has made a selection.
//            val fields = listOf(Place.Field.ID, Place.Field.NAME)
//
//            // Start the autocomplete intent.
//            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
//                .build(requireContext())
//            requireActivity().startActivityFromFragment(this@UploadHouseFragment, intent, AUTOCOMPLETE_REQUEST_CODE)
        }

        return binding.root
    }

    private fun getToken(): String {
        return preferences.getString("user_token", "...")!!
    }

    private fun getAuthorization(): String {
        return "Bearer ${getToken()}"
    }

    private fun choosePictures() {
        // For latest versions API LEVEL 19+
        var intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        requireActivity().startActivityFromFragment(this@UploadHouseFragment, intent, IMAGES_REQUEST_CODE);
    }

    fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return false
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == IMAGES_REQUEST_CODE) {
            when (resultCode) {
            Activity.RESULT_OK -> {
                // Use Uri object instead of File to avoid storage permissions
                // imgProfile.setImageURI(fileUri)

                // if multiple images are selected
                if (data?.clipData != null) {
                    var count = data.clipData?.itemCount

                    for (i in 0 until count!!) {
                        var imageUri: Uri = data.clipData?.getItemAt(i)!!.uri
                        viewModel.addUri(imageUri)
                        val path = FileUtils.getPath(imageUri, requireContext())
                        val file = path?.let { File(it) }
                        file?.name?.let { imageNames.add(it) }
                        if (file != null) {
                            viewModel.imagePartList.add(
                                MultipartBody.Part.createFormData(
                                    name = "images",
                                    filename = file.name,
                                    body = file.asRequestBody("image/*".toMediaTypeOrNull())
                                ))
//                            viewModel.imagePartList.add(file.asRequestBody("image/*".toMediaTypeOrNull()))
                        }
                        //     iv_image.setImageURI(imageUri) Here you can assign your Image URI to the ImageViews
                    }
                    viewModel.notifyDataSetChanged()

                } else if (data?.data != null) {
                    // if single image is selected
                    var imageUri: Uri = data.data!!
                    viewModel.addUri(imageUri)
                    val path = FileUtils.getPath(imageUri, requireContext())
                    val file = path?.let { File(it) }
                    file?.name?.let { imageNames.add(it) }
                    if (file != null) {
                        viewModel.imagePartList.add(
                            MultipartBody.Part.createFormData(
                                name = "images",
                                filename = file.name,
                                body = file.asRequestBody("image/*".toMediaTypeOrNull())
                            ))
//                        viewModel.imagePartList.add(file.asRequestBody("image/*".toMediaTypeOrNull()))
                    }
                    viewModel.notifyDataSetChanged()
                    //   iv_image.setImageURI(imageUri) Here you can assign the picked image uri to your imageview

                }
                binding.apply {
                    uploadHouseFragmentSelectedImagesNames.text = imageNames.joinToString((", "))
                }
            }
            else -> {
                // Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
        }

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        Log.i("TAG", "Place: ${place.name}, ${place.id}")
                        Log.i("TAG", "Place LatLng: ${place.latLng}")
                        viewModel.houseAddress = HouseAddress(place.id!!, place.name!!, place.latLng?.latitude!!, place.latLng?.longitude!!)
                        binding.apply {
                            uploadHouseFragmentAddressTextView.text = viewModel.houseAddress!!.name
                        }
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    // TODO: Handle the error.
                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
                        Log.i("TAG", status.statusMessage ?: "")
                    }
                }
                Activity.RESULT_CANCELED -> {
                    // The user canceled the operation.
                }
            }
            return
        }

        super.onActivityResult(requestCode, resultCode, data)
    }
}