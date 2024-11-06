package com.limyusontudtud.souvseek.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.limyusontudtud.souvseek.R
import com.limyusontudtud.souvseek.ShopActivity

class HomeFragment : Fragment() {

    private lateinit var adapter: ShopAdapter
    private lateinit var shopNames: Array<String>
    private lateinit var filteredShopNames: ArrayList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val shopListView: ListView = view.findViewById(R.id.shopListView)
        val searchEditText: EditText = view.findViewById(R.id.inputSearch)

        shopNames = arrayOf(
            "Souvenir Island General Merchandise",
            "Kultura Filipino",
            "Team Manila",
            "Test Shop"
        )
        filteredShopNames = ArrayList(shopNames.toList())

        adapter = ShopAdapter(requireContext(), filteredShopNames)
        shopListView.adapter = adapter

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        return view
    }

    private fun filter(query: String) {
        filteredShopNames.clear()
        if (query.isEmpty()) {
            filteredShopNames.addAll(shopNames)
        } else {
            for (shop in shopNames) {
                if (shop.contains(query, ignoreCase = true)) {
                    filteredShopNames.add(shop)
                }
            }
        }
        adapter.notifyDataSetChanged()
    }

    private inner class ShopAdapter(private val context: Context, private val shopNames: ArrayList<String>) :
        ArrayAdapter<String>(context, R.layout.list_item_shop, shopNames) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view =
                convertView ?: LayoutInflater.from(context)
                    .inflate(R.layout.list_item_shop, parent, false)

            val shopNameTextView: TextView = view.findViewById(R.id.shopName)
            shopNameTextView.text = shopNames[position]

            val shopImageView: ImageView = view.findViewById(R.id.shopImage)
            when (shopNames[position]) {
                "Souvenir Island General Merchandise" -> shopImageView.setImageResource(R.drawable.first_shop)
                "Kultura Filipino" -> shopImageView.setImageResource(R.drawable.second_shop)
                "Team Manila" -> shopImageView.setImageResource(R.drawable.third_shop)
                "Test Shop" -> shopImageView.setImageResource(R.drawable.fourth_shop)
                else -> shopImageView.setImageResource(R.drawable.default_shop_image)
            }

            view.setOnClickListener {
                val intent = Intent(context, ShopActivity::class.java).apply {
                    putExtra("shopName", shopNames[position])
                    putExtra("shopImage", getShopImageResource(shopNames[position]))
                }
                context.startActivity(intent)
            }

            return view
        }

        private fun getShopImageResource(shopName: String): Int {
            return when (shopName) {
                "Souvenir Island General Merchandise" -> R.drawable.first_shop
                "Kultura Filipino" -> R.drawable.second_shop
                "Team Manila" -> R.drawable.third_shop
                "Test Shop" -> R.drawable.fourth_shop
                else -> R.drawable.default_shop_image
            }
        }
    }
}