package com.limyusontudtud.souvseek

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.limyusontudtud.souvseek.databinding.ActivityPaymentOptionBinding
import com.paypal.checkout.PayPalCheckout
import com.paypal.checkout.approve.OnApprove
import com.paypal.checkout.cancel.OnCancel
import com.paypal.checkout.createorder.CreateOrder
import com.paypal.checkout.createorder.CurrencyCode
import com.paypal.checkout.createorder.OrderIntent
import com.paypal.checkout.createorder.UserAction
import com.paypal.checkout.error.OnError
import com.paypal.checkout.order.Amount
import com.paypal.checkout.order.AppContext
import com.paypal.checkout.order.OrderRequest
import com.paypal.checkout.order.PurchaseUnit

class PaymentOptionActivity : ComponentActivity() {

    lateinit var binding: ActivityPaymentOptionBinding
    val TAG = "MyTag"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPaymentOptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve the total price value from the intent extras
        val isSubscription = intent.getBooleanExtra("isSubscription", false)
        val totalPrice = if (isSubscription) {
            intent.getDoubleExtra("subscriptionPrice", 0.0)
        } else {
            intent.getDoubleExtra("totalPrice", 0.0)
        }

        binding.paymentButtonContainer.setup(
            createOrder = CreateOrder { createOrderActions ->
                val order = OrderRequest(
                    intent = OrderIntent.CAPTURE,
                    appContext = AppContext(userAction = UserAction.PAY_NOW),
                    purchaseUnitList = listOf(
                        PurchaseUnit(
                            amount = Amount(currencyCode = CurrencyCode.USD, value = totalPrice.toString())
                        )
                    )
                )
                createOrderActions.create(order)
            },
            onApprove = OnApprove { approval ->
                approval.orderActions.capture { captureOrderResult ->
                    Log.d(TAG, "CaptureOrderResult: $captureOrderResult")
                    Toast.makeText(this, if (isSubscription) "Subscription Successful" else "Payment Successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, if (isSubscription) ShopOwnerDashboardActivity::class.java else DashboardActivity::class.java))
                }
            },
            onCancel = OnCancel {
                Log.d(TAG, "Buyer Cancelled This Purchase")
                Toast.makeText(this, if (isSubscription) "Subscription Cancelled" else "Payment Cancelled", Toast.LENGTH_SHORT).show()
            },
            onError = OnError { errorInfo ->
                Log.d(TAG, "Error: $errorInfo")
                Toast.makeText(this, if (isSubscription) "Subscription Error" else "Payment Error", Toast.LENGTH_SHORT).show()
            }
        )
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
