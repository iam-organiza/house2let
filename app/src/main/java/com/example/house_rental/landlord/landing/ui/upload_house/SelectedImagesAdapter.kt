package com.example.house_rental.landlord.landing.ui.upload_house

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.house_rental.R
import com.example.house_rental.databinding.SingleSelectedHouseImageBinding

class SelectedImagesAdapter(private val uriList: ArrayList<Uri>) : RecyclerView.Adapter<SelectedImagesViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedImagesViewHolder {
        return SelectedImagesViewHolder(SingleSelectedHouseImageBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: SelectedImagesViewHolder, position: Int) {
        holder.image.setImageURI(uriList[position])
    }

    override fun getItemCount(): Int = uriList.size
}

class SelectedImagesViewHolder(binding: SingleSelectedHouseImageBinding) : RecyclerView.ViewHolder(binding.root) {
    val image = binding.houseImage
}