package com.powidev.coffeshop.Activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.powidev.coffeshop.Adapter.PaymentAdapter
import com.powidev.coffeshop.Domain.PaymentModel
import com.powidev.coffeshop.R
import com.powidev.coffeshop.databinding.ActivityPaymentBinding

class PaymentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var paymentAdapter: PaymentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        setVariable()
        loadPaymentOptions()
    }

    private fun setVariable() {
        binding.backBtn.setOnClickListener {
            finish()
        }
    }

    private fun loadPaymentOptions() {
        val paymentOptions = listOf (
            PaymentModel(getString(R.string.cash), R.drawable.cash),
            PaymentModel(getString(R.string.yape), R.drawable.yape_app_logo_vector),
            PaymentModel(getString(R.string.paypal), R.drawable.paypal),
            PaymentModel(getString(R.string.credit_debit_card), R.drawable.credit_card)
        )

        recyclerView = binding.recyclerPayments
        recyclerView.layoutManager = LinearLayoutManager(this)
        paymentAdapter = PaymentAdapter(paymentOptions)
        recyclerView.adapter = paymentAdapter
    }
}