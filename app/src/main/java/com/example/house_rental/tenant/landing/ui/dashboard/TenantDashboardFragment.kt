package com.example.house_rental.tenant.landing.ui.dashboard

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.example.house_rental.R
import com.example.house_rental.data.model.FundWalletPayload
import com.example.house_rental.databinding.FragmentTenantDashboardBinding
import com.example.house_rental.databinding.TopUpAlertDialogBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat
import java.text.NumberFormat


@AndroidEntryPoint
class TenantDashboardFragment : Fragment() {
    private val viewModel: TenantDashboardViewModel by viewModels()
    private lateinit var binding: FragmentTenantDashboardBinding
    private lateinit var preferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentTenantDashboardBinding.inflate(layoutInflater)
        preferences = requireContext().getSharedPreferences("sub_details", Context.MODE_PRIVATE)

        viewModel.setBinding(binding)
        binding.apply {
            val name = "Hi ${preferences.getString("user_first_name", "")?.trim('"')}"
            tenantDashboardFragmentWelcomeLabel.text = name
        }

        val user_avatar = preferences.getString("user_avatar", "")
        val user_occupation = preferences.getString("user_occupation", "")
        val ref_letter = preferences.getString("ref_letter", "")
//        val user_source_of_income = preferences.getString("user_source_of_income", "")
        val user_wallet_balance = preferences.getInt("user_wallet_balance", 0)

        val authorization = getAuthorization()
        Log.d("Auth", authorization)
        viewModel.setAuthorization(authorization)

        if (user_avatar.isNullOrEmpty() || user_occupation.isNullOrEmpty() || ref_letter.isNullOrEmpty()) {
            viewModel.getMe(authorization)
        }

        viewModel.getTenantRequests(authorization)

        val formatter: NumberFormat = DecimalFormat("#,###")

        viewModel.getWallet(authorization)

        binding.apply {
            tenantDashboardFragmentWalletBalanceLabel.text = formatter.format(user_wallet_balance).toString()
        }

        binding.tenantDashboardFragmentWalletTopupButton.setOnClickListener {

            val builder = AlertDialog.Builder(requireContext(),R.style.TopUpAlertDialog)
                .create()

            val view = TopUpAlertDialogBinding.inflate(layoutInflater)
            val errorContainer = view.topUpAlertDialogErrorContainer
            val errorText = view.topUpAlertDialogErrorText


            val dismissBtn: Button = view.topUpAlertDialogDismissButton
            val confirmBtn: Button = view.topUpAlertDialogConfirmButton
            builder.setView(view.root)

            dismissBtn.setOnClickListener {
                builder.dismiss()
            }

            confirmBtn.setOnClickListener {
                val amount = view.topUpAlertDialogAmountInput.text.toString()
                val reason = "Rent"

                it.isEnabled = false
                confirmBtn.text = "please wait..."

                if (amount.isNullOrEmpty() || reason.isNullOrEmpty()) {

                    it.isEnabled = true
                    confirmBtn.text = "Top Up"

                    displayTopUpAlertDialogErrorText(view, "Please fill in form correctly")
                    clearTopUpAlertDialogErrorText(view)

                } else {

                    val fundWalletPayload = FundWalletPayload(amount.toInt(), reason)

                    viewModel.fundWallet(authorization, fundWalletPayload) { url ->
                        confirmBtn.isEnabled = true
                        confirmBtn.text = "Top Up"
                        builder.dismiss()
                        loadCheckout(url, "http://housetolet.io/wallet/")
                    }

                }
            }

            builder.setCanceledOnTouchOutside(false)
            builder.show()

        }



        binding.tenantDashboardFragmentCompleteProfileButton.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.settingsFragment)
        }

        return binding.root
    }

    private fun getToken(): String {
        return preferences.getString("user_token", "")!!
    }

    private fun getAuthorization(): String {
        return "Bearer ${getToken()}"
    }

    private fun clearTopUpAlertDialogErrorText(binding: TopUpAlertDialogBinding, delay: Long = 3000, callback: () -> Unit = {}) {
        Handler(Looper.getMainLooper()).postDelayed({
            binding.apply {
                topUpAlertDialogErrorContainer.visibility = View.GONE
                topUpAlertDialogErrorText.text = ""
            }
            callback()
        }, delay)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadCheckout(authorizationUrl: String, callbackUrl: String) {
        val webView: WebView = binding.webView
        webView.visibility = View.VISIBLE

        webView.settings.apply {
            javaScriptEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            domStorageEnabled = true
        }

        webView.webViewClient = object:  WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url: Uri? = request?.url

                if (url?.host == callbackUrl) {
                    return true
                } else if (url.toString() == "https://standard.paystack.co/close") {
                    return false
                }

                return super.shouldOverrideUrlLoading(view, request)
            }
        }

        webView.loadUrl(authorizationUrl)
    }

    private fun displayTopUpAlertDialogErrorText(binding: TopUpAlertDialogBinding, target: CharSequence?, color: Int = R.color.bg_danger) {
        binding.apply {
            topUpAlertDialogErrorContainer.visibility = View.VISIBLE

            context?.let { topUpAlertDialogErrorContainer.setBackgroundColor(it.getColor(color)) }
            topUpAlertDialogErrorText.text = target
        }
    }
}