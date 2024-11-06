package com.limyusontudtud.souvseek

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment

class ItemDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_item_detail, container, false)
        val itemDetailTextView: TextView = view.findViewById(R.id.itemDetailTextView)
        val priceEditText: EditText = view.findViewById(R.id.priceEditText)
        val quantityEditText: EditText = view.findViewById(R.id.quantityEditText)
        val itemName = arguments?.getString("itemName")
        itemDetailTextView.text = "Details of $itemName"

        priceEditText.setText("10.00")
        quantityEditText.setText("1")

        return view
    }

}