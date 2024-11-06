// DetailActivity.kt
package com.limyusontudtud.souvseek

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class DetailActivity : AppCompatActivity() {
    private lateinit var detailTitle: TextView
    private lateinit var detailImage: ImageView
    private lateinit var deleteButton: FloatingActionButton
    private lateinit var editButton: FloatingActionButton
    private var key: String = ""
    private var imageUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        detailImage = findViewById(R.id.detailImage)
        detailTitle = findViewById(R.id.detailTitle)
        deleteButton = findViewById(R.id.deleteButton)
        editButton = findViewById(R.id.editButton)

        val bundle = intent.extras
        bundle?.let {
            detailTitle.text = it.getString("Title")
            key = it.getString("Key", "")
            imageUrl = it.getString("Image", "")
            Glide.with(this).load(imageUrl).into(detailImage)
        }

        deleteButton.setOnClickListener {
            // Reference to the specific item in the database
            val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Android Tutorials").child(key)
            // Reference to the specific image in Firebase Storage
            val storageReference: StorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)

            // Delete the image from Firebase Storage
            storageReference.delete().addOnSuccessListener {
                // Image deleted successfully, now delete the reference from the database
                databaseReference.removeValue().addOnSuccessListener {
                    Toast.makeText(this@DetailActivity, "Deleted successfully", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(applicationContext, ShopOwnerDashboardActivity::class.java))
                    finish()
                }.addOnFailureListener {
                    Toast.makeText(this@DetailActivity, "Failed to delete from database", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this@DetailActivity, "Failed to delete image", Toast.LENGTH_SHORT).show()
            }
        }

        editButton.setOnClickListener {
            val intent = Intent(this@DetailActivity, UpdateActivity::class.java).apply {
                putExtra("Title", detailTitle.text.toString())
                putExtra("Image", imageUrl)
                putExtra("Key", key)
            }
            startActivityForResult(intent, UPDATE_REQUEST_CODE)
        }
    }

    private val UPDATE_REQUEST_CODE = 1

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UPDATE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Handle the updated data received from UpdateActivity
            val updatedTitle = data?.getStringExtra("UpdatedTitle")
            val updatedImage = data?.getStringExtra("UpdatedImage")

            // Update the UI with the new data
            updatedTitle?.let { detailTitle.text = it }
            updatedImage?.let { imageUrl = it }
            updatedImage?.let { Glide.with(this).load(it).into(detailImage)
            }
        }
    }
}
