package com.example.blinkitadmin.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.blinkitadmin.databinding.UploadListItemBinding

class AdapterUploadImage(private var imageList : ArrayList<Uri>, private val fragment: Fragment) : RecyclerView.Adapter<AdapterUploadImage.ItemViewHolder>() {

    inner class ItemViewHolder(val binding : UploadListItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(UploadListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val imageUri = imageList[position]

        Glide.with(fragment)
            .load(imageUri)
            .into(holder.binding.imgProduct)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

}