package com.limyusontudtud.souvseek

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class TransactionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction)

        val itemName = intent.getStringExtra("itemName") ?: ""
        val itemPrice = intent.getDoubleExtra("itemPrice", 0.0)
        val itemQuantity = intent.getIntExtra("itemQuantity", 0)

        val itemNameTextView: TextView = findViewById(R.id.itemName)
        itemNameTextView.text = itemName

        val itemPriceTextView: TextView = findViewById(R.id.itemPrice)
        itemPriceTextView.text = getString(R.string.price_format, itemPrice)

        val itemQuantityTextView: TextView = findViewById(R.id.itemQuantity)
        itemQuantityTextView.text = "Quantity: $itemQuantity"

        val totalPriceTextView: TextView = findViewById(R.id.totalPrice)
        val totalPrice = itemPrice * itemQuantity
        totalPriceTextView.text = getString(R.string.price_format, totalPrice)

        val checkoutButton: Button = findViewById(R.id.checkoutButton2)
        checkoutButton.setOnClickListener {
            // Intent to start PaymentOptionActivity
            val intent = Intent(this@TransactionActivity, PaymentOptionActivity::class.java).apply {
                putExtra("totalPrice", totalPrice)
            }
            startActivity(intent)
        }
    }
}
