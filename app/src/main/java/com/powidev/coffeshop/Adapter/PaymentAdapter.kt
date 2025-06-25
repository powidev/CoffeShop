package com.powidev.coffeshop.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.powidev.coffeshop.Domain.PaymentModel
import com.powidev.coffeshop.R

class PaymentAdapter(
    private val list: List<PaymentModel>,
    private val onItemClicked: (PaymentModel, Int) -> Unit
) : RecyclerView.Adapter<PaymentAdapter.PaymentViewHolder>() {

    private var selectedPosition = -1

    class PaymentViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val imagen: ImageView = itemview.findViewById(R.id.imgPaymentIcon)
        val text: TextView = itemview.findViewById(R.id.textPaymentType)
        val card: MaterialCardView = itemview.findViewById(R.id.paymentCard)
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
        val currentCard = holder.card

        currentCard.isChecked = selectedPosition == position
        currentCard.strokeWidth = if (currentCard.isChecked) 5 else 0

        currentCard.setOnClickListener {
            val previousSelectedPosition = selectedPosition
            selectedPosition = holder.adapterPosition

            if (previousSelectedPosition != -1 && previousSelectedPosition != selectedPosition) {
                notifyItemChanged(previousSelectedPosition)
            }

            onItemClicked(payment, position)

            currentCard.isChecked = true
            currentCard.strokeWidth = 5
            currentCard.setCardForegroundColor(
                ContextCompat.getColorStateList(
                    holder.itemView.context,
                    android.R.color.transparent
                )
            )
        }

        holder.imagen.setImageResource(payment.image)
        holder.text.text = payment.name
    }

    override fun getItemCount(): Int {
        return list.size
    }
}