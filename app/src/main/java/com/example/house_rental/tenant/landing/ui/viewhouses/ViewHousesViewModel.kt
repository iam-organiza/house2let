package com.example.house_rental.tenant.landing.ui.viewhouses

import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.GridLayoutManager
import com.example.house_rental.data.model.GetHousesResponse
import com.example.house_rental.data.model.House
import com.example.house_rental.data.repo.MainRepository
import com.example.house_rental.databinding.FragmentViewHousesBinding
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ViewHousesViewModel @Inject constructor(@ApplicationContext private val context: Context, private val repository: MainRepository) : ViewModel() {
    private lateinit var binding: FragmentViewHousesBinding
    private val houses = arrayListOf<House>()
    private val adapter = HousesAdapter(houses)
    val imagePartList = arrayListOf<MultipartBody.Part>()

    fun setBinding(_binding: FragmentViewHousesBinding) {
        binding = _binding
        binding.apply {
            viewHousesFragmentHousesRecyclerView.layoutManager = GridLayoutManager(context, 2)
            viewHousesFragmentHousesRecyclerView.adapter = adapter
        }
    }

    fun fetchHouses() {
        houses.clear()
        repository.getHouses().enqueue(
            object: Callback<GetHousesResponse> {
                override fun onResponse(
                    call: Call<GetHousesResponse>,
                    response: Response<GetHousesResponse>
                ) {

                    if (response.code().toString()[0].toString().toInt() == 2) {
                        val getHousesResponse = response.body()
//                        displayErrorText("Uploaded Successful.", R.color.bg_success)
                        println("Successful")
//                        println(getHousesResponse)
                        for (house in getHousesResponse?.data?.items!!) {
                            houses.add(house)
                        }
//                        houses.addAll(getHousesResponse?.data?.items!!.toCollection(houses))
                        adapter.notifyDataSetChanged()
                    } else {
                        Log.d("Response error", "failed")
                    }
                    binding.apply {
                        viewHousesFragmentLoadingTextView.visibility = View.GONE
                    }
                }

                override fun onFailure(call: Call<GetHousesResponse>, t: Throwable) {
                    Log.d("onFailure", "failed")
                    binding.apply {
                        viewHousesFragmentLoadingTextView.visibility = View.GONE
                    }
                }

            }
        )
    }
}