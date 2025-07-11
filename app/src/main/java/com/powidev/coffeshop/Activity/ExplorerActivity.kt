package com.powidev.coffeshop.Activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.powidev.coffeshop.Adapter.ExplorerAdapter
import com.powidev.coffeshop.Adapter.PopularAdapter
import com.powidev.coffeshop.ViewModel.MainViewModel
import com.powidev.coffeshop.databinding.ActivityExplorerBinding

class ExplorerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExplorerBinding
    private val viewModel = MainViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityExplorerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBackButton()
        setupEstadoAnimo()
        loadPopulares()
        loadNuevos()
    }

    private fun setupBackButton() {
        binding.backBtn.setOnClickListener {
            finish()
        }
    }

    private fun loadPopulares() {
        binding.progressBar.visibility = View.VISIBLE
        viewModel.loadPopular().observe(this) { listaPopulares ->
            if (listaPopulares != null) {
                binding.recyclerPopulares.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                binding.recyclerPopulares.adapter = ExplorerAdapter(listaPopulares)
            }
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun loadNuevos() {
        viewModel.loadNuevos().observe(this) { nuevosList ->
            binding.recyclerNuevos.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            binding.recyclerNuevos.adapter = ExplorerAdapter(nuevosList.toMutableList())
        }
    }

    private fun setupEstadoAnimo() {
        binding.frioBtn.setOnClickListener {
            Toast.makeText(this, "Recomendaciones para días fríos ☕", Toast.LENGTH_SHORT).show()
            // Filtrado o recomendaciones aquí
        }

        binding.relajanteBtn.setOnClickListener {
            Toast.makeText(this, "Recomendaciones relajantes 😌", Toast.LENGTH_SHORT).show()
            // Filtrado o recomendaciones aquí
        }
    }
}