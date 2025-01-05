package com.example.blinkitadmin.adminHome

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.blinkitadmin.Constants
import com.example.blinkitadmin.R
import com.example.blinkitadmin.adapter.CategoriesAdapter
import com.example.blinkitadmin.adapter.ProductAdapter
import com.example.blinkitadmin.adapter.SkeletonAdapter
import com.example.blinkitadmin.databinding.EditProductViewBinding
import com.example.blinkitadmin.databinding.FragmentHomeBinding
import com.example.blinkitadmin.model.Product
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
        setProductRecyclerView(Constants.allProductCategory[0].title!!)

        return binding.root
    }

    private fun onEditButtonClick(product: Product) {
        val editProductViewBinding = EditProductViewBinding.inflate(LayoutInflater.from(requireContext()))

        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(editProductViewBinding.root)
            .create()
        alertDialog.show()

        editProductViewBinding.apply {
            etProductName.setText(product.title)
            etQuantity.setText(product.quantity.toString())
            etUnit.setText(product.unit)
            etPrice.setText(product.price.toString())
            etStock.setText(product.stock.toString())
            product.discount?.let {
                etDiscount.setText(product.discount.toString())
            }
            etCategory.setText(product.category)
            etType.setText(product.type)

            btnEdit.setOnClickListener {
                etProductName.isEnabled = true
                etQuantity.isEnabled = true
                etUnit.isEnabled = true
                etPrice.isEnabled = true
                etStock.isEnabled = true
                etDiscount.isEnabled = true
                etCategory.isEnabled = true
                etType.isEnabled = true
                setAutoCompleteTextViews(editProductViewBinding)
                etDiscount.requestFocus()
            }

            btnSave.setOnClickListener {
                if(etProductName.isEnabled) {
                    lifecycleScope.launch {
                        product.title = etProductName.text.toString()
                        product.quantity = etQuantity.text.toString().toInt()
                        product.unit = etUnit.text.toString()
                        product.price = etPrice.text.toString().toInt()
                        product.stock = etStock.text.toString().toInt()
                        product.discount = if(etDiscount.text.toString().isNotEmpty()) etDiscount.text.toString().toInt() else null
                        product.category = etCategory.text.toString()
                        product.type = etType.text.toString()

                        viewModel.saveUpdatedProductValue(product)
                    }
                    Toast.makeText(requireContext(), "Product Updated Successfully", Toast.LENGTH_SHORT).show()
                }

                alertDialog.dismiss()

            }
        }

    }

    private fun setAutoCompleteTextViews(binding : EditProductViewBinding) {
        val allCategoriesList : ArrayList<String?> = arrayListOf()
        for(category in Constants.allProductCategory) {
            allCategoriesList.add(category.title)
        }
        val units = ArrayAdapter(requireContext(), R.layout.show_list, Constants.allUnitsOfProducts)
        val categories = ArrayAdapter(requireContext(), R.layout.show_list, allCategoriesList)
        val types = ArrayAdapter(requireContext(), R.layout.show_list, Constants.allProductTypes)

        binding.apply {
            etUnit.setAdapter(units)
            etCategory.setAdapter(categories)
            etType.setAdapter(types)

            imgArrowDown1.setOnClickListener { etUnit.showDropDown() }
            imgArrowDown2.setOnClickListener { etCategory.showDropDown() }
            imgArrowDown3.setOnClickListener { etType.showDropDown() }

        }
    }

    private fun onCategoryClicked(category: String) {
        showSkeletonLoader()
        setProductRecyclerView(category)
    }

    private fun setProductRecyclerView(category: String) {
        lifecycleScope.launch {
            viewModel.fetchAllProducts(category).collect {
                val productAdapter = ProductAdapter(this@HomeFragment, ::onEditButtonClick)
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