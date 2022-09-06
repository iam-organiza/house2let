package com.example.house_rental.landlord.landing.ui.house_requests

import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.GridLayoutManager
import com.example.house_rental.data.model.DataXXXXXXX
import com.example.house_rental.data.model.GetHousesResponse
import com.example.house_rental.data.model.House
import com.example.house_rental.data.model.LandlordHouseRequestsResponse
import com.example.house_rental.data.repo.MainRepository
import com.example.house_rental.databinding.FragmentRequestsBinding
import com.example.house_rental.tenant.landing.ui.viewhouses.HousesAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class RequestsViewModel @Inject constructor(@ApplicationContext private val context: Context, private val repository: MainRepository) : ViewModel() {
    private lateinit var binding: FragmentRequestsBinding
    private val requests = arrayListOf<DataXXXXXXX>()
    private val adapter = RequestsAdapter(requests)

    fun setBinding(_binding: FragmentRequestsBinding) {
        binding = _binding
        binding.apply {
            requestsFragmentHouseRequestsRv.layoutManager = GridLayoutManager(context, 1)
            requestsFragmentHouseRequestsRv.adapter = adapter
        }
    }

    fun fetchRequests(authorization: String) {
        requests.clear()
        repository.getRequests(authorization).enqueue(
            object: Callback<LandlordHouseRequestsResponse> {
                override fun onResponse(
                    call: Call<LandlordHouseRequestsResponse>,
                    response: Response<LandlordHouseRequestsResponse>
                ) {
//                    println(response.body())
                    if (response.code().toString()[0].toString().toInt() == 2) {
                        val landlordHouseRequestsResponse = response.body()
//                        displayErrorText("Uploaded Successful.", R.color.bg_success)
                        println("Successful")
                        println(landlordHouseRequestsResponse?.data)
//                        println(getHousesResponse)
                        for (request in landlordHouseRequestsResponse?.data!!) {
                            requests.add(request)
                        }
//                        houses.addAll(getHousesResponse?.data?.items!!.toCollection(houses))
                        adapter.notifyDataSetChanged()
                    } else {
                        Log.d("Response error", "failed")
                    }
                    binding.apply {
                        requestsFragmentLoadingTextView.visibility = View.GONE
                    }
                }

                override fun onFailure(call: Call<LandlordHouseRequestsResponse>, t: Throwable) {
                    println(t)
                    Log.d("onFailure", "failed")
                    binding.apply {
                        requestsFragmentLoadingTextView.visibility = View.GONE
                    }
                }

            }
        )
    }
}