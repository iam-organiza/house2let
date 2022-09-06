package com.example.house_rental.data.repo

import com.example.house_rental.data.api.ApiHelper
import com.example.house_rental.data.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Part

class MainRepository(private val apiHelper: ApiHelper) {
    fun registerUser(userDetails: UserDetails) = apiHelper.registerUser(userDetails)

    fun verifyEmail(verifyEmail: VerifyEmail) = apiHelper.verifyEmail(verifyEmail)

    fun resendOTP(resendOTP: ResendOTP) = apiHelper.resendOTP(resendOTP)

    fun loginUser(loginPayload: LoginPayload) = apiHelper.loginUser(loginPayload)

    fun verifyLogin(verifyLoginPayload: VerifyLoginPayload) = apiHelper.verifyLogin(verifyLoginPayload)

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
    ) = apiHelper.uploadHouse(
        authorization,
        ownerId,
        address,
        lat,
        lng,
        description,
        price,
        title,
        images
    )

    fun getHouses() = apiHelper.getHouses()

    fun getHouse(id: Int) = apiHelper.getHouse(id)

    fun updateProfile(
        authorization: String,
        image: MultipartBody.Part,
        occupation: String,
        file: MultipartBody.Part,
        firstName: String,
        lastName: String,
    ) = apiHelper.updateProfile(
        authorization,
        image,
        occupation,
        file,
        firstName,
        lastName,
    )

    fun getMe(authorization: String) = apiHelper.getMe(authorization)

    fun requestRent(authorization: String, houseRequest: HouseRequest) = apiHelper.requestRent(authorization, houseRequest)

    fun requestReset(requestReset: RequestReset) = apiHelper.requestReset(requestReset)

    fun resetPassword(reset: Reset) = apiHelper.resetPassword(reset)

    fun getWallet(authorization: String) = apiHelper.getWallet(authorization)

    fun fundWallet(authorization: String, fundWalletPayload: FundWalletPayload) = apiHelper.fundWallet(authorization, fundWalletPayload)

    fun getRequests(authorization: String) = apiHelper.getRequests(authorization)

    fun getRequest(authorization: String, request_id: Int) = apiHelper.getRequest(authorization, request_id)

    fun sendResponse(authorization: String, requestId: Int, landlordRespondPayload: LandlordResponsePayload) = apiHelper.sendResponse(authorization, requestId, landlordRespondPayload)

    fun getTenantRequests(authorization: String) = apiHelper.getTenantRequests(authorization)

    fun payRent(authorization: String, requestId: Int) = apiHelper.payRent(authorization, requestId)
}