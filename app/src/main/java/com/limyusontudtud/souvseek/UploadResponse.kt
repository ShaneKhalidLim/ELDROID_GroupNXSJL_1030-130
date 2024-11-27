package com.limyusontudtud.souvseek

data class UploadResponse(
    val status: String,
    val message: String,
    val imageURL: String? = null // Optional in case of failure
)
