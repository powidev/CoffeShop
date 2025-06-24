package com.powidev.coffeshop.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.powidev.coffeshop.Domain.PaymentModel
import com.powidev.coffeshop.R

class PaymentAdapter(private val list: List<PaymentModel>) :
    RecyclerView.Adapter<PaymentAdapter.PaymentViewHolder>() {

    class PaymentViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val imagen: ImageView = itemview.findViewById(R.id.imgPaymentIcon)
        val text: TextView = itemview.findViewById(R.id.textPaymentType)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.payment_items, parent, false)
        return PaymentViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: PaymentViewHolder,
        position: Int
    ) {
        val payment = list[position]
        holder.imagen.setImageResource(payment.image)
        holder.text.text = payment.name
    }

    override fun getItemCount(): Int {
        return list.size
    }
}