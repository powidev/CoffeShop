package com.powidev.coffeshop.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.powidev.coffeshop.Domain.OrderModel
import com.powidev.coffeshop.R

class OrdersAdapter(
    private val list: List<OrderModel>
) : RecyclerView.Adapter<OrdersAdapter.OrdersViewHolder>() {

    class OrdersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val orderId: TextView = itemView.findViewById(R.id.textOrderId)
        val orderDate: TextView = itemView.findViewById(R.id.textOrderDate)
        val orderPrice: TextView = itemView.findViewById(R.id.textOrderPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.order_items, parent, false)
        return OrdersViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        val order = list[position]

        val orderIdFmt : String = holder.itemView.context.getString(R.string.order_id) + " " + order.orderID.toString()
        val orderDateFmt : String = holder.itemView.context.getString(R.string.date) + " " + order.date.toString()
        val orderPriceFmt : String = holder.itemView.context.getString(R.string.total_price) + " " + order.totalPrice.toString()

        holder.orderId.text =  orderIdFmt
        holder.orderDate.text = orderDateFmt
        holder.orderPrice.text = orderPriceFmt
    }

    override fun getItemCount(): Int {
        return list.size
    }
}