package com.limyusontudtud.souvseek

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "souvseek.db"
        const val DATABASE_VERSION = 2 // Incremented version to recreate tables
        const val TABLE_USERS = "users"
        const val TABLE_SHOP = "shop"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // Create users table
        val createUsersTable = """
            CREATE TABLE IF NOT EXISTS $TABLE_USERS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                email TEXT UNIQUE,
                password TEXT
            )
        """
        db?.execSQL(createUsersTable)

        // Create shop table
        val createShopTable = """
            CREATE TABLE IF NOT EXISTS $TABLE_SHOP (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT,
                image_url TEXT
            )
        """
        db?.execSQL(createShopTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_SHOP")
        onCreate(db)
    }

    // Insert function for users table
    fun insertUser(email: String, password: String): Boolean {
        val db = writableDatabase
        val contentValues = ContentValues().apply {
            put("email", email)
            put("password", password)
        }
        val result = db.insert(TABLE_USERS, null, contentValues)
        db.close()

        if (result == -1L) {
            Log.e("DatabaseHelper", "Failed to insert user")
        } else {
            Log.d("DatabaseHelper", "User inserted successfully")
        }

        return result != -1L
    }

    // Check user credentials for login
    fun checkUserCredentials(email: String, password: String): Boolean {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_USERS WHERE email = ? AND password = ?"
        val cursor = db.rawQuery(query, arrayOf(email, password))
        val userExists = cursor.count > 0
        cursor.close()
        db.close()
        return userExists
    }

    // Insert function for shop table
    fun insertShopItem(title: String, imageUrl: String?): Boolean {
        val db = writableDatabase
        val contentValues = ContentValues().apply {
            put("title", title)
            put("image_url", imageUrl)
        }
        val result = db.insert(TABLE_SHOP, null, contentValues)
        db.close()

        if (result == -1L) {
            Log.e("DatabaseHelper", "Failed to insert shop item")
            return false
        } else {
            Log.d("DatabaseHelper", "Shop item inserted successfully")
            return true
        }
    }

    fun updatePassword(email: String, newPassword: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("password", newPassword)
        val result = db.update("users", contentValues, "email = ?", arrayOf(email))
        return result > 0
    }

    fun updateShopItem(id: Int, title: String, newImageUrl: String?): Boolean {
        val db = writableDatabase
        val finalImageUrl = newImageUrl ?: getImageUrl(id) ?: ""

        if (finalImageUrl.isEmpty()) {
            Log.e("DatabaseHelper", "Error: No valid image URL for ID $id.")
            return false
        }

        val contentValues = ContentValues().apply {
            put("title", title)
            put("image_url", finalImageUrl)
        }

        val result = db.update(TABLE_SHOP, contentValues, "id=?", arrayOf(id.toString()))
        db.close()

        Log.d("DatabaseHelper", "Shop item updated successfully: ID $id")
        return result > 0
    }


    fun getImageUrl(id: Int): String? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT image_url FROM $TABLE_SHOP WHERE id = ?", arrayOf(id.toString()))
        var imageUrl: String? = null
        if (cursor.moveToFirst()) {
            imageUrl = cursor.getString(cursor.getColumnIndexOrThrow("image_url"))
        }
        cursor.close()
        db.close()

        Log.d("DatabaseHelper", "Fetched image URL for ID $id: $imageUrl")
        return imageUrl
    }


    fun deleteShopItem(id: Int): Boolean {
        val db = writableDatabase
        val result = db.delete("shop", "id=?", arrayOf(id.toString()))
        db.close()

        if (result > 0) {
            Log.d("DatabaseHelper", "Shop item deleted successfully: ID $id")
        } else {
            Log.e("DatabaseHelper", "Failed to delete shop item: ID $id")
        }

        return result > 0
    }

    fun logAllShopItems() {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_SHOP", null)
        if (cursor != null && cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
                val imageUrl = cursor.getString(cursor.getColumnIndexOrThrow("image_url"))
                Log.d("DatabaseHelper", "Shop Item - ID: $id, Title: $title, Image URL: $imageUrl")
            } while (cursor.moveToNext())
        } else {
            Log.e("DatabaseHelper", "No shop items found in the database.")
        }
        cursor?.close()
    }


    fun readAllShops(): Cursor? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_SHOP", null)
        Log.d("DatabaseHelper", "Fetched ${cursor.count} items from database.")
        return cursor
    }




}
