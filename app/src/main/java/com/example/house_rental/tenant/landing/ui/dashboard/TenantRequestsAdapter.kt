package com.example.house_rental.tenant.landing.ui.dashboard

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.house_rental.data.model.DataXXXXXXX
import com.example.house_rental.data.model.DataXXXXXXXXXX
import com.example.house_rental.data.model.House
import com.example.house_rental.data.model.LandlordRespond
import com.example.house_rental.databinding.SingleHouseBinding
import com.example.house_rental.databinding.SingleRequestBinding
import com.example.house_rental.databinding.TenantSingleRequestBinding
import java.text.DecimalFormat
import java.text.NumberFormat

class TenantRequestsAdapter(private val requests: ArrayList<DataXXXXXXXXXX>, private val viewModel: TenantDashboardViewModel) : RecyclerView.Adapter<RequestsViewHolder>() {
    private lateinit var context: Context
    private lateinit var viewGroup: ViewGroup

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestsViewHolder {
        context = parent.context
        viewGroup = parent
        return RequestsViewHolder(TenantSingleRequestBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RequestsViewHolder, position: Int) {
        val formatter: NumberFormat = DecimalFormat("#,###")
        holder.title.text = "${requests[position].House.title.trim('"')} / N${formatter.format(requests[position].House.price)}"
        holder.status.text = "Status: ${requests[position].status}"

        println(requests[position].House.status)
        println(requests[position].status)
        println(requests[position].id)
        if (requests[position].status != LandlordRespond.ACCEPTED.toString()) {
            holder.payButton.visibility = View.GONE
        }

        if (requests[position].House.status == "CLOSED") {
            holder.payButton.isEnabled = false
            holder.payButton.text = "PAID"
        }

        holder.payButton.setOnClickListener {
            holder.payButton.isEnabled = false
            holder.payButton.text = "please wait..."
            viewModel.payRent(viewModel.getAuthorization(), requests[position].id) {
                if (it.code().toString()[0].toString().toInt() == 2) {
                    holder.payButton.isEnabled = true
                    holder.payButton.text = "Paid"
                } else {
                    holder.payButton.isEnabled = true
                    holder.payButton.text = "Insufficient fund!"
                }
            }
//            val action = RequestsFragmentDirections.actionRequestsFragmentToRequestFragment(requests[position].id)
//            println(action)
//            Navigation.findNavController(viewGroup).navigate(action)
//            holder.itemView.findNavController().navigate(action)
        }
//        Glide.with(context)
//            .load(houses[position].HouseImages[0].image)
//            .centerCrop()
//            .skipMemoryCache(true)//for caching the image url in case phone is offline
//            .into(holder.image)
//        holder.title.text = houses[position].title.trim('"')
//        holder.price.text = "${houses[position].price} / per annum"
//        holder.address.text = houses[position].address.trim('"')
//        holder.sendRequestBtn.setOnClickListener {
//            val action = ViewHousesFragmentDirections.actionViewHousesFragmentToHouseRequestFragment(houses[position].id)
//            Navigation.findNavController(viewGroup).navigate(action)
//        }

    }

    override fun getItemCount(): Int = requests.size
}

class RequestsViewHolder(binding: TenantSingleRequestBinding) : RecyclerView.ViewHolder(binding.root) {
    val container = binding.requestContainer
    val title = binding.requestTitle
    val status = binding.requestStatus
    val payButton = binding.payButton
}