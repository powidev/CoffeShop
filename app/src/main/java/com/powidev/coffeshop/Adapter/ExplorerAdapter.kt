package com.powidev.coffeshop.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.powidev.coffeshop.Activity.DetailActivity
import com.powidev.coffeshop.Domain.ItemsModel
import com.powidev.coffeshop.databinding.ItemExplorerBinding

class ExplorerAdapter(private val items: List<ItemsModel>) :
    RecyclerView.Adapter<ExplorerAdapter.ViewHolder>() {

    private lateinit var context: Context

    inner class ViewHolder(val binding: ItemExplorerBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding = ItemExplorerBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.binding.nombreProducto.text = item.title
        holder.binding.precioProducto.text = "$${item.price}"

        Glide.with(context)
            .load(item.picUrl[0])  // Asegúrate que `picUrl` tiene al menos 1 imagen
            .into(holder.binding.imgProducto)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("object", item)
            context.startActivity(intent)
        }
    }
}
