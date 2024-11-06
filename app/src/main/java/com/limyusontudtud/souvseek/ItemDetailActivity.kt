package com.limyusontudtud.souvseek

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ItemDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_detail)

        val itemName = intent.getStringExtra("itemName") ?: ""
        val itemPrice = intent.getDoubleExtra("itemPrice", 0.0)
        val itemQuantity = intent.getIntExtra("itemQuantity", 0)
        val itemImageResource = intent.getIntExtra("itemImage", R.drawable.default_shop_image)

        val itemNameTextView: TextView = findViewById(R.id.itemName)
        itemNameTextView.text = itemName

        val itemPriceTextView: TextView = findViewById(R.id.itemPrice)
        itemPriceTextView.text = getString(R.string.price_format, itemPrice)

        val itemQuantityTextView: TextView = findViewById(R.id.itemQuantity)
        itemQuantityTextView.text = getString(R.string.quantity_format, itemQuantity)

        val itemImageView: ImageView = findViewById(R.id.itemImage)
        itemImageView.setImageResource(itemImageResource)

        val buyButton: Button = findViewById(R.id.buyButton)
        buyButton.setOnClickListener {
            val intent = Intent(this, ShoppingCartActivity::class.java)
            intent.putExtra("itemName", itemName)
            intent.putExtra("itemImage", itemImageResource)
            intent.putExtra("itemPrice", itemPrice)
            startActivity(intent)
        }

        val addToWishlistButton: Button = findViewById(R.id.addToWishlistButton)
        addToWishlistButton.setOnClickListener {
            WishlistManager.addItem(WishlistItem(itemName, itemPrice, itemImageResource))

            // Inflate custom toast layout
            val inflater: LayoutInflater = layoutInflater
            val layout = inflater.inflate(R.layout.custom_toast, null)

            // Set text and image for the toast
            val toastText: TextView = layout.findViewById(R.id.toast_text)
            toastText.text = "Added to wishlist"
            val toastImage: ImageView = layout.findViewById(R.id.toast_image)
            toastImage.setImageResource(R.drawable.souvseeklogowithmotto)  // Set your logo here

            // Create and show the custom toast
            val toast = Toast(applicationContext)
            toast.duration = Toast.LENGTH_SHORT
            toast.view = layout
            toast.show()
        }
    }
}
