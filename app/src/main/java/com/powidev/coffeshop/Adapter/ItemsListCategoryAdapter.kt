package com.powidev.coffeshop.Adapter

import android.content.Intent
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.powidev.coffeshop.Activity.DetailActivity
import com.powidev.coffeshop.Activity.DetailAdminActivity
import com.powidev.coffeshop.Domain.ItemsModel
import com.powidev.coffeshop.databinding.ViewholderItemPicLeftBinding
import com.powidev.coffeshop.databinding.ViewholderItemPicRightBinding
import com.powidev.coffeshop.manager.SessionManager

class ItemsListCategoryAdapter(val items: MutableList<ItemsModel>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_ITEM1 = 0
        const val TYPE_ITEM2 = 1
    }

    lateinit var context: Context

    override fun getItemViewType(position: Int): Int {
        return if(position % 2 == 0) TYPE_ITEM1 else TYPE_ITEM2
    }

    class ViewholderItem1(val binding: ViewholderItemPicRightBinding):
        RecyclerView.ViewHolder(binding.root)

    class ViewholderItem2(val binding: ViewholderItemPicLeftBinding):
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return when (viewType) {
            TYPE_ITEM1 -> {
                val binding = ViewholderItemPicRightBinding.inflate(
                    LayoutInflater.from(context),
                    parent, false
                )
                ViewholderItem1(binding)
            }
            TYPE_ITEM2 -> {
                val binding = ViewholderItemPicLeftBinding.inflate(
                    LayoutInflater.from(context),
                    parent, false
                )
                ViewholderItem2(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]

        fun bindCommonData(
            titleTxt: String,
            priceTxt: String,
            rating: Float,
            picUrl: String
        ) {
            when(holder) {
                is ViewholderItem1 -> {
                    holder.binding.titleTxt.text = titleTxt
                    holder.binding.priceTxt.text = priceTxt
                    holder.binding.ratingBar.rating = rating

                    Glide.with(context)
                        .load(picUrl)
                        .into(holder.binding.picMain)

                    holder.itemView.setOnClickListener {
                        navigateToDetail(item, position)
                    }
                }

                is ViewholderItem2 -> {
                    holder.binding.titleTxt.text = titleTxt
                    holder.binding.priceTxt.text = priceTxt
                    holder.binding.ratingBar.rating = rating

                    Glide.with(context)
                        .load(picUrl)
                        .into(holder.binding.picMain)

                    holder.itemView.setOnClickListener {
                        navigateToDetail(item, position)
                    }
                }
            }
        }

        bindCommonData(
            item.title,
            priceTxt = "${item.price} USD",
            item.rating.toFloat(),
            item.picUrl[0]
        )
    }

    private fun navigateToDetail(item: ItemsModel, position: Int) {
        val destination = if (SessionManager.isAdmin(context)) {
            DetailAdminActivity::class.java
        } else {
            DetailActivity::class.java
        }

        Intent(context, destination).apply {
            putExtra("object", items[position])
            context.startActivity(this)
        }
    }
}