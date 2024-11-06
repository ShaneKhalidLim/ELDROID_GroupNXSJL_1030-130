package com.limyusontudtud.souvseek

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WishlistAdapter(private val items: MutableList<WishlistItem>) :
    RecyclerView.Adapter<WishlistAdapter.WishlistViewHolder>() {

    var onDeleteClick: ((WishlistItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WishlistViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_wishlist, parent, false)
        return WishlistViewHolder(view)
    }

    override fun onBindViewHolder(holder: WishlistViewHolder, position: Int) {
        val item = items[position]
        holder.itemName.text = item.name
        holder.itemPrice.text = holder.itemView.context.getString(R.string.price_format, item.price)
        holder.itemImage.setImageResource(item.imageResource)

        holder.deleteButton.setOnClickListener {
            onDeleteClick?.invoke(item)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class WishlistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.wishlistItemName)
        val itemPrice: TextView = itemView.findViewById(R.id.wishlistItemPrice)
        val itemImage: ImageView = itemView.findViewById(R.id.wishlistItemImage)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
    }
}
