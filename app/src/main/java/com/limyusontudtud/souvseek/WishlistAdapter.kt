package com.limyusontudtud.souvseek

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WishlistAdapter(
    private val items: List<WishlistItem>,
    private val onDeleteClick: (WishlistItem) -> Unit
) : RecyclerView.Adapter<WishlistAdapter.WishlistViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WishlistViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_wishlist, parent, false)
        return WishlistViewHolder(view, onDeleteClick)
    }

    override fun onBindViewHolder(holder: WishlistViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    class WishlistViewHolder(
        itemView: View,
        private val onDeleteClick: (WishlistItem) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val itemName: TextView = itemView.findViewById(R.id.wishlistItemName)
        private val itemPrice: TextView = itemView.findViewById(R.id.wishlistItemPrice)
        private val itemImage: ImageView = itemView.findViewById(R.id.wishlistItemImage)
        private val deleteButton: Button = itemView.findViewById(R.id.deleteButton)

        fun bind(item: WishlistItem) {
            itemName.text = item.name
            itemPrice.text = itemView.context.getString(R.string.price_format, item.price)
            itemImage.setImageResource(item.imageResource)
            deleteButton.setOnClickListener { onDeleteClick(item) }
        }
    }
}
