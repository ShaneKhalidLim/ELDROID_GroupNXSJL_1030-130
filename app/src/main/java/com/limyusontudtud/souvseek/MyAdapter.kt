package com.limyusontudtud.souvseek

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MyAdapter(private val context: Context, private var dataList: List<DataClass>) :
    RecyclerView.Adapter<MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = dataList[position]
        Log.d("MyAdapter", "Binding image URL: ${data.dataImage}")

        // Load the image and title into the ViewHolder
        Glide.with(context)
            .load(data.dataImage)
            .into(holder.recImage)

        holder.recTitle.text = data.dataTitle

        // Set click listener to pass data to DetailActivity
        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java).apply {
                putExtra("Image", data.dataImage)  // Pass image URL
                putExtra("Title", data.dataTitle)  // Pass title
                putExtra("Id", data.id)            // Pass ID
            }
            context.startActivity(intent)
        }
    }


    override fun getItemCount(): Int {
        return dataList.size
    }

    // Validate if the URL is valid
    private fun isValidUrl(url: String?): Boolean {
        return !url.isNullOrEmpty() && (url.startsWith("http://") || url.startsWith("https://") || url.startsWith("file://") || url.startsWith("content://"))
    }

    // Method to update data when searching
    fun searchDataList(searchList: ArrayList<DataClass>) {
        dataList = searchList
        notifyDataSetChanged()
    }

    // Method to update the complete list
    fun updateDataList(newDataList: ArrayList<DataClass>) {
        dataList = newDataList
        notifyDataSetChanged()
    }
}

class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val recImage: ImageView = itemView.findViewById(R.id.recImage)
    val recTitle: TextView = itemView.findViewById(R.id.recTitle)
}

