package com.example.house_rental.tenant.landing.ui.settings

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.house_rental.MainActivity
import com.example.house_rental.databinding.FragmentSettingsBinding
import com.example.house_rental.utils.FileUtils
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream


@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var preferences: SharedPreferences
    private val viewModel: SettingsViewModel by viewModels()

    companion object {
        const val IMAGE_REQUEST_CODE = 300
        const val FILE_REQUEST_CODE = 250
        fun newInstance() = SettingsFragment()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(layoutInflater)
        preferences = requireContext().getSharedPreferences("sub_details", Context.MODE_PRIVATE)


        viewModel.setBinding(binding)
        viewModel.setAuthorization(getAuthorization())
        binding.apply {
            settingsFragmentImageLayout.setOnClickListener {
                val activity = (activity as MainActivity)
                if (activity.isPermissionsAllowed()) {
                    choosePicture()
                } else {
                    activity.askForPermissions()
                }
            }

            settingsFragmentLetterOfReferenceLayout.setOnClickListener {
                val activity = (activity as MainActivity)
                if (activity.isPermissionsAllowed()) {
                    chooseFile()
                } else {
                    activity.askForPermissions()
                }
            }

            settingsFragmentClearImage.setOnClickListener {
                settingsFragmentSelectedImageName.text = "Select image"
                settingsFragmentPlaceholderImage.visibility = View.GONE
                viewModel.setPicture(null)
            }

            settingsFragmentClearLetterOfReference.setOnClickListener {
                settingsFragmentSelectedLetterOfReferenceName.text = "Select file"
                viewModel.setFile(null)
            }
        }

        return binding.root
    }

    private fun getToken(): String {
        return preferences.getString("user_token", "...")!!
    }

    private fun getAuthorization(): String {
        return "Bearer ${getToken()}"
    }

    private fun choosePicture() {
        // For latest versions API LEVEL 19+
        var intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        // intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        requireActivity().startActivityFromFragment(this@SettingsFragment, intent,
            IMAGE_REQUEST_CODE
        );
    }

    private fun chooseFile() {
        // For latest versions API LEVEL 19+
        var intent = Intent(Intent.ACTION_GET_CONTENT)
        // intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "application/*"
        requireActivity().startActivityFromFragment(this@SettingsFragment, intent,
            FILE_REQUEST_CODE
        )
    }

    @Throws(IOException::class)
    fun getFile(context: Context, uri: Uri): File? {
        val destinationFilename =
            File(context.filesDir.path + File.separatorChar + queryName(context, uri))
        try {
            context.contentResolver.openInputStream(uri).use { ins ->
                if (ins != null) {
                    createFileFromStream(
                        ins,
                        destinationFilename
                    )
                }
            }
        } catch (ex: Exception) {
            ex.message?.let { Log.e("Save File", it) }
            ex.printStackTrace()
        }
        return destinationFilename
    }

    private fun createFileFromStream(ins: InputStream, destination: File?) {
        try {
            FileOutputStream(destination).use { os ->
                val buffer = ByteArray(4096)
                var length: Int
                while (ins.read(buffer).also { length = it } > 0) {
                    os.write(buffer, 0, length)
                }
                os.flush()
            }
        } catch (ex: Exception) {
            ex.message?.let { Log.e("Save File", it) }
            ex.printStackTrace()
        }
    }

    private fun queryName(context: Context, uri: Uri): String {
        val returnCursor = context.contentResolver.query(uri, null, null, null, null)!!
        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val name = returnCursor.getString(nameIndex)
        returnCursor.close()
        return name
    }

    @SuppressLint("Range")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == IMAGE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    // Use Uri object instead of File to avoid storage permissions
                    // imgProfile.setImageURI(fileUri)

                    // if multiple images are selected
                    if (data?.data != null) {
                        // if single image is selected
                        var imageUri: Uri = data.data!!
                        val path = FileUtils.getPath(imageUri, requireContext())
                        val file = path?.let { File(it) }
                        file?.name?.let { binding.settingsFragmentSelectedImageName.text = it }
                        if (file != null) {
                            viewModel.setPicture(MultipartBody.Part.createFormData(
                                name = "image",
                                filename = file.name,
                                body = file.asRequestBody("image/*".toMediaTypeOrNull())
                            ))

                            binding.settingsFragmentPlaceholderImage.visibility = View.VISIBLE
                            context?.let {
                                Glide.with(it)
                                    .load(file) // Uri of the picture
                                    .into(binding.settingsFragmentPlaceholderImage);
                            }

                        }
                    }
                }
                else -> {
                    // Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
                }
            }
        }

        if (requestCode == FILE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    // Get the Uri of the selected file
                    val uri: Uri = data?.data!!

                    val pdf = getFile(requireContext(), uri)!!

                    binding.settingsFragmentSelectedLetterOfReferenceName.text = pdf.name
                    viewModel.setFile(MultipartBody.Part.createFormData(
                        name = "ref_letter",
                        filename = pdf.name,
                        body = pdf.asRequestBody("application/*".toMediaTypeOrNull())
                    ))
                }
                else -> {
                    // Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

}