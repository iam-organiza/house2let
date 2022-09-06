package com.example.house_rental.data.api

import com.example.house_rental.data.model.*
import okhttp3.MultipartBody

class ApiHelper(private val apiService: ApiService) {
    fun registerUser(userDetails: UserDetails) = apiService.registerUser(userDetails)

    fun verifyEmail(verifyEmail: VerifyEmail) = apiService.verifyEmail(verifyEmail)

    fun resendOTP(resendOTP: ResendOTP) = apiService.resendOTP(resendOTP)

    fun loginUser(loginPayload: LoginPayload) = apiService.loginUser(loginPayload)

    fun verifyLogin(verifyLoginPayload: VerifyLoginPayload) = apiService.verifyLogin(verifyLoginPayload)

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
    ) = apiService.uploadHouse(
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

    fun getHouses() = apiService.getHouses()

    fun getHouse(id: Int) = apiService.getHouse(id)

    fun updateProfile(
        authorization: String,
        image: MultipartBody.Part,
        occupation: String,
        file: MultipartBody.Part,
        firstName: String,
        lastName: String,
    ) = apiService.updateProfile(
        authorization,
        image,
        occupation,
        file,
        firstName,
        lastName,
    )

    fun getMe(authorization: String) = apiService.getMe(authorization)

    fun requestRent(authorization: String, houseRequest: HouseRequest) = apiService.requestRent(authorization, houseRequest)

    fun requestReset(requestReset: RequestReset) = apiService.requestReset(requestReset)

    fun resetPassword(reset: Reset) = apiService.resetPassword(reset)

    fun getWallet(authorization: String) = apiService.getWallet(authorization)

    fun fundWallet(authorization: String, fundWalletPayload: FundWalletPayload) = apiService.fundWallet(authorization, fundWalletPayload)

    fun getRequests(authorization: String) = apiService.getRequests(authorization)

    fun getRequest(authorization: String, requestId: Int) = apiService.getRequest(authorization, requestId)

    fun sendResponse(authorization: String, requestId: Int, landlordRespondPayload: LandlordResponsePayload) = apiService.sendResponse(authorization, requestId, landlordRespondPayload)

    fun getTenantRequests(authorization: String) = apiService.getTenantRequests(authorization)

    fun payRent(authorization: String, requestId: Int) = apiService.payRent(authorization, requestId)
}