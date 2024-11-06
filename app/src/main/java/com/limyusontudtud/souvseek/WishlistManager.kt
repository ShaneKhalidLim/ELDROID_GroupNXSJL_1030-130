package com.limyusontudtud.souvseek

object WishlistManager {
    private val wishlist = mutableListOf<WishlistItem>()

    fun addItem(item: WishlistItem) {
        wishlist.add(item)
    }

    fun removeItem(item: WishlistItem) {
        wishlist.remove(item)
    }

    fun getWishlist(): MutableList<WishlistItem> {
        return wishlist
    }
}
