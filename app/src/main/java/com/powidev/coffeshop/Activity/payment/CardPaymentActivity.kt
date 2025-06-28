package com.powidev.coffeshop.Activity.payment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.powidev.coffeshop.Activity.MainActivity
import com.powidev.coffeshop.Domain.OrderDetailModel
import com.powidev.coffeshop.Domain.OrderModel
import com.powidev.coffeshop.Domain.PaymentType
import com.powidev.coffeshop.Helper.LoadingDialogFragment
import com.powidev.coffeshop.Helper.ManagmentCart
import com.powidev.coffeshop.Helper.OrderDBHelper
import com.powidev.coffeshop.Helper.OrderDetailDBHelper
import com.powidev.coffeshop.R
import com.powidev.coffeshop.databinding.ActivityCardPaymentBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CardPaymentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCardPaymentBinding

    private val loadingDialogFragment by lazy { LoadingDialogFragment() }

    private lateinit var orderDBHelper: OrderDBHelper
    private lateinit var orderDetailDBHelper: OrderDetailDBHelper
    lateinit var managementCart: ManagmentCart

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCardPaymentBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        enableEdgeToEdge()

        setVariables()
        startPayment()
    }

    private fun startPayment() {
        binding.confirmButton.setOnClickListener {
            val cc = binding.textCc.text.toString().trim()
            val expirationDate = binding.textDate.text.toString().trim()
            val cvv = binding.textCvv.text.toString().trim()

            if (cc.isEmpty()) {
                binding.textCc.error = resources.getString(R.string.required)
                binding.textCc.requestFocus()
                return@setOnClickListener
            } else if (expirationDate.isEmpty()) {
                binding.textDate.error = resources.getString(R.string.required)
                binding.textDate.requestFocus()
                return@setOnClickListener
            } else if (cvv.isEmpty()) {
                binding.textCvv.error = resources.getString(R.string.required)
                binding.textCvv.requestFocus()
                return@setOnClickListener
            } else {
                loadingDialogFragment.show(supportFragmentManager, "loader")

                Handler(mainLooper).postDelayed({
                    if (loadingDialogFragment.isAdded) {
                        loadingDialogFragment.dismissAllowingStateLoss()
                    }
                    saveOrder()
                    startActivity(Intent(this, MainActivity::class.java))
                }, 1500)

                Toast.makeText(
                    this,
                    resources.getString(R.string.payment_successful),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun saveOrder() {
        orderDBHelper = OrderDBHelper(this)
        orderDetailDBHelper = OrderDetailDBHelper(this)
        managementCart = ManagmentCart(this)

        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = format.format(Date())

        val order = OrderModel(
            date = currentDate,
            totalPrice = managementCart.getCalculatedFee(),
            grossPrice = managementCart.getTotalFee(),
            paymentType = PaymentType.CREDIT_CARD
        )
        val orderId = orderDBHelper.insertOrder(order)

        if (orderId.toInt() == -1) {
            Toast.makeText(this, this.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT)
                .show()
            return
        }

        managementCart.getListCart().forEach {
            val order = OrderDetailModel(it.title, it.price, it.numberInCart, orderId.toInt())
            orderDetailDBHelper.insertOrder(order)
        }
        managementCart.clearListCart()
    }

    fun setVariables() {
        binding.backBtn.setOnClickListener {
            finish()
        }
    }
}