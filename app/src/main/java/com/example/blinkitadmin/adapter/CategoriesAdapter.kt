package com.example.blinkitadmin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.blinkitadmin.databinding.ItemViewCategoriesBinding
import com.example.blinkitadmin.model.Category

class CategoriesAdapter(private val categoryImageUrlList : Array<Category>, private val fragment : Fragment) : RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(val binding : ItemViewCategoriesBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(ItemViewCategoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.binding.apply {
            tvCategoryTitle.text = categoryImageUrlList[position].title

            Glide.with(fragment)
                .load(categoryImageUrlList[position].imageUrl)
                .into(holder.binding.imgCategory)

        }
    }

    override fun getItemCount(): Int {
        return categoryImageUrlList.size
    }
}