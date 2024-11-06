package com.limyusontudtud.souvseek

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.limyusontudtud.souvseek.databinding.ActivityShoppingCartBinding

class ShoppingCartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShoppingCartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShoppingCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val itemName = intent.getStringExtra("itemName")
        val itemImageResource = intent.getIntExtra("itemImage", R.drawable.default_shop_image)
        val itemPrice = intent.getDoubleExtra("itemPrice", 0.0)

        itemName?.let {
            binding.itemNameTextView.text = it
        }
        binding.itemImageView.setImageResource(itemImageResource)

        val itemQuantityEditText: EditText = findViewById(R.id.itemQuantityEditText)
        val totalPriceTextView: TextView = findViewById(R.id.totalPrice)
        totalPriceTextView.text = getString(R.string.price_format, itemPrice)

        itemQuantityEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val quantity = s.toString().toIntOrNull() ?: 0
                val totalPrice = itemPrice * quantity
                totalPriceTextView.text = getString(R.string.price_format, totalPrice)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        val checkoutButton: Button = findViewById(R.id.checkoutButton)
        checkoutButton.setOnClickListener {
            val quantity = itemQuantityEditText.text.toString().toIntOrNull() ?: 0
            if (quantity > 0) {
                val intent = Intent(this, TransactionActivity::class.java)
                intent.putExtra("itemName", itemName)
                intent.putExtra("itemImage", itemImageResource)
                intent.putExtra("itemPrice", itemPrice)
                intent.putExtra("itemQuantity", quantity)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please enter a valid quantity", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
