package com.powidev.coffeshop.Activity.payment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.paypal.android.corepayments.CoreConfig
import com.paypal.android.corepayments.Environment
import com.paypal.android.corepayments.PayPalSDKError
import com.paypal.android.paypalwebpayments.PayPalWebCheckoutClient
import com.paypal.android.paypalwebpayments.PayPalWebCheckoutFundingSource
import com.paypal.android.paypalwebpayments.PayPalWebCheckoutListener
import com.paypal.android.paypalwebpayments.PayPalWebCheckoutRequest
import com.paypal.android.paypalwebpayments.PayPalWebCheckoutResult
import com.powidev.coffeshop.Activity.MainActivity
import com.powidev.coffeshop.Domain.OrderDetailModel
import com.powidev.coffeshop.Domain.OrderModel
import com.powidev.coffeshop.Domain.PaymentType
import com.powidev.coffeshop.Helper.LoadingDialogFragment
import com.powidev.coffeshop.Helper.ManagmentCart
import com.powidev.coffeshop.Helper.OrderDBHelper
import com.powidev.coffeshop.Helper.OrderDetailDBHelper
import com.powidev.coffeshop.R
import com.powidev.coffeshop.databinding.ActivityPaypalBinding
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class PaypalActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaypalBinding
    private val loadingDialogFragment by lazy { LoadingDialogFragment() }

    //Paypal
    private lateinit var clientID: String
    private lateinit var secretID: String
    private lateinit var accessToken: String
    private lateinit var uniqueId: String
    private lateinit var orderId: String

    private val returnUrl = "com.powidev.coffeshop://paypalActivity"

    //SQLite
    private lateinit var orderDBHelper: OrderDBHelper
    private lateinit var orderDetailDBHelper: OrderDetailDBHelper
    private lateinit var managementCart: ManagmentCart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidNetworking.initialize(this)

        binding = ActivityPaypalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        managementCart = ManagmentCart(this)

        if (!readPaypalSecrets()) {
            Toast.makeText(this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show()
            finish()
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            fetchAccessToken()

            loadingDialogFragment.show(supportFragmentManager, "loader")

            Handler(mainLooper).postDelayed({
                if (loadingDialogFragment.isAdded) {
                    loadingDialogFragment.dismissAllowingStateLoss()
                }
                startPayment()
            }, 1500)
        }
    }

    private fun readPaypalSecrets(): Boolean {
        clientID = resources.getString(R.string.client_id)
        secretID = resources.getString(R.string.secret_id)

        if (!clientID.trim().isEmpty() || !secretID.trim().isEmpty()) {
            return true
        } else {
            Log.i(
                "PayPalAPI",
                "PayPal secrets have not been set up correctly, integration will not work"
            )
            return false
        }
    }

    private fun fetchAccessToken() {
        val encodedAuthString =
            Base64.encodeToString("$clientID:$secretID".toByteArray(), Base64.NO_WRAP)

        AndroidNetworking.post("https://api-m.sandbox.paypal.com/v1/oauth2/token")
            .addHeaders("Authorization", "Basic $encodedAuthString")
            .addHeaders("Content-Type", "application/x-www-form-urlencoded")
            .addBodyParameter("grant_type", "client_credentials")
            .setPriority(Priority.HIGH)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    accessToken = response.getString("access_token")

                    Toast.makeText(
                        this@PaypalActivity,
                        resources.getString(R.string.paypal_loaded_succesfully),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onError(error: ANError) {
                    Log.d("debug", error.errorBody)
                    Toast.makeText(
                        this@PaypalActivity,
                        resources.getString(R.string.paypal_could_not_load),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            })
    }

    private fun startPayment() {
        uniqueId = UUID.randomUUID().toString()

        val orderRequestJson = JSONObject().apply {
            put("intent", "CAPTURE")
            put("purchase_units", JSONArray().apply {
                put(JSONObject().apply {
                    put("reference_id", uniqueId)
                    put("amount", JSONObject().apply {
                        put("currency_code", "USD")
//                        put("value", "5.00")
                        put("value", managementCart.getCalculatedFee().toString())
                    })
                })
            })
            put("payment_source", JSONObject().apply {
                put("paypal", JSONObject().apply {
                    put("experience_context", JSONObject().apply {
                        put("payment_method_preference", "IMMEDIATE_PAYMENT_REQUIRED")
                        put("brand_name", "Coffee Shop S.A")
                        put("locale", "en-US")
                        put("landing_page", "LOGIN")
                        put("shipping_preference", "NO_SHIPPING")
                        put("user_action", "PAY_NOW")
                        put("return_url", returnUrl)
                        put("cancel_url", returnUrl)
                    })
                })
            })
        }

        AndroidNetworking.post("https://api-m.sandbox.paypal.com/v2/checkout/orders")
            .addHeaders("Authorization", "Bearer $accessToken")
            .addHeaders("Content-Type", "application/json")
            .addHeaders("PayPal-Request-Id", uniqueId)
            .addJSONObjectBody(orderRequestJson)
            .setPriority(Priority.HIGH)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    handleOrderID(response.getString("id"))
                }

                override fun onError(error: ANError) {
                    Log.d(
                        "debug",
                        "Order Error : ${error.message} || ${error.errorBody} || ${error.response}"
                    )
                    Toast.makeText(
                        this@PaypalActivity,
                        getString(R.string.something_went_wrong),
                        Toast.LENGTH_SHORT
                    ).show()
                    endActivity()
                }
            })
    }

    private fun handleOrderID(orderID: String) {
        val config = CoreConfig(clientID, environment = Environment.SANDBOX)
        val payPalWebCheckoutClient =
            PayPalWebCheckoutClient(this@PaypalActivity, config, returnUrl)
        payPalWebCheckoutClient.listener = object : PayPalWebCheckoutListener {
            override fun onPayPalWebSuccess(result: PayPalWebCheckoutResult) {
                Log.d("debug", "onPayPalWebSuccess: $result")
                Toast.makeText(
                    this@PaypalActivity,
                    resources.getString(R.string.payment_successful),
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onPayPalWebFailure(error: PayPalSDKError) {
                Log.d("debug", "onPayPalWebFailure: $error")
                Toast.makeText(
                    this@PaypalActivity,
                    resources.getString(R.string.something_went_wrong),
                    Toast.LENGTH_SHORT
                ).show()
                endActivity()
            }

            override fun onPayPalWebCanceled() {
                Log.d("debug", "onPayPalWebCanceled: ")
                endActivity()
            }
        }

        orderId = orderID
        val payPalWebCheckoutRequest =
            PayPalWebCheckoutRequest(orderID, fundingSource = PayPalWebCheckoutFundingSource.PAYPAL)
        payPalWebCheckoutClient.start(payPalWebCheckoutRequest)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d("debug", "onNewIntent: $intent")
        if (intent.data!!.getQueryParameter("opType") == "payment") {
            captureOrder(orderId)
            Toast.makeText(
                this,
                resources.getString(R.string.payment_successful),
                Toast.LENGTH_SHORT
            ).show()
        } else if (intent.data!!.getQueryParameter("opType") == "cancel") {
            Toast.makeText(
                this,
                resources.getString(R.string.payment_cancelled),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun captureOrder(orderID: String) {
        Log.d("debug", "captureOrder: $orderID")
        AndroidNetworking.post("https://api-m.sandbox.paypal.com/v2/checkout/orders/$orderID/capture")
            .addHeaders("Authorization", "Bearer $accessToken")
            .addHeaders("Content-Type", "application/json")
            .addJSONObjectBody(JSONObject())
            .setPriority(Priority.HIGH)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    Log.d("debug", "Capture Response : " + response.toString())
                    saveOrderInSQLite()
                }

                override fun onError(error: ANError) {
                    Log.e("debug", "Capture Error : " + error.errorDetail)
                    Toast.makeText(
                        this@PaypalActivity,
                        resources.getString(R.string.could_not_capture_order),
                        Toast.LENGTH_SHORT
                    ).show()
                    endActivity()
                }
            })
    }

    private fun endActivity() {
        finish()
        startActivity(Intent(this@PaypalActivity, MainActivity::class.java))
    }

    fun saveOrderInSQLite() {
        orderDBHelper = OrderDBHelper(this)
        orderDetailDBHelper = OrderDetailDBHelper(this)

        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = format.format(Date())

        val order = OrderModel(
            date = currentDate,
            totalPrice = managementCart.getCalculatedFee(),
            grossPrice = managementCart.getTotalFee(),
            paymentType = PaymentType.PAYPAL
        )
        val orderId = orderDBHelper.insertOrder(order)

        if (orderId.toInt() == -1) {
            Toast.makeText(
                this,
                resources.getString(R.string.something_went_wrong),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        managementCart.getListCart().forEach {
            val order = OrderDetailModel(it.title, it.price, it.numberInCart, orderId.toInt())
            orderDetailDBHelper.insertOrder(order)
        }
        managementCart.clearListCart()
    }
}