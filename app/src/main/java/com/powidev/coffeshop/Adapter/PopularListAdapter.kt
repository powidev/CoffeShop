package com.powidev.coffeshop.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.powidev.coffeshop.Activity.DetailActivity
import com.powidev.coffeshop.Activity.DetailAdminActivity
import com.powidev.coffeshop.Domain.ItemsModel
import com.powidev.coffeshop.databinding.ViewholderPopularLeftBinding
import com.powidev.coffeshop.databinding.ViewholderPopularRightBinding
import com.powidev.coffeshop.manager.SessionManager

class PopularListAdapter(
    private val items: List<ItemsModel>,
    private val onRemoveClicked: (ItemsModel) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_LEFT = 0
        const val TYPE_RIGHT = 1
    }

    private lateinit var context: Context

    override fun getItemViewType(position: Int): Int {
        return if (position % 2 == 0) TYPE_LEFT else TYPE_RIGHT
    }

    class ViewHolderLeft(val binding: ViewholderPopularLeftBinding) :
        RecyclerView.ViewHolder(binding.root)

    class ViewHolderRight(val binding: ViewholderPopularRightBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return when (viewType) {
            TYPE_LEFT -> {
                val binding = ViewholderPopularLeftBinding.inflate(
                    LayoutInflater.from(context), parent, false
                )
                ViewHolderLeft(binding)
            }
            TYPE_RIGHT -> {
                val binding = ViewholderPopularRightBinding.inflate(
                    LayoutInflater.from(context), parent, false
                )
                ViewHolderRight(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]

        when (holder) {
            is ViewHolderLeft -> {
                val binding = holder.binding
                bindCommon(binding.root, binding.titleTxt.text.toString(), binding.priceTxt.text.toString(), binding.ratingBar.rating, item.picUrl[0])
                binding.titleTxt.text = item.title
                binding.priceTxt.text = "${item.price} USD"
                binding.ratingBar.rating = item.rating.toFloat()

                Glide.with(context).load(item.picUrl[0]).into(binding.picMain)

                binding.root.setOnClickListener { navigateToDetail(item) }

                if (SessionManager.isAdmin(context)) {
                    binding.btnRemovePopular.visibility = View.VISIBLE
                    binding.btnRemovePopular.setOnClickListener {
                        onRemoveClicked(item) // Se llama el callback
                    }
                } else {
                    binding.btnRemovePopular.visibility = View.GONE
                }
            }

            is ViewHolderRight -> {
                val binding = holder.binding
                bindCommon(binding.root, binding.titleTxt.text.toString(), binding.priceTxt.text.toString(), binding.ratingBar.rating, item.picUrl[0])
                binding.titleTxt.text = item.title
                binding.priceTxt.text = "${item.price} USD"
                binding.ratingBar.rating = item.rating.toFloat()

                Glide.with(context).load(item.picUrl[0]).into(binding.picMain)

                binding.root.setOnClickListener { navigateToDetail(item) }

                if (SessionManager.isAdmin(context)) {
                    binding.btnRemovePopular.visibility = View.VISIBLE
                    binding.btnRemovePopular.setOnClickListener {
                        onRemoveClicked(item) // Se llama el callback
                    }
                } else {
                    binding.btnRemovePopular.visibility = View.GONE
                }
            }
        }
    }

    private fun navigateToDetail(item: ItemsModel) {
        val destination = if (SessionManager.isAdmin(context)) {
            DetailAdminActivity::class.java
        } else {
            DetailActivity::class.java
        }

        val intent = Intent(context, destination).apply {
            putExtra("object", item)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    private fun bindCommon(view: View, title: String, price: String, rating: Float, imageUrl: String) {
        // Método opcional, por si se requiere factorizar el comportamiento común
    }
}
