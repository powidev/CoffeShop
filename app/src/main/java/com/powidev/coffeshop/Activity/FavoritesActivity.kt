package com.powidev.coffeshop.Activity

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.powidev.coffeshop.Adapter.FavoriteAdapter
import com.powidev.coffeshop.databinding.ActivityFavoritesBinding
import com.powidev.coffeshop.ViewModel.FavoriteViewModel

class FavoritesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoritesBinding
    private lateinit var viewModel: FavoriteViewModel
    private lateinit var adapter: FavoriteAdapter
    private lateinit var uid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        viewModel = ViewModelProvider(this)[FavoriteViewModel::class.java]

        initRecyclerView()
        loadFavorites()
        setupBackButton()
    }

    private fun initRecyclerView() {
        adapter = FavoriteAdapter(viewModel,
            onItemRemoved = { loadFavorites() },
            onLoading = { showLoading(true) }
        )
        binding.favoriteRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.favoriteRecyclerView.adapter = adapter
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }


    private fun loadFavorites() {
        binding.progressBar.visibility = View.VISIBLE
        viewModel.loadFavorites(uid).observe(this) { list ->
            binding.progressBar.visibility = View.GONE
            adapter.setItemList(list)
            checkIfEmpty()
        }
    }

    private fun checkIfEmpty() {
        val isEmpty = adapter.itemCount == 0
        binding.favoriteRecyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
        binding.emptyText.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }

    private fun setupBackButton() {
        binding.backBtn.setOnClickListener {
            finish()
        }
    }
}