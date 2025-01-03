package com.example.blinkitadmin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.blinkitadmin.databinding.SkeletonLoaderItemBinding

class SkeletonAdapter(private val skeletonList : List<String>) : RecyclerView.Adapter<SkeletonAdapter.SkeletonViewHolder>() {

    inner class SkeletonViewHolder(val binding : SkeletonLoaderItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            this.binding.apply {
                shimmer.showShimmer(true)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkeletonViewHolder {
        return SkeletonViewHolder(SkeletonLoaderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: SkeletonViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return skeletonList.size
    }
}