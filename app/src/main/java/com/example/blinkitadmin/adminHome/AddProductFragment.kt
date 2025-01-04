package com.example.blinkitadmin.adminHome

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.blinkitadmin.Constants
import com.example.blinkitadmin.Firebase
import com.example.blinkitadmin.R
import com.example.blinkitadmin.Utility
import com.example.blinkitadmin.activity.AdminHomeActivity
import com.example.blinkitadmin.adapter.AdapterUploadImage
import com.example.blinkitadmin.databinding.FragmentAddProductBinding
import com.example.blinkitadmin.model.Product
import com.example.blinkitadmin.viewmodel.AdminViewModel
import com.example.blinkitadmin.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

class AddProductFragment : Fragment() {
    private lateinit var binding : FragmentAddProductBinding
    private val viewModel : AdminViewModel by viewModels()
    private lateinit var formDetails : Array<EditText>
    private val imageUriList : ArrayList<Uri> = arrayListOf()
    private lateinit var imageAdapter: AdapterUploadImage

    private val uploadImageLauncher = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { listOfUris ->
        val fiveImages = listOfUris.take(5)
        imageUriList.addAll(fiveImages)
        binding.apply {
            if(imageUriList.isNotEmpty()) {
                tvAddImage.visibility = View.GONE
                tvErrorMsg2.visibility = View.GONE
            }
        }
        binding.rvImages.adapter = imageAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddProductBinding.inflate(layoutInflater)
        formDetails = arrayOf(binding.etProductName, binding.etQuantity, binding.etUnit, binding.etPrice, binding.etStock, binding.etCategory, binding.etType)
        imageAdapter = AdapterUploadImage(imageUriList, this)

        setAutoCompleteTextViews()
        setProductNameTextListener()
        setFormDetailsListener()
        onAddProductClick()
        binding.apply {
            btnUpload.setOnClickListener { uploadImageLauncher.launch("image/*") }
            btnDelete.setOnClickListener {
                imageUriList.clear()
                tvAddImage.visibility = View.VISIBLE
                imageAdapter.notifyDataSetChanged()
            }
        }

        return binding.root
    }

    private fun onAddProductClick() {

        binding.btnAddProduct.setOnClickListener {
            formDetails.forEach {
                if(it.text.toString().isEmpty()) {
                    binding.tvErrorMsg1.visibility = View.VISIBLE
                }
            }
            if(imageUriList.isEmpty()) {
                binding.tvErrorMsg2.visibility = View.VISIBLE
            }

            if(binding.tvErrorMsg1.visibility == View.GONE && binding.tvErrorMsg2.visibility == View.GONE) {
                val product = Product(
                    title = binding.etProductName.text.toString(),
                    quantity = binding.etQuantity.text.toString().toIntOrNull(),
                    unit = binding.etUnit.text.toString(),
                    price = binding.etPrice.text.toString().toIntOrNull(),
                    stock = binding.etStock.text.toString().toIntOrNull(),
                    category = binding.etCategory.text.toString(),
                    type = binding.etType.text.toString(),
                    itemCount = 0,
                    adminUid = Firebase.getCurrentUserId(),
                    id = Utility.generateRandomId(32)
                )

                saveImage(product)
            }
        }
    }

    private fun saveImage(product: Product) {
        binding.progressbar.visibility = View.VISIBLE
        Toast.makeText(requireContext(), "Uploading Images in Storage...", Toast.LENGTH_SHORT).show()

        viewModel.saveImageInStorage(imageUriList)

        lifecycleScope.launch {
            viewModel.isImageUploaded.collect {
                if(it) {
                    binding.progressbar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Images Uploaded.", Toast.LENGTH_SHORT).show()
                    getImageUrlsFromStorage(product)
                }
            }
        }
    }

    private fun getImageUrlsFromStorage(product : Product) {
        lifecycleScope.launch {
            viewModel.downloadedUrls.collect {
                val urls = it
                product.productImageUrl = urls
                saveProduct(product)
            }
        }
    }

    private fun saveProduct(product: Product) {
        binding.progressbar.visibility = View.VISIBLE
        Toast.makeText(requireContext(), "Saving Product in Firebase DB...", Toast.LENGTH_SHORT).show()

        viewModel.saveProductInDB(product)

        lifecycleScope.launch {
            viewModel.isProductSaved.collect {
                if(it) {
                    binding.progressbar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Product is Published.", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(requireActivity(), AdminHomeActivity::class.java))
                }
            }
        }
    }

    private fun setFormDetailsListener() {
        formDetails.forEach {
            it.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    binding.tvErrorMsg1.visibility = View.GONE
                }

                override fun afterTextChanged(s: Editable?) {
                }

            })
        }
    }

    private fun onProductNameCrossClick() {
        binding.crossBtn.setOnClickListener {
            binding.etProductName.setText("")
        }
    }

    private fun setProductNameTextListener() {
        onProductNameCrossClick()

        binding.etProductName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().isNotEmpty()) {
                    binding.crossBtn.visibility = View.VISIBLE
                } else {
                    binding.crossBtn.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    private fun setAutoCompleteTextViews() {
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
}