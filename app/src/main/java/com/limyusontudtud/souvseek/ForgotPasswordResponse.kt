package com.limyusontudtud.souvseek

data class ForgotPasswordRequest(val email: String)
data class ForgotPasswordResponse(val status: String, val message: String)

