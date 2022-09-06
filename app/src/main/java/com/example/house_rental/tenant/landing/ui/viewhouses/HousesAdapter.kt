package com.example.house_rental.tenant.landing.ui.viewhouses

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.house_rental.R
import com.example.house_rental.data.model.House
import com.example.house_rental.databinding.SingleHouseBinding

class HousesAdapter(private val houses: ArrayList<House>) : RecyclerView.Adapter<HousesViewHolder>() {
    private lateinit var context: Context
    private lateinit var viewGroup: ViewGroup

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HousesViewHolder {
        context = parent.context
        viewGroup = parent
        return HousesViewHolder(SingleHouseBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: HousesViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            val action = ViewHousesFragmentDirections.actionViewHousesFragmentToViewHouseFragment(houses[position].id)
            Navigation.findNavController(viewGroup).navigate(action)
        }
        Glide.with(context)
            .load(houses[position].HouseImages[0].image)
            .centerCrop()
            .skipMemoryCache(true)//for caching the image url in case phone is offline
            .into(holder.image)
        holder.title.text = houses[position].title.trim('"')
        holder.price.text = "${houses[position].price} / per annum"
        holder.address.text = houses[position].address.trim('"')
        holder.sendRequestBtn.setOnClickListener {
            val action = ViewHousesFragmentDirections.actionViewHousesFragmentToHouseRequestFragment(houses[position].id)
            Navigation.findNavController(viewGroup).navigate(action)
        }

    }

    override fun getItemCount(): Int = houses.size
}

class HousesViewHolder(binding: SingleHouseBinding) : RecyclerView.ViewHolder(binding.root) {
    val container = binding.houseContainer
    val title = binding.houseTitle
    val image = binding.houseImage
    val price = binding.housePrice
    val address = binding.houseAddress
    val sendRequestBtn = binding.sendRequestButton
}