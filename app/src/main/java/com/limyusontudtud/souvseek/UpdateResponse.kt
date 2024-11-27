// UpdateResponse.kt
package com.limyusontudtud.souvseek

data class UpdateResponse(
    val status: String, // "success" or "error"
    val message: String, // API response message
    val data: UpdateData? // Contains updated image URL
)

data class UpdateData(
    val imageURL: String // Updated image URL
)
