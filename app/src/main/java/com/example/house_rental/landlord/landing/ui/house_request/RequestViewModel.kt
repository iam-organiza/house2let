package com.example.house_rental.landlord.landing.ui.house_request

import android.content.Context
import android.text.method.LinkMovementMethod
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.example.house_rental.MainActivity
import com.example.house_rental.data.model.*
import com.example.house_rental.data.repo.MainRepository
import com.example.house_rental.databinding.FragmentRequestBinding
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject


@HiltViewModel
class RequestViewModel @Inject constructor(@ApplicationContext private val context: Context, private val repository: MainRepository) : ViewModel() {
    private lateinit var binding: FragmentRequestBinding
    private lateinit var authorization: String
    private lateinit var activity: FragmentActivity

    fun setBinding(_binding: FragmentRequestBinding) {
        binding = _binding
    }

    fun getRequest(authorization: String, request_id: Int) {
        repository.getRequest(authorization, request_id).enqueue(
            object: Callback<GetHouseRequestResponse> {
                override fun onResponse(
                    call: Call<GetHouseRequestResponse>,
                    response: Response<GetHouseRequestResponse>
                ) {

                    if (response.code().toString()[0].toString().toInt() == 2) {
                        val getHouseRequestsResponse = response.body()
                        val user = getHouseRequestsResponse?.data?.request?.User
                        val profile = getHouseRequestsResponse?.data?.UserProfile
                        val request = getHouseRequestsResponse?.data?.request
                        val house = getHouseRequestsResponse?.data?.request?.House

                        println("Successful")
                        println(getHouseRequestsResponse)

                        binding.apply {
                            Glide.with(context)
                                .load(profile?.image)
                                .centerCrop()
                                .skipMemoryCache(true)//for caching the image url in case phone is offline
                                .into(tenantAvatar)
                            tenantFullname.text = "${user?.firstName?.trim('"')} ${user?.lastName?.trim('"')}"
                            tenantOccupation.text = "${profile?.occupation?.trim('"')}"
                            tenantRequestReason.text = "${request?.reason?.trim('"')}"

                            refLetterLink.text = "${user?.refLetter}"

                            refLetterLink.movementMethod = LinkMovementMethod.getInstance()

                            Glide.with(context)
                                .load(request?.House?.HouseImages?.get(0)?.image)
                                .centerCrop()
                                .skipMemoryCache(true)//for caching the image url in case phone is offline
                                .into(houseImage)
                            houseTitle.text = "${house?.title?.trim('"')}"
                            houseAddress.text = "${house?.address?.trim('"')}"
                            housePrice.text = "${"%,d".format(house?.price)}"
                            houseDescription.text = "${house?.description?.trim('"')}"

                            acceptRequestButton.setOnClickListener {
                                it.isEnabled = false
                                acceptRequestButton.text = "please wait..."

//                                val landlordRespondPayload = LandlordRespondPayload(LandlordRespond.ACCEPTED)
                                val landlordRespondPayload = LandlordResponsePayload("ACCEPTED")
                                sendResponse(authorization, request?.id!!, landlordRespondPayload) {
                                    it.isEnabled = true
                                    acceptRequestButton.text = "Accept"
                                }
                            }

                            rejectRequestButton.setOnClickListener {
                                it.isEnabled = false
                                rejectRequestButton.text = "please wait..."

                                val landlordRespondPayload = LandlordResponsePayload("REJECTED")
                                println(landlordRespondPayload)
                                sendResponse(authorization, request?.id!!, landlordRespondPayload) {
                                    it.isEnabled = true
                                    rejectRequestButton.text = "Reject"
                                }
                            }
                        }
                    } else {
                        Log.d("Response error", "failed")
                    }
                }

                override fun onFailure(call: Call<GetHouseRequestResponse>, t: Throwable) {
                    Log.d("onFailure", "failed")
                }

            }
        )
    }

    fun setAuthorization(_authorization: String) {
        authorization = _authorization
    }

    fun sendResponse(authorization: String, request_id: Int, landlordRespondPayload: LandlordResponsePayload, callback: () -> Unit = {}) {
//        println(landlordRespondPayload)
//        println(request_id)
        repository.sendResponse(authorization, request_id, landlordRespondPayload).enqueue(
            object: Callback<LandlordResponsePayloadX> {
                override fun onResponse(
                    call: Call<LandlordResponsePayloadX>,
                    response: Response<LandlordResponsePayloadX>
                ) {

                    println("LandlordResponsePayloadX")
                    println(response)

                    if (response.code().toString()[0].toString().toInt() == 2) {
                        val landlordRespondResponse = response.body()
//                        val user = getHouseRequestsResponse?.data?.request?.User
//                        val profile = getHouseRequestsResponse?.data?.UserProfile
//                        val request = getHouseRequestsResponse?.data?.request
//                        val house = getHouseRequestsResponse?.data?.request?.House

                        println("Successful")
                        println(landlordRespondResponse)

                        val alertDialog = AlertDialog.Builder(activity).create()
                        alertDialog.setTitle("Alert")
                        alertDialog.setMessage("Request accepted")
                        alertDialog.setButton(
                            AlertDialog.BUTTON_NEUTRAL, "OK"
                        ) { dialog, _ -> dialog.dismiss() }
                        alertDialog.show()



//                        binding.apply {
//                            Glide.with(context)
//                                .load(profile?.image)
//                                .centerCrop()
//                                .skipMemoryCache(true)//for caching the image url in case phone is offline
//                                .into(tenantAvatar)
//                            tenantFullname.text = "${user?.firstName?.trim('"')} ${user?.lastName?.trim('"')}"
//                            tenantOccupation.text = "${profile?.occupation?.trim('"')}"
//                            tenantRequestReason.text = "${request?.reason?.trim('"')}"
//
//                            Glide.with(context)
//                                .load(request?.House?.HouseImages?.get(0)?.image)
//                                .centerCrop()
//                                .skipMemoryCache(true)//for caching the image url in case phone is offline
//                                .into(houseImage)
//                            houseTitle.text = "${house?.title?.trim('"')}"
//                            houseAddress.text = "${house?.address?.trim('"')}"
//                            housePrice.text = "${"%,d".format(house?.price)}"
//                            houseDescription.text = "${house?.description?.trim('"')}"
//
//                            acceptRequestButton.setOnClickListener {
//
//                            }
//
//                            rejectRequestButton.setOnClickListener {
//
//                            }
//                        }
                    } else {
                        Log.d("Response error", "failed")

                        val alertDialog = AlertDialog.Builder(activity).create()
                        alertDialog.setTitle("Alert")
                        alertDialog.setMessage("You have already responded to this request already!")
                        alertDialog.setButton(
                            AlertDialog.BUTTON_NEUTRAL, "OK"
                        ) { dialog, _ -> dialog.dismiss() }
                        alertDialog.show()
                    }

                    callback()
                }

                override fun onFailure(call: Call<LandlordResponsePayloadX>, t: Throwable) {
                    println("-------")
                    println(t)
                    Log.d("onFailure", "failed")
                }

            }
        )
    }

    fun setActivity(_activity: FragmentActivity) {
        activity = _activity
    }
}