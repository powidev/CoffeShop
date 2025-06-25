package com.powidev.coffeshop.Activity

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.powidev.coffeshop.Adapter.PaymentAdapter
import com.powidev.coffeshop.Domain.PaymentModel
import com.powidev.coffeshop.R
import com.powidev.coffeshop.databinding.ActivityPaymentBinding
import androidx.core.content.edit

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
        handlePaymentMethod()
    }

    private fun setVariable() {
        binding.backBtn.setOnClickListener {
            finish()
        }
    }

    private fun loadPaymentOptions() {
        val paymentOptions = listOf(
            PaymentModel(getString(R.string.cash), R.drawable.cash),
            PaymentModel(getString(R.string.yape), R.drawable.yape_app_logo_vector),
            PaymentModel(getString(R.string.paypal), R.drawable.paypal),
            PaymentModel(getString(R.string.credit_debit_card), R.drawable.credit_card)
        )

        recyclerView = binding.recyclerPayments
        recyclerView.layoutManager = LinearLayoutManager(this)
        paymentAdapter = PaymentAdapter(paymentOptions) { paymentModel, position ->
            val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
            preferences.edit { putInt("payment_preference", position) }
        }
        recyclerView.adapter = paymentAdapter
    }

    private fun handlePaymentMethod() {
        binding.confirmButton.setOnClickListener {
            val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
            val selectedPaymentPosition = preferences.getInt("payment_preference", -1)

            if (selectedPaymentPosition == -1) {
                Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show()
            }

            //TODO: Optimize code (There is a lot of repetition) and implement payment activities
            if (selectedPaymentPosition == 0) {
                Toast.makeText(this, "Cash payment selected", Toast.LENGTH_SHORT).show()
                preferences.edit { putInt("payment_preference", -1) }
            } else if (selectedPaymentPosition == 1) {
                Toast.makeText(this, "Yape payment selected", Toast.LENGTH_SHORT).show()
                preferences.edit { putInt("payment_preference", -1) }
            } else if (selectedPaymentPosition == 2) {
                Toast.makeText(this, "PayPal payment selected", Toast.LENGTH_SHORT).show()
                preferences.edit { putInt("payment_preference", -1) }
            } else if (selectedPaymentPosition == 3) {
                Toast.makeText(this, "Credit/Debit Card payment selected", Toast.LENGTH_SHORT)
                    .show()
                preferences.edit { putInt("payment_preference", -1) }
            }

        }
    }
}