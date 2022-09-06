package com.example.house_rental.data.api

import com.example.house_rental.data.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("user/register")
    fun registerUser(@Body userDetails: UserDetails): Call<ResponseBody>

    @POST("user/verify")
    fun verifyEmail(@Body verifyEmail: VerifyEmail): Call<ResponseBody>

    @POST("user/resend")
    fun resendOTP(@Body resendOTP: ResendOTP): Call<ResponseBody>

    @POST("user/login")
    fun loginUser(@Body loginPayload: LoginPayload): Call<LoginResponseX>

    @POST("user/login/verify")
    fun verifyLogin(@Body verifyLoginPayload: VerifyLoginPayload): Call<LoginResponse>

//    @POST("house")
//    fun uploadHouse(@Part images: RequestBody): Call<ResponseBody>

    @POST("house")
    @Multipart
    fun uploadHouse(
        @Header("Authorization") authorization: String,
        @Part("ownerId") ownerId: Int,
        @Part("address") address: String,
        @Part("lat") lat: Double,
        @Part("lng") lng: Double,
        @Part("description") description: String,
        @Part("price") price: Int,
        @Part("title") title: String,
        @Part images: ArrayList<MultipartBody.Part>
    ): Call<HouseUploadResponse>

    @GET("house")
    fun getHouses(): Call<GetHousesResponse>

    @GET("house/{id}")
    fun getHouse(@Path("id") id: Int): Call<GetHouseResponse>

    @PUT("user/profile")
    @Multipart
    fun updateProfile(
        @Header("Authorization") authorization: String,
        @Part image: MultipartBody.Part,
        @Part("occupation") occupation: String,
        @Part ref_letter: MultipartBody.Part,
        @Part("firstName") firstName: String,
        @Part("lastName") lastName: String,
    ): Call<UpdateProfileResponse>

    @GET("user/me")
    fun getMe(@Header("Authorization") authorization: String): Call<GetMeResponse>

    @POST("houserequest")
    fun requestRent(
        @Header("Authorization") authorization: String,
        @Body houseRequest: HouseRequest
    ): Call<HouseRequestResponse>

    @POST("user/request-reset")
    fun requestReset(@Body requestReset: RequestReset): Call<RequestResetResponse>

    @POST("user/reset")
    fun resetPassword(@Body reset: Reset): Call<RequestResetResponse>

    @GET("wallet")
    fun getWallet(@Header("Authorization") authorization: String,): Call<GetWalletResponse>

    @POST("wallet")
    fun fundWallet(@Header("Authorization") authorization: String, @Body fundWalletPayload: FundWalletPayload): Call<InitializeTopUpResponse>

    @GET("houserequest/landlord")
    fun getRequests(@Header("Authorization") authorization: String,): Call<LandlordHouseRequestsResponse>

    @GET("houserequest/{request_id}")
    fun getRequest(@Header("Authorization") authorization: String, @Path("request_id") request_id: Int): Call<GetHouseRequestResponse>

    @POST("houserequest/landlord/respond/{request_id}")
    fun sendResponse(@Header("Authorization") authorization: String, @Path("request_id") request_id: Int, @Body response: LandlordResponsePayload): Call<LandlordResponsePayloadX>

    @GET("houserequest")
    fun getTenantRequests(@Header("Authorization") authorization: String): Call<TenantHouseRequestsResponse>

    @POST("houserequest/complete/{request_id}")
    fun payRent(@Header("Authorization") authorization: String, @Path("request_id") request_id: Int): Call<PayRentResponse>
}