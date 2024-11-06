package com.limyusontudtud.souvseek.fragments

import com.limyusontudtud.souvseek.R
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.limyusontudtud.souvseek.DataClass
import com.limyusontudtud.souvseek.MyAdapter
import com.limyusontudtud.souvseek.UploadActivity

class ShopOwnerHomeFragment : Fragment() {

    private lateinit var fabAdd: FloatingActionButton
    private lateinit var fabUpdate: FloatingActionButton
    private lateinit var databaseReference: DatabaseReference
    private lateinit var eventListener: ValueEventListener
    private lateinit var recyclerView: RecyclerView
    private lateinit var dataList: ArrayList<DataClass>
    private lateinit var adapter: MyAdapter
    private lateinit var searchView: SearchView
    private val PREFS_NAME = "ShopOwnerHomePrefs"
    private val DATA_LIST_KEY = "DataList"
    private lateinit var dialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_shop_owner_home, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        fabAdd = view.findViewById(R.id.fab_add)
        fabUpdate = view.findViewById(R.id.fab_update)
        searchView = view.findViewById(R.id.search)

        searchView.clearFocus()
        recyclerView.layoutManager = GridLayoutManager(context, 1)

        val builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(false)
//        builder.setView(R.layout.progress_layout)
        dialog = builder.create()

        dataList = ArrayList()
        adapter = MyAdapter(requireContext(), dataList)
        recyclerView.adapter = adapter

        databaseReference = FirebaseDatabase.getInstance().getReference("Android Tutorials")

        // Load saved data
        loadSavedData()

        eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dataList.clear()
                for (itemSnapshot in snapshot.children) {
                    val dataClass = itemSnapshot.getValue(DataClass::class.java)
                    dataClass?.key = itemSnapshot.key
                    dataList.add(dataClass!!)
                }
                adapter.notifyDataSetChanged()
                dialog.dismiss()

                // Save data
                saveData()
            }

            override fun onCancelled(error: DatabaseError) {
                dialog.dismiss()
            }
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchList(newText)
                return true
            }
        })

        fabAdd.setOnClickListener {
            startActivity(Intent(requireContext(), UploadActivity::class.java))
        }

        fabUpdate.setOnClickListener {
            dialog.show()
            databaseReference.addListenerForSingleValueEvent(eventListener)
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0 && fabAdd.isShown) {
                    fabAdd.hide()
                    fabUpdate.hide()
                } else if (dy < 0 && !fabAdd.isShown) {
                    fabAdd.show()
                    fabUpdate.show()
                }
            }
        })

        return view
    }

    override fun onResume() {
        super.onResume()
        dialog.show()
        databaseReference.addValueEventListener(eventListener)
    }

    override fun onPause() {
        super.onPause()
        databaseReference.removeEventListener(eventListener)
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

    private fun saveData() {
        val sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(dataList)
        editor.putString(DATA_LIST_KEY, json)
        editor.apply()
    }

    private fun loadSavedData() {
        val sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString(DATA_LIST_KEY, null)
        if (json != null) {
            val type = object : TypeToken<ArrayList<DataClass>>() {}.type
            dataList = gson.fromJson(json, type)
            adapter.updateDataList(dataList)
        }
    }
}
