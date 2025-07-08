package com.powidev.coffeshop.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.powidev.coffeshop.Domain.ItemsModel
import com.powidev.coffeshop.databinding.ItemFavoriteBinding
import com.powidev.coffeshop.ViewModel.FavoriteViewModel

class FavoriteAdapter(
    private val viewModel: FavoriteViewModel,
    private val onItemRemoved: () -> Unit,
    private val onLoading: (Boolean) -> Unit
) : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    private var favoriteList = mutableListOf<ItemsModel>()
    private val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    fun setItemList(items: MutableList<ItemsModel>) {
        this.favoriteList = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = ItemFavoriteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(favoriteList[position])
    }

    override fun getItemCount(): Int = favoriteList.size

    inner class FavoriteViewHolder(private val binding: ItemFavoriteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ItemsModel) {
            binding.titleTxt.text = item.title
            binding.priceTxt.text = "$${item.price}"
            binding.ratingBar.rating = item.rating.toFloat()
            Glide.with(binding.picMain.context)
                .load(item.picUrl.firstOrNull())
                .into(binding.picMain)

            binding.removeFavoriteBtn.setOnClickListener {
                onLoading(true)
                viewModel.removeFromFavorites(item, uid).observeForever { success ->
                    onLoading(false)
                    if (success) {
                        Toast.makeText(binding.root.context, "Eliminado de favoritos", Toast.LENGTH_SHORT).show()
                        onItemRemoved()
                    } else {
                        Toast.makeText(binding.root.context, "Error al eliminar", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
