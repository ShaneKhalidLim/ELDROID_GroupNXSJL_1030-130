// UpdateActivity.kt
package com.limyusontudtud.souvseek

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class UpdateActivity : AppCompatActivity() {
    private lateinit var updateImage: ImageView
    private lateinit var updateButton: Button
    private lateinit var updateTitle: EditText
    private var uri: Uri? = null
    private lateinit var key: String
    private lateinit var oldImageURL: String
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)

        updateButton = findViewById(R.id.updateButton)
        updateImage = findViewById(R.id.updateImage)
        updateTitle = findViewById(R.id.updateTitle)

        val activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                uri = data?.data
                updateImage.setImageURI(uri)
            } else {
                Toast.makeText(this@UpdateActivity, "No Image Selected", Toast.LENGTH_SHORT).show()
            }
        }

        val bundle = intent.extras
        bundle?.let {
            Glide.with(this@UpdateActivity).load(it.getString("Image")).into(updateImage)
            updateTitle.setText(it.getString("Title"))
            key = it.getString("Key", "")
            oldImageURL = it.getString("Image", "")
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("Android Tutorials").child(key)

        updateImage.setOnClickListener {
            val photoPicker = Intent(Intent.ACTION_PICK)
            photoPicker.type = "image/*"
            activityResultLauncher.launch(photoPicker)
        }

        updateButton.setOnClickListener {
            if (uri != null) {
                saveImageOnly()
            } else {
                saveTitleOnly()
            }
        }
    }

    private fun saveImageOnly() {
        storageReference = FirebaseStorage.getInstance().getReference().child("Android Images").child(uri!!.lastPathSegment!!)
        val builder = AlertDialog.Builder(this@UpdateActivity)
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.show()
        storageReference.putFile(uri!!).addOnSuccessListener { taskSnapshot ->
            taskSnapshot.storage.downloadUrl.addOnCompleteListener { uriTask ->
                val imageUrl = uriTask.result.toString()
                updateData(imageUrl)
                dialog.dismiss()
            }
        }.addOnFailureListener {
            dialog.dismiss()
        }
    }

    private fun saveTitleOnly() {
        updateData(oldImageURL)
    }

    private fun updateData(imageUrl: String) {
        val title = updateTitle.text.toString().trim()
        if (title.isEmpty()) {
            Toast.makeText(this@UpdateActivity, "Title cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }
        val dataClass = DataClass(title, imageUrl)
        databaseReference.setValue(dataClass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (uri != null && oldImageURL.isNotEmpty()) {
                    val reference = FirebaseStorage.getInstance().getReferenceFromUrl(oldImageURL)
                    reference.delete()
                }
                Toast.makeText(this@UpdateActivity, "Updated", Toast.LENGTH_SHORT).show()
                // Pass back the updated data to DetailActivity
                val resultIntent = Intent().apply {
                    putExtra("UpdatedTitle", title)
                    putExtra("UpdatedImage", imageUrl)
                }
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this@UpdateActivity, e.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }
}
