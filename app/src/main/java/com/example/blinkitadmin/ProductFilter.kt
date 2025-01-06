package com.example.blinkitadmin

import android.widget.Filter
import com.example.blinkitadmin.adapter.ProductAdapter
import com.example.blinkitadmin.model.Product
import java.util.Locale

class ProductFilter(val adapter : ProductAdapter, val productList : ArrayList<Product>) : Filter() {
    override fun performFiltering(constraint: CharSequence?): FilterResults {
        val filteredList : ArrayList<Product> = arrayListOf()
        val query = constraint.toString().trim().uppercase(Locale.getDefault()).split(" ")
        val result = FilterResults()

        if(constraint.isNullOrEmpty()) {
            result.values = productList
            result.count = productList.size
        } else {
            productList.forEach { product ->
                if(query.any {
                        product.title?.uppercase(Locale.getDefault()).toString().contains(it) ||
                                product.type?.uppercase(Locale.getDefault()).toString().contains(it)
                    }) {
                    filteredList.add(product)
                }
            }
            result.values = filteredList
            result.count = filteredList.size
        }
        return result
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
        adapter.differ.submitList(results?.values as List<Product>)
    }

}