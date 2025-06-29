package com.powidev.coffeshop.Activity

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.powidev.coffeshop.Adapter.ItemsListCategoryAdapter
import com.powidev.coffeshop.Domain.CategoryModel
import com.powidev.coffeshop.R
import com.powidev.coffeshop.ViewModel.MainViewModel
import com.powidev.coffeshop.databinding.ActivityProductoBinding

class ProductoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductoBinding
    private val viewModel = MainViewModel()
    private val categoriaList = mutableListOf<CategoryModel>()
    private lateinit var spinnerAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener { finish() }

        setupSpinner()
        loadCategorias()
    }

    private fun setupSpinner() {
        spinnerAdapter = ArrayAdapter(this, R.layout.spinner_item, mutableListOf())
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategorias.adapter = spinnerAdapter

        binding.spinnerCategorias.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {
                if (position >= 0 && position < categoriaList.size) {
                    val categoryId = categoriaList[position].id.toString()
                    cargarProductosPorCategoria(categoryId)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun loadCategorias() {
        viewModel.loadCategory().observe(this, Observer { categorias ->
            categoriaList.clear()
            categoriaList.addAll(categorias)
            val nombresCategorias = categorias.map { it.title }
            spinnerAdapter.clear()
            spinnerAdapter.addAll(nombresCategorias)
            spinnerAdapter.notifyDataSetChanged()
        })
    }

    private fun cargarProductosPorCategoria(categoryId: String) {
        viewModel.loadItems(categoryId).observe(this, Observer { productos ->
            binding.recyclerProductos.layoutManager = LinearLayoutManager(this)
            binding.recyclerProductos.adapter = ItemsListCategoryAdapter(productos)
        })
    }
}
