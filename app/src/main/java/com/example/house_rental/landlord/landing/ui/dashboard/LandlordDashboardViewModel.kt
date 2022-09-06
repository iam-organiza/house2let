package com.example.house_rental.landlord.landing.ui.dashboard

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.house_rental.data.model.GetWalletResponse
import com.example.house_rental.data.repo.MainRepository
import com.example.house_rental.databinding.FragmentLandlordDashboardBinding
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.text.NumberFormat
import javax.inject.Inject

@HiltViewModel
class LandlordDashboardViewModel @Inject constructor(@ApplicationContext private val context: Context, private val repository: MainRepository): ViewModel() {
    private lateinit var binding: FragmentLandlordDashboardBinding
    private lateinit var preferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var authorization: String

    fun setBinding(_binding: FragmentLandlordDashboardBinding) {
        binding = _binding
        preferences = context.getSharedPreferences("sub_details", Context.MODE_PRIVATE)
        editor = preferences.edit()
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
                                landlordDashboardFragmentWalletBalanceLabel.text = formatter.format(getMeResponse.data.balance).toString()
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

}