package com.limyusontudtud.souvseek

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class UploadActivity : AppCompatActivity() {
    private lateinit var uploadImage: ImageView
    private lateinit var saveButton: Button
    private lateinit var uploadTopic: EditText
    private lateinit var databaseHelper: DatabaseHelper
    private var uri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        uploadImage = findViewById(R.id.uploadImage)
        uploadTopic = findViewById(R.id.uploadTopic)
        saveButton = findViewById(R.id.saveButton)

        // Initialize DatabaseHelper
        databaseHelper = DatabaseHelper(this)

        // Request storage permission if needed
        requestStoragePermission()

        val activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data = result.data
                    uri = data?.data
                    uploadImage.setImageURI(uri)
                } else {
                    Toast.makeText(this@UploadActivity, "No Image Selected", Toast.LENGTH_SHORT).show()
                }
            }
        )

        uploadImage.setOnClickListener {
            val photoPicker = Intent(Intent.ACTION_PICK)
            photoPicker.type = "image/*"
            activityResultLauncher.launch(photoPicker)
        }

        saveButton.setOnClickListener {
            saveData()
        }
    }

    private fun saveData() {
        if (uri == null) {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
            return
        }

        val topic = uploadTopic.text.toString().trim()

        if (topic.isEmpty()) {
            Toast.makeText(this, "Please fill in the topic", Toast.LENGTH_SHORT).show()
            return
        }

        // Show a progress dialog
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        builder.setMessage("Uploading...")
        val dialog = builder.create()
        dialog.show()

        // Get a temporary file from the URI
        val tempFile = getTempFile(uri!!)
        if (tempFile == null) {
            dialog.dismiss()
            Toast.makeText(this, "Failed to process the file", Toast.LENGTH_SHORT).show()
            return
        }

        // Convert the file path to a File object
        val requestFile = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("image", tempFile.name, requestFile)

        // Convert topic to RequestBody
        val topicBody = RequestBody.create("text/plain".toMediaTypeOrNull(), topic)

        // Send the request to your PHP backend
        val call = RetrofitInstance.api.uploadImage(topicBody, imagePart)
        call.enqueue(object : Callback<UploadResponse> {
            override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>) {
                dialog.dismiss()
                if (response.isSuccessful) {
                    val uploadResponse = response.body()
                    if (uploadResponse?.status == "success") {
                        Toast.makeText(this@UploadActivity, "Upload successful!", Toast.LENGTH_SHORT).show()

                        // Save data locally to SQLite
                        val imageUrl = uploadResponse.imageURL
                        val isSaved = databaseHelper.insertShopItem(topic, imageUrl)
                        if (isSaved) {
                            Toast.makeText(this@UploadActivity, "Data saved locally in SQLite.", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@UploadActivity, "Failed to save data locally.", Toast.LENGTH_SHORT).show()
                        }
                        finish()
                    } else {
                        Toast.makeText(this@UploadActivity, "Upload failed: ${uploadResponse?.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@UploadActivity, "Server error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                dialog.dismiss()
                Toast.makeText(this@UploadActivity, "Upload failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Helper function to get the real file path from URI
    private fun getRealPathFromURI(uri: Uri): String? {
        var filePath: String? = null

        // Check if the URI scheme is content
        if ("content".equals(uri.scheme, ignoreCase = true)) {
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            val cursor: Cursor? = contentResolver.query(uri, projection, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    filePath = cursor.getString(columnIndex)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                cursor?.close()
            }
        }

        // If scheme is not content or no file path was found
        if (filePath == null) {
            filePath = uri.path
        }
        return filePath
    }

    private fun getTempFile(uri: Uri): File? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            val tempFile = File.createTempFile("upload", ".jpg", cacheDir)
            tempFile.outputStream().use { inputStream?.copyTo(it) }
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun requestStoragePermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            android.Manifest.permission.READ_MEDIA_IMAGES
        } else {
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), 1)
        }
    }
}
