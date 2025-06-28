package com.powidev.coffeshop.Activity.payment

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.powidev.coffeshop.Activity.MainActivity
import com.powidev.coffeshop.Adapter.PaymentAdapter
import com.powidev.coffeshop.Domain.PaymentModel
import com.powidev.coffeshop.Helper.LoadingDialogFragment
import com.powidev.coffeshop.R
import com.powidev.coffeshop.databinding.ActivityPaymentBinding

class PaymentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var paymentAdapter: PaymentAdapter
    private lateinit var preferences: SharedPreferences
    private val loadingDialogFragment by lazy { LoadingDialogFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        preferences = PreferenceManager.getDefaultSharedPreferences(this)
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
            preferences.edit { putInt("payment_preference", position) }
        }
        recyclerView.adapter = paymentAdapter
    }

    private fun handlePaymentMethod() {
        binding.confirmButton.setOnClickListener {
            val selectedPaymentPosition = preferences.getInt("payment_preference", -1)

            when (selectedPaymentPosition) {
                0 -> handleCashPayment()
                1 -> Toast.makeText(this, "Yape payment selected", Toast.LENGTH_SHORT).show()
                2 -> handlePaypalPayment()
                3 -> Toast.makeText(this, "Credit/Debit Card payment selected", Toast.LENGTH_SHORT)
                    .show()

                else -> Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT)
                    .show()
            }

            //Reset to unselected for future purchases
            preferences.edit { putInt("payment_preference", -1) }
        }
    }

    private fun handleCashPayment() {
        val cashPayment = CashPaymentActivity(this)
        loadingDialogFragment.show(supportFragmentManager, "loader")

        Handler(mainLooper).postDelayed({
            if (loadingDialogFragment.isAdded) {
                loadingDialogFragment.dismissAllowingStateLoss()
            }
            cashPayment.saveOrder()
            startActivity(Intent(this, MainActivity::class.java))
        }, 1500)
    }

    private fun handlePaypalPayment() {
        startActivity(Intent(this, PaypalActivity::class.java))
    }
}