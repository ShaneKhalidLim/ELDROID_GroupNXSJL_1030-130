package com.limyusontudtud.souvseek

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction

class ShopActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop)

        val shopName = intent.getStringExtra("shopName")
        val shopImageResource = intent.getIntExtra("shopImage", R.drawable.default_shop_image)
        val shopItems = getShopItems(shopName)

        val shopNameTextView: TextView = findViewById(R.id.shopName)
        shopNameTextView.text = shopName

        val shopImageView: ImageView = findViewById(R.id.shopImage)
        shopImageView.setImageResource(shopImageResource)

        val shopItemsListView: ListView = findViewById(R.id.shopItemsListView)
        val adapter = ShopItemAdapter(this, shopItems)
        shopItemsListView.adapter = adapter

        shopItemsListView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val item = shopItems[position]
            val intent = Intent(this, ItemDetailActivity::class.java).apply {
                putExtra("itemName", item.name)
                putExtra("itemPrice", item.price)
                putExtra("itemQuantity", item.quantity)
                putExtra("itemImage", item.imageResource)
            }
            startActivity(intent)
        }
        val backBtn: ImageView = findViewById(R.id.backBtn)
        backBtn.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Add click listener to the locateImgBtn
        val locateImgBtn: ImageButton = findViewById(R.id.locateImgBtn)
        locateImgBtn.setOnClickListener {
            val location = when (shopName) {
                "Souvenir Island General Merchandise" -> "135-137 Carlos Palanca Street, Quiapo, Manila, Metro Manila, Philippines"
                "Kultura Filipino" -> "2/F, SM Makati Annex Building, Ayala Center, Makati, 1224 Metro Manila, Philippines"
                "Team Manila" -> "2/F, Power Plant, Makati City, 1210 Metro Manila, Philippines"
                else -> "Philippines" // Provide default location or handle accordingly
            }
            val title = shopName ?: "Shop Name"
            navigateToGoogleMap(location, title)
        }
    }

    private fun getShopItems(shopName: String?): ArrayList<ShopItem> {
        return when (shopName) {
            "Souvenir Island General Merchandise" -> arrayListOf(
                ShopItem("Straw hat", 10.0, 5, R.drawable.ic_strawhat),
                ShopItem("Wooden Slipper Keychains", 20.0, 3, R.drawable.ic_woodenslipper),
                ShopItem("Bamboo Cup", 30.0, 2, R.drawable.ic_cup)
            )
            "Kultura Filipino" -> arrayListOf(
                ShopItem("I <3 Philippines Shirt", 15.0, 10, R.drawable.ic_philippinesshirt),
                ShopItem("Striped Long Back Jusi Barong", 25.0, 6, R.drawable.ic_jusibarong),
                ShopItem("Sinta PDS Silk Skirt", 35.0, 4, R.drawable.ic_silkshirt)
            )
            "Team Manila" -> arrayListOf(
                ShopItem("TEAM MANILA RZL OBRERO TSHIRT BLACK", 695.0, 8, R.drawable.ic_obreroshirt),
                ShopItem("TEAM MANILA ANOTHER DAY TSHIRT BROWN", 695.0, 7, R.drawable.ic_anotherday),
                ShopItem("TEAM MANILA DISKARTE O DIPLOMA TSHIRT BLACK", 695.0, 9, R.drawable.ic_diskarte)
            )
            "Test Shop" -> arrayListOf(
                ShopItem("Test Item 1", 5.0, 12, R.drawable.ic_testimage),
                ShopItem("Test Item 2", 7.5, 15, R.drawable.ic_testimage),
                ShopItem("Test Item 3", 9.0, 10, R.drawable.ic_testimage)
            )
            else -> arrayListOf(ShopItem("No items found", 0.0, 0, R.drawable.default_shop_image))
        }
    }

    private fun navigateToGoogleMap(location: String, title: String) {
        val intent = Intent(this, GoogleMapActivity::class.java).apply {
            putExtra("location", location)
            putExtra("title", title)
        }
        startActivity(intent)
    }
}
