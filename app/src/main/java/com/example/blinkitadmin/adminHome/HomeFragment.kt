package com.example.blinkitadmin.adminHome

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.blinkitadmin.Constants
import com.example.blinkitadmin.adapter.CategoriesAdapter
import com.example.blinkitadmin.adapter.ProductAdapter
import com.example.blinkitadmin.adapter.SkeletonAdapter
import com.example.blinkitadmin.databinding.FragmentHomeBinding
import com.example.blinkitadmin.viewmodel.AdminViewModel
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private lateinit var binding : FragmentHomeBinding
    private val viewModel : AdminViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)

        onSearchTextListener()
        onSearchCrossClick()
        showSkeletonLoader()
        setCategoriesRecyclerView()
        setProductRecyclerView("All Products")

        return binding.root
    }

    private fun onCategoryClicked(category: String) {
        showSkeletonLoader()
        setProductRecyclerView(category)
    }

    private fun setProductRecyclerView(category: String) {
        lifecycleScope.launch {
            viewModel.fetchAllProducts(category).collect {
                val productAdapter = ProductAdapter(this@HomeFragment)
                productAdapter.differ.submitList(it)
                binding.rvProducts.adapter = productAdapter

                hideSkeletonLoader()

                if(it.isEmpty()) {
                    binding.tvNoProducts.visibility = View.VISIBLE
                } else {
                    binding.tvNoProducts.visibility = View.GONE
                }
            }
        }

    }

    private fun setCategoriesRecyclerView() {
        val categoryAdapter = CategoriesAdapter(Constants.allProductCategory, this, ::onCategoryClicked)
        binding.rvCategories.adapter = categoryAdapter
    }

    private fun showSkeletonLoader() {
        binding.apply {
            scrollView.visibility = View.GONE
            skeletonScrollView.visibility = View.VISIBLE
            val skeletonList = List(6)  {""}
            binding.rvSkeletonProducts.adapter = SkeletonAdapter(skeletonList)
        }
    }

    private fun hideSkeletonLoader() {
        binding.apply {
            skeletonScrollView.visibility = View.GONE
            scrollView.visibility = View.VISIBLE
        }
    }

    private fun onSearchCrossClick() {
        binding.crossBtn.setOnClickListener {
            binding.etSearchBar.setText("")
        }
    }

    private fun onSearchTextListener() {
        binding.etSearchBar.addTextChangedListener( object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().isNotEmpty()) {
                    binding.crossBtn.visibility = View.VISIBLE
                } else {
                    binding.crossBtn.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {}

        })
    }
}