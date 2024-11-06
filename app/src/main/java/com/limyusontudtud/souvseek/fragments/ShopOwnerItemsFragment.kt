package com.limyusontudtud.souvseek.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.limyusontudtud.souvseek.R

class ShopOwnerItemsFragment : Fragment() {

    private lateinit var itemListView: ListView
    private lateinit var addShopsButton: FloatingActionButton

    // Initialize the shop list and adapter
    private lateinit var shopList: MutableList<String>
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_shop_owner_items, container, false)
        itemListView = view.findViewById(R.id.itemListView)
        addShopsButton = view.findViewById(R.id.addShops)

        // Initialize the shop list and adapter
        shopList = mutableListOf()
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, shopList)
        itemListView.adapter = adapter

        addShopsButton.setOnClickListener {
            showAddShopDialog()
        }

        return view
    }

    private fun showAddShopDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Add Shop")

        val input = EditText(requireContext())
        input.hint = "Enter shop name"
        builder.setView(input)

        builder.setPositiveButton("Add") { dialog, _ ->
            val shopName = input.text.toString()
            if (shopName.isNotEmpty()) {
                addShop(shopName)
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun addShop(shopName: String) {
        // Add the shop name to the list and notify the adapter
        shopList.add(shopName)
        adapter.notifyDataSetChanged()
    }
}