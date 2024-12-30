package com.example.blinkitadmin.adminHome

import android.annotation.SuppressLint
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
import androidx.activity.result.contract.ActivityResultContracts
import com.example.blinkitadmin.Constants
import com.example.blinkitadmin.R
import com.example.blinkitadmin.adapter.AdapterUploadImage
import com.example.blinkitadmin.databinding.FragmentAddProductBinding

class AddProductFragment : Fragment() {
    private lateinit var binding : FragmentAddProductBinding
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
        val units = ArrayAdapter(requireContext(), R.layout.show_list, Constants.allUnitsOfProducts)
        val categories = ArrayAdapter(requireContext(), R.layout.show_list, Constants.allProductCategory)
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