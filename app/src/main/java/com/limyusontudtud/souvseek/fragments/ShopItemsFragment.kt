package com.limyusontudtud.souvseek.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.limyusontudtud.souvseek.R

class ShopItemsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_shop_items, container, false)
    }

    companion object {
        private const val ARG_SHOP_NAME = "shop_name"

        fun newInstance(shopName: String) = ShopItemsFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_SHOP_NAME, shopName)
            }
        }
    }
}
