package com.example.house_rental.tenant.landing.ui.dashboard

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.example.house_rental.R
import com.example.house_rental.data.model.*
import com.example.house_rental.data.repo.MainRepository
import com.example.house_rental.databinding.FragmentLoginBinding
import com.example.house_rental.databinding.FragmentTenantDashboardBinding
import com.example.house_rental.landlord.landing.ui.house_requests.RequestsAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.text.NumberFormat
import javax.inject.Inject

@HiltViewModel
class TenantDashboardViewModel @Inject constructor(@ApplicationContext private val context: Context, private val repository: MainRepository) : ViewModel() {
    private val requests = arrayListOf<DataXXXXXXXXXX>()
    private val adapter = TenantRequestsAdapter(requests, this)
    private lateinit var binding: FragmentTenantDashboardBinding
    private lateinit var preferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var authorization: String

    fun setBinding(_binding: FragmentTenantDashboardBinding) {
        binding = _binding
        preferences = context.getSharedPreferences("sub_details", Context.MODE_PRIVATE)
        editor = preferences.edit()
        binding.apply {
            tenantDashboardFragmentListActivities.layoutManager = GridLayoutManager(context, 1)
            tenantDashboardFragmentListActivities.adapter = adapter
        }
    }

    fun getMe(authorization: String) {
        repository.getMe(authorization).enqueue(
            object: Callback<GetMeResponse> {
                override fun onResponse(
                    call: Call<GetMeResponse>,
                    response: Response<GetMeResponse>
                ) {
                    if (response.code().toString()[0].toString().toInt() == 2) {
                        val body = response.body();

                        if (body != null) {
                            val getMeResponse = GetMeResponse(
                                data = body.data,
                                message = body.message,
                                status = body.status,
                                success = body.success
                            )

                            println("Get Me Response: ")
                            println(getMeResponse.data.UserProfile)

                            val userProfile = getMeResponse.data.UserProfile
                            val userData = getMeResponse.data

                            editor.putString("user_avatar", userProfile.image)
                            editor.putString("user_occupation", userProfile.occupation)
                            editor.putString("ref_letter", userData.refLetter)
                            editor.commit()

                            println(getMeResponse)
                            if (userProfile.image.isNullOrEmpty() || userProfile.occupation.isNullOrEmpty()) {
                                binding.tenantDashboardFragmentCompleteYourProfileLayout.visibility = View.VISIBLE
                            }

                        }
                    }
                }

                override fun onFailure(call: Call<GetMeResponse>, t: Throwable) {
                    Log.d("onFailure", "failed")
                }

            }
        )
    }

    fun getWallet(authorization: String) {
        repository.getWallet(authorization).enqueue(
            object: Callback<GetWalletResponse> {
                override fun onResponse(
                    call: Call<GetWalletResponse>,
                    response: Response<GetWalletResponse>
                ) {
                    if (response.code().toString()[0].toString().toInt() == 2) {
                        val body = response.body();

                        if (body != null) {
                            val getMeResponse = GetWalletResponse(
                                data = body.data,
                                message = body.message,
                                status = body.status,
                                success = body.success
                            )

                            println("Get Wallet: ")
                            println(getMeResponse.data)

                            editor.putInt("user_wallet_balance", getMeResponse.data.balance)
                            editor.commit()

                            val formatter: NumberFormat = DecimalFormat("#,###")
                            binding.apply {
                                tenantDashboardFragmentWalletBalanceLabel.text = formatter.format(getMeResponse.data.balance).toString()
                            }

                        }
                    }
                }

                override fun onFailure(call: Call<GetWalletResponse>, t: Throwable) {
                    Log.d("onFailure", "failed")
                }

            }
        )
    }

    fun fundWallet(authorization: String, fundWalletPayload: FundWalletPayload, callback: (authorizationUrl: String) -> Unit = {}) {
        repository.fundWallet(authorization, fundWalletPayload).enqueue(
            object: Callback<InitializeTopUpResponse> {
                override fun onResponse(
                    call: Call<InitializeTopUpResponse>,
                    response: Response<InitializeTopUpResponse>
                ) {
                    if (response.code().toString()[0].toString().toInt() == 2) {
                        val body = response.body();

                        if (body != null) {
                            val initializeTopUpResponse = InitializeTopUpResponse(
                                data = body.data,
                                message = body.message,
                                status = body.status,
                                success = body.success
                            )

                            println("Fund Wallet: ")
                            println(initializeTopUpResponse)
                            println(initializeTopUpResponse.data)

                            callback(initializeTopUpResponse.data.charge.authorizationUrl)

//                            editor.putInt("user_wallet_balance", initializeTopUpResponse.data.balance)
//                            editor.commit()
//
//                            val formatter: NumberFormat = DecimalFormat("#,###")
//                            binding.apply {
//                                tenantDashboardFragmentWalletBalanceLabel.text = formatter.format(getMeResponse.data.balance).toString()
//                            }

                        }
                    }
                }

                override fun onFailure(call: Call<InitializeTopUpResponse>, t: Throwable) {
                    Log.d("onFailure", "failed")
                }

            }
        )
    }

    fun getTenantRequests(authorization: String) {
        requests.clear()
        repository.getTenantRequests(authorization).enqueue(
            object: Callback<TenantHouseRequestsResponse> {
                override fun onResponse(
                    call: Call<TenantHouseRequestsResponse>,
                    response: Response<TenantHouseRequestsResponse>
                ) {
//                    println(response)
                    if (response.code().toString()[0].toString().toInt() == 2) {
                        val tenantHouseRequestsResponse = response.body()
//                        displayErrorText("Uploaded Successful.", R.color.bg_success)
                        println("Successful")
                        println(tenantHouseRequestsResponse?.data)
//                        println(getHousesResponse)
                        for (request in tenantHouseRequestsResponse?.data!!) {
                            requests.add(request)
                        }
////                        houses.addAll(getHousesResponse?.data?.items!!.toCollection(houses))
                        adapter.notifyDataSetChanged()
                    } else {
                        Log.d("Response error", "failed")
                    }
                    binding.apply {
//                        requestsFragmentLoadingTextView.visibility = View.GONE
                    }
                }

                override fun onFailure(call: Call<TenantHouseRequestsResponse>, t: Throwable) {
                    println(t)
                    Log.d("onFailure", "failed")
                    binding.apply {
//                        requestsFragmentLoadingTextView.visibility = View.GONE
                    }
                }

            }
        )
    }

    fun payRent(authorization: String, request_id: Int, callback: (payRentResponse: Response<PayRentResponse>) -> Unit = {}) {
        repository.payRent(authorization, request_id).enqueue(
            object: Callback<PayRentResponse> {
                override fun onResponse(
                    call: Call<PayRentResponse>,
                    response: Response<PayRentResponse>
                ) {
                    println("pay rent")
                    println(response)
                    println(response.body())
                    if (response.code().toString()[0].toString().toInt() == 2) {
                        val body = response.body();

                        if (body != null) {
                            val payRentResponse = PayRentResponse(
                                data = body.data,
                                message = body.message,
                                status = body.status,
                                success = body.success
                            )

                            println("House Paid: ")
                            println(payRentResponse)
                            println(payRentResponse.data)

                            callback(response)
                        }
                    } else {
                        callback(response)
                    }
                }

                override fun onFailure(call: Call<PayRentResponse>, t: Throwable) {
                    Log.d("onFailure", "failed")
                }

            }
        )
    }

    fun setAuthorization(_authorization: String) {
        authorization = _authorization
    }

    fun getAuthorization(): String {
        return authorization
    }
}