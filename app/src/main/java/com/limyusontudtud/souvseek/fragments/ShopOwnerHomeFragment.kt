package com.limyusontudtud.souvseek.fragments

import com.limyusontudtud.souvseek.R
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.limyusontudtud.souvseek.DataClass
import com.limyusontudtud.souvseek.DatabaseHelper
import com.limyusontudtud.souvseek.MyAdapter
import com.limyusontudtud.souvseek.UploadActivity

class ShopOwnerHomeFragment : Fragment() {

    private lateinit var fabAdd: FloatingActionButton
    private lateinit var fabUpdate: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var dataList: ArrayList<DataClass>
    private lateinit var adapter: MyAdapter
    private lateinit var searchView: SearchView
    private lateinit var dialog: AlertDialog
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_shop_owner_home, container, false)

        // Initialize views
        recyclerView = view.findViewById(R.id.recyclerView)
        fabAdd = view.findViewById(R.id.fab_add)
        fabUpdate = view.findViewById(R.id.fab_update)
        searchView = view.findViewById(R.id.search)

        searchView.clearFocus()
        recyclerView.layoutManager = GridLayoutManager(context, 1)

        // Initialize alert dialog for loading
        val builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(false)
        dialog = builder.create()

        // Initialize data and adapter
        dataList = ArrayList()
        adapter = MyAdapter(requireContext(), dataList)
        recyclerView.adapter = adapter

        // Initialize DatabaseHelper
        databaseHelper = DatabaseHelper(requireContext())

        // Load data from SQLite database
        loadDataFromSQLite()

        // Search functionality
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchList(newText)
                return true
            }
        })

        // Add new item
        fabAdd.setOnClickListener {
            startActivity(Intent(requireContext(), UploadActivity::class.java))
        }

        // Refresh data
        fabUpdate.setOnClickListener {
            dialog.show()
            loadDataFromSQLite()
        }

        return view
    }

    private fun loadDataFromSQLite() {
        dialog.show()
        dataList.clear()

        val cursor = databaseHelper.readAllShops()

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id")) // Fetch the ID
                val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
                val imageUrl = cursor.getString(cursor.getColumnIndexOrThrow("image_url"))

                val dataClass = DataClass(id, title, imageUrl)
                dataList.add(dataClass)
            } while (cursor.moveToNext())
        }
        cursor?.close()
        adapter.notifyDataSetChanged()
        dialog.dismiss()
    }

    private fun searchList(text: String?) {
        val searchList = ArrayList<DataClass>()
        for (dataClass in dataList) {
            if (dataClass.dataTitle!!.contains(text!!, true)) {
                searchList.add(dataClass)
            }
        }
        adapter.searchDataList(searchList)
    }
}
