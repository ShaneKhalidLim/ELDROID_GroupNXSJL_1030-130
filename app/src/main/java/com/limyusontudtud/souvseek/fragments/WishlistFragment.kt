package com.limyusontudtud.souvseek

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class WishlistFragment : Fragment() {

    private lateinit var wishlistRecyclerView: RecyclerView
    private lateinit var wishlistAdapter: WishlistAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_wishlist, container, false)
        wishlistRecyclerView = view.findViewById(R.id.wishlistRecyclerView)
        wishlistRecyclerView.layoutManager = LinearLayoutManager(context)

        // Initialize wishlist adapter with data
        wishlistAdapter = WishlistAdapter(WishlistManager.getWishlist())
        wishlistRecyclerView.adapter = wishlistAdapter

        wishlistAdapter.onDeleteClick = { item ->
            WishlistManager.removeItem(item)
            wishlistAdapter.notifyDataSetChanged()
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        // Update the wishlist when fragment resumes
        wishlistAdapter.notifyDataSetChanged()
    }
}
