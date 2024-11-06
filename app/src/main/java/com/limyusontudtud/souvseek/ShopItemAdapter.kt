package com.limyusontudtud.souvseek

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView


class ShopItemAdapter(context: Context, private val items: List<ShopItem>) : ArrayAdapter<ShopItem>(context, 0, items) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.shop_item_layout, parent, false)

        val item = items[position]

        val itemNameTextView: TextView = view.findViewById(R.id.itemName)
        val itemImageView: ImageView = view.findViewById(R.id.itemImage)

        itemNameTextView.text = item.name
        itemImageView.setImageResource(item.imageResource)

        return view
    }
}