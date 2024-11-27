package com.limyusontudtud.souvseek

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton

class DetailActivity : AppCompatActivity() {
    private lateinit var detailTitle: TextView
    private lateinit var detailImage: ImageView
    private lateinit var deleteButton: FloatingActionButton
    private lateinit var editButton: FloatingActionButton
    private lateinit var databaseHelper: DatabaseHelper
    private var recordId: Int = -1
    private var imageUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // Initialize views
        detailImage = findViewById(R.id.detailImage)
        detailTitle = findViewById(R.id.detailTitle)
        deleteButton = findViewById(R.id.deleteButton)
        editButton = findViewById(R.id.editButton)

        // Initialize database helper
        databaseHelper = DatabaseHelper(this)

        // Retrieve data from Intent
        recordId = intent.getIntExtra("Id", -1)
        val title = intent.getStringExtra("Title") ?: ""
        imageUrl = intent.getStringExtra("Image") ?: ""

        // Log received data
        Log.d("DetailActivity", "Received Data - Id: $recordId, Title: $title, Image: $imageUrl")

        // Populate UI
        detailTitle.text = title
        Glide.with(this).load(imageUrl).into(detailImage)

        // Log all shop items for debugging
        databaseHelper.logAllShopItems()

        // Handle delete button click
        deleteButton.setOnClickListener {
            if (recordId != -1) {
                val isDeleted = databaseHelper.deleteShopItem(recordId)
                if (isDeleted) {
                    Toast.makeText(this, "Deleted successfully.", Toast.LENGTH_SHORT).show()
                    Log.d("DetailActivity", "Item with ID $recordId deleted successfully.")
                    finish()
                } else {
                    Toast.makeText(this, "Failed to delete item. Please try again.", Toast.LENGTH_SHORT).show()
                    Log.e("DetailActivity", "Failed to delete item with ID $recordId.")
                }
            } else {
                Toast.makeText(this, "Invalid record ID. Cannot delete item.", Toast.LENGTH_SHORT).show()
                Log.e("DetailActivity", "Delete attempted with invalid ID: $recordId.")
            }
        }

        // Handle edit button click
        editButton.setOnClickListener {
            if (recordId != -1) {
                val intent = Intent(this, UpdateActivity::class.java).apply {
                    putExtra("Id", recordId)      // Pass the shop's ID
                    putExtra("Title", title)     // Pass the shop's title
                    putExtra("Image", imageUrl)  // Pass the shop's image URL
                }
                Log.d("DetailActivity", "Navigating to UpdateActivity with ID: $recordId")
                startActivity(intent)
            } else {
                Toast.makeText(this, "Invalid record ID. Cannot edit item.", Toast.LENGTH_SHORT).show()
                Log.e("DetailActivity", "Edit attempted with invalid ID: $recordId.")
            }
        }
    }
}
