package com.powidev.coffeshop.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.powidev.coffeshop.Domain.CategoryModel
import com.powidev.coffeshop.R

class ManagerCategoryAdapter(
    private val categories: List<CategoryModel>,
    private val onDelete: (CategoryModel) -> Unit
) : RecyclerView.Adapter<ManagerCategoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = categories[position]
        holder.bind(category)
        holder.deleteButton.setOnClickListener { onDelete(category) }
    }

    override fun getItemCount(): Int = categories.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.textCategoryTitle)
        val deleteButton: ImageView = itemView.findViewById(R.id.imageDelete)

        fun bind(category: CategoryModel) {
            titleText.text = category.title
        }
    }
}
