package com.example.house_rental.landlord.landing.ui.house_requests

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.house_rental.data.model.DataXXXXXXX
import com.example.house_rental.data.model.House
import com.example.house_rental.databinding.SingleHouseBinding
import com.example.house_rental.databinding.SingleRequestBinding

class RequestsAdapter(private val requests: ArrayList<DataXXXXXXX>) : RecyclerView.Adapter<RequestsViewHolder>() {
    private lateinit var context: Context
    private lateinit var viewGroup: ViewGroup

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestsViewHolder {
        context = parent.context
        viewGroup = parent
        return RequestsViewHolder(SingleRequestBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RequestsViewHolder, position: Int) {
        holder.title.text = "${requests[position].User.firstName.trim('"')} ${requests[position].User.lastName.trim('"')}"
        holder.viewRequestBtn.setOnClickListener {
            val action = RequestsFragmentDirections.actionRequestsFragmentToRequestFragment(requests[position].id)
//            println(action)
//            Navigation.findNavController(viewGroup).navigate(action)
            holder.itemView.findNavController().navigate(action)
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

class RequestsViewHolder(binding: SingleRequestBinding) : RecyclerView.ViewHolder(binding.root) {
    val container = binding.requestContainer
    val title = binding.requestTitle
    val viewRequestBtn = binding.viewRequestButton
}