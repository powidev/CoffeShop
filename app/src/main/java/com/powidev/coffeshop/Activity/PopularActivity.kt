package com.powidev.coffeshop.Activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.powidev.coffeshop.Adapter.PopularListAdapter
import com.powidev.coffeshop.ViewModel.MainViewModel
import com.powidev.coffeshop.databinding.ActivityPopularBinding

class PopularActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPopularBinding
    private val viewModel = MainViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPopularBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        loadPopularItems()
    }

    private fun setupUI() {
        binding.apply {
            backBtn.setOnClickListener { finish() }
            popularRecyclerView.layoutManager = LinearLayoutManager(
                this@PopularActivity,
                LinearLayoutManager.VERTICAL,
                false
            )
        }
    }

    private fun loadPopularItems() {
        binding.progressBar.visibility = View.VISIBLE

        viewModel.loadPopular().observe(this, Observer { items ->
            binding.progressBar.visibility = View.GONE

            // Configura el adapter con el callback de eliminación
            binding.popularRecyclerView.adapter = PopularListAdapter(items) { itemToRemove ->
                viewModel.removeFromPopular(itemToRemove).observe(this) { success ->
                    if (success) {
                        Toast.makeText(this, "Quitado de populares", Toast.LENGTH_SHORT).show()
                        loadPopularItems() // Recargar la lista actualizada
                    } else {
                        Toast.makeText(this, "Error al quitar", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}
