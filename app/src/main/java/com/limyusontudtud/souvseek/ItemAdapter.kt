package com.limyusontudtud.souvseek

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.limyusontudtud.souvseek.databinding.ListItemBinding

class ItemAdapter(
    private val items: List<ShopItem>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(item: ShopItem)
        fun onAddToWishlistClick(item: ShopItem)
    }

    inner class ItemViewHolder(private val binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ShopItem) {
            binding.itemName.text = item.name
            binding.itemPrice.text = "₱ ${item.price}"
            binding.itemImage.setImageResource(item.imageResource)

            binding.root.setOnClickListener {
                listener.onItemClick(item)
            }

            binding.addToWishlistButton.setOnClickListener {
                listener.onAddToWishlistClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return items.size
    }
}