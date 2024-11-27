package com.limyusontudtud.souvseek

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class UpdateActivity : AppCompatActivity() {

    private lateinit var updateImage: ImageView
    private lateinit var updateButton: Button
    private lateinit var updateTitle: EditText
    private lateinit var databaseHelper: DatabaseHelper
    private var uri: Uri? = null
    private var recordId: Int = -1
    private var currentImageUrl: String = "" // Holds the current image URL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)

        // Initialize views
        updateImage = findViewById(R.id.updateImage)
        updateTitle = findViewById(R.id.updateTitle)
        updateButton = findViewById(R.id.updateButton)

        // Initialize database helper
        databaseHelper = DatabaseHelper(this)

        // Retrieve record ID and other details from the Intent
        recordId = intent.getIntExtra("Id", -1)
        val title = intent.getStringExtra("Title") ?: ""

        // Fetch `image_url` from the database
        currentImageUrl = fetchImageUrlFromDatabase(recordId)

        // Populate UI with existing data
        updateTitle.setText(title)

        if (currentImageUrl.isNotEmpty()) {
            Glide.with(this).load(currentImageUrl).into(updateImage)
        }

        // Set image picker on click
        updateImage.setOnClickListener {
            openImagePicker()
        }

        // Set update button on click
        updateButton.setOnClickListener {
            validateAndSendUpdate()
        }
    }

    private fun fetchImageUrlFromDatabase(recordId: Int): String {
        val imageUrl = databaseHelper.getImageUrl(recordId)
        Log.d("UpdateActivity", "Fetched image_url: $imageUrl for recordId: $recordId")
        return imageUrl ?: ""
    }

    private fun openImagePicker() {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, 100)
    }

    private fun validateAndSendUpdate() {
        val title = updateTitle.text.toString().trim()
        if (title.isEmpty()) {
            Toast.makeText(this, "Title is required.", Toast.LENGTH_SHORT).show()
            return
        }

        val imagePath = if (uri != null) getRealPathFromURI(uri!!) else null

        sendUpdateRequest(recordId, title, imagePath)
    }

    private fun sendUpdateRequest(id: Int, title: String, imagePath: String?) {
        // Log the data being sent
        Log.d("UpdateActivity", "Sending ID: $id, Title: $title, ImagePath: $imagePath")

        // Prepare the request body parts
        val idPart = RequestBody.create("text/plain".toMediaTypeOrNull(), id.toString())
        val titlePart = RequestBody.create("text/plain".toMediaTypeOrNull(), title)

        val imagePart: MultipartBody.Part? = if (!imagePath.isNullOrEmpty()) {
            val file = File(imagePath)
            val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
            MultipartBody.Part.createFormData("image", file.name, requestFile)
        } else {
            null
        }

        // Make the API call
        RetrofitInstance.api.updateData(idPart, titlePart, imagePart).enqueue(object : Callback<UpdateResponse> {
            override fun onResponse(call: Call<UpdateResponse>, response: Response<UpdateResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    val updatedImageUrl = response.body()?.data?.imageURL ?: currentImageUrl

                    val isLocalUpdateSuccessful = databaseHelper.updateShopItem(id, title, updatedImageUrl)

                    runOnUiThread {
                        if (isLocalUpdateSuccessful) {
                            Toast.makeText(this@UpdateActivity, "Update successful.", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this@UpdateActivity, "Failed to update local database.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Log.e("UpdateActivity", "Error: ${response.errorBody()?.string()}")
                    runOnUiThread {
                        Toast.makeText(this@UpdateActivity, "Failed to update data.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<UpdateResponse>, t: Throwable) {
                Log.e("UpdateActivity", "API call failed: ${t.message}", t)
                runOnUiThread {
                    Toast.makeText(this@UpdateActivity, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun getRealPathFromURI(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                return cursor.getString(columnIndex)
            }
        }
        return null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null) {
            uri = data.data
            updateImage.setImageURI(uri)
        }
    }
}
