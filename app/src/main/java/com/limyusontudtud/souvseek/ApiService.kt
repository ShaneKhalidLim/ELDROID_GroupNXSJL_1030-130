package com.limyusontudtud.souvseek

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @FormUrlEncoded
    @POST("signup.php")
    fun registerUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegistrationResponse>

    @FormUrlEncoded
    @POST("login.php") // PHP script for user login
    fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("forgetpassword.php")
    fun forgotPassword(
        @Field("email") email: String
    ): Call<ForgotPasswordResponse>

    @Multipart
    @POST("upload.php") // Ensure this points to your PHP upload script
    fun uploadImage(
        @Part("uploadTopic") uploadTopic: RequestBody, // Use 'uploadTopic' to match the PHP field name
        @Part image: MultipartBody.Part
    ): Call<UploadResponse>

    @Multipart
    @POST("update.php") // PHP script for updating
    fun updateData(
        @Part("id") id: RequestBody, // Record ID
        @Part("uploadTopic") topic: RequestBody, // Title or topic
        @Part image: MultipartBody.Part? = null // Optional image
    ): Call<UpdateResponse>

    @GET("shopownerdashboard.php") // Assuming this is the PHP endpoint URL
    fun getShopItems(): Call<List<DataClass>>

}
