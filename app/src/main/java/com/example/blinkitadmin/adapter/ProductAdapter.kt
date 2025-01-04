package com.example.blinkitadmin.adapter

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.blinkitadmin.R
import com.example.blinkitadmin.Utility
import com.example.blinkitadmin.databinding.ItemViewProductBinding
import com.example.blinkitadmin.model.Product

class ProductAdapter(private val fragment : Fragment) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    val differ = AsyncListDiffer(this, object : DiffUtil.ItemCallback<Product>() {
        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    })

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductAdapter.ProductViewHolder {
        return ProductViewHolder(ItemViewProductBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ProductAdapter.ProductViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class ProductViewHolder(val binding : ItemViewProductBinding) : RecyclerView.ViewHolder(binding.root) {
        private var images : ArrayList<String?>? = null
        private var currentImageIndex = 0

        fun bind(position: Int) {
            binding.apply {
                val quantity = "${differ.currentList[position].quantity} ${differ.currentList[position].unit}"
                tvQuantity.text = quantity
                tvName.text = differ.currentList[position].title
                if(differ.currentList[position].discount == null) {
                    tvDiscount.visibility = View.GONE
                    tvMrp.visibility = View.GONE
                } else {
                    tvDiscount.visibility = View.VISIBLE
                    tvMrp.visibility = View.VISIBLE
                    tvDiscount.text = fragment.getString(R.string.discount_text, differ.currentList[position].discount)
                    val builder = SpannableStringBuilder()
                    builder.append("MRP ")
                    val start = builder.length
                    builder.append("₹${differ.currentList[position].price.toString()}")
                        .setSpan(StrikethroughSpan(), start, builder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    tvMrp.text = builder
                }
                tvPrice.text = fragment.getString(R.string.price_text, Utility.discountPrice(differ.currentList[position].price!!, differ.currentList[position].discount).toString())

            }

            // Set up the ImageSwitcher with animation
            val fadeIn: Animation = AnimationUtils.loadAnimation(fragment.requireContext(), android.R.anim.fade_in)
            val fadeOut: Animation = AnimationUtils.loadAnimation(fragment.requireContext(), android.R.anim.fade_out)
            binding.imageSwitcher.inAnimation = fadeIn
            binding.imageSwitcher.outAnimation = fadeOut

            // List of product image resources (can be URLs or drawables)
            images = differ.currentList[position].productImageUrl

            // Set the initial image
            Glide.with(binding.imageSwitcher)
                .load(images!![0])
                .override(100, 100)
                .into(binding.imageView)

        }

    }
}