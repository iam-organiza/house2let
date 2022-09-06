package com.example.house_rental.tenant.landing.ui.viewhouse

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.core.view.setMargins
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
import com.example.house_rental.data.model.GetHouseResponse
import com.example.house_rental.data.model.GetHousesResponse
import com.example.house_rental.data.model.House
import com.example.house_rental.data.repo.MainRepository
import com.example.house_rental.databinding.FragmentViewHouseBinding
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ViewHouseViewModel @Inject constructor(@ApplicationContext private val context: Context, private val repository: MainRepository) : ViewModel() {
    private lateinit var binding: FragmentViewHouseBinding
    var house: House? = null

    fun setBinding(_binding: FragmentViewHouseBinding) {
        binding = _binding
    }

    fun getHouse(id: Int, callback: () -> Unit) {
        repository.getHouse(id).enqueue(
            object: Callback<GetHouseResponse> {
                override fun onResponse(
                    call: Call<GetHouseResponse>,
                    response: Response<GetHouseResponse>
                ) {

                    if (response.code().toString()[0].toString().toInt() == 2) {
                        val getHouseResponse = response.body()
//                        displayErrorText("Uploaded Successful.", R.color.bg_success)
//                        println("Successful")
//                        println(getHouseResponse)
                        house = getHouseResponse?.data

                        callback()

                        binding.apply {
                            viewHouseFragmentHouseTitle.text = house?.title?.trim('"')
                            viewHouseFragmentHouseAddress.text = house?.address?.trim('"')
                            viewHouseFragmentHousePrice.text = "${"%,d".format(house?.price)} / per annum"
                            viewHouseFragmentHouseDescription.text = house?.description?.trim('"')
                            for(houseImage in house?.HouseImages!!) {
                                val image = ImageView(context)
                                val params = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    400
                                )
                                params.setMargins(0, 10, 0, 10)
                                image.layoutParams = params

                                Glide.with(context)
                                    .load(houseImage.image)
                                    .centerCrop()
                                    .skipMemoryCache(true)//for caching the image url in case phone is offline
                                    .into(image)
                                viewHouseFragmentHouseImagesContainer.addView(image)
                                println(houseImage.image)
                            }
                        }
                    } else {
                        Log.d("Response error", "failed")
                    }
                }

                override fun onFailure(call: Call<GetHouseResponse>, t: Throwable) {
                    Log.d("onFailure", "failed")
                }

            }
        )
    }
}