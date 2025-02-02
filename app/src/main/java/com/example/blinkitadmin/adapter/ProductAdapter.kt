package com.example.blinkitadmin.adapter

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Filter
import android.widget.Filterable
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.blinkitadmin.ProductFilter
import com.example.blinkitadmin.R
import com.example.blinkitadmin.Utility
import com.example.blinkitadmin.databinding.ItemViewProductBinding
import com.example.blinkitadmin.model.Product

class ProductAdapter(
    private val fragment : Fragment,
    val onEditButtonClick : (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>(), Filterable {

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

        fun bind(position: Int) {
            binding.apply {
                val quantity = "${differ.currentList[position].quantity} ${differ.currentList[position].unit}"
                tvQuantity.text = quantity
                tvName.text = differ.currentList[position].title
                if(differ.currentList[position].discount == null || differ.currentList[position].discount == 0) {
                    tvDiscount.visibility = View.GONE
                    tvMrp.visibility = View.GONE
                } else {
                    tvDiscount.visibility = View.VISIBLE
                    tvMrp.visibility = View.VISIBLE
                    tvDiscount.text = fragment.getString(R.string.discount_text, differ.currentList[position].discount)
                    val builder = SpannableStringBuilder()
                    builder.append("MRP ")
                    val start = builder.length
                    builder.append("₹${Utility.priceString(differ.currentList[position].price.toString())}")
                        .setSpan(StrikethroughSpan(), start, builder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    tvMrp.text = builder
                }
                tvPrice.text = fragment.getString(R.string.price_text, Utility.priceString(Utility.discountPrice(differ.currentList[position].price!!, differ.currentList[position].discount).toString()))

                images = differ.currentList[position].productImageUrl

                // Set the initial image
                Glide.with(imageSwitcher)
                    .load(images!![0])
                    .override(100, 100)
                    .into(imageView)

                // Edit Button Click Listener
                btnEdit.setOnClickListener {
                    onEditButtonClick(differ.currentList[position])
                }
            }

        }

    }

    var originalProductList : List<Product> = ArrayList()
    var filter : ProductFilter? = null
    override fun getFilter(): Filter {
        if(filter == null) {
            filter = ProductFilter(this, originalProductList as ArrayList<Product>)
        }
        return filter as ProductFilter
    }
}