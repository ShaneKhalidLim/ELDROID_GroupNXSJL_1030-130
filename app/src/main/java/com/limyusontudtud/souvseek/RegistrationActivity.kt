package com.limyusontudtud.souvseek

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.limyusontudtud.souvseek.databinding.ActivityRegistrationBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistrationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (isValidEmail(email) && isValidPassword(password)) {
                registerUser(email, password)
            } else {
                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
            }
        }

        binding.textViewLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        return password.isNotEmpty() && password.length >= 6
    }

    private fun registerUser(email: String, password: String) {
        RetrofitInstance.api.registerUser(email, password)
            .enqueue(object : Callback<RegistrationResponse> {
                override fun onResponse(
                    call: Call<RegistrationResponse>,
                    response: Response<RegistrationResponse>
                ) {
                    if (response.isSuccessful && response.body()?.status == "success") {
                        // Registration was successful on the server
                        Toast.makeText(this@RegistrationActivity, "Registration successful", Toast.LENGTH_SHORT).show()

                        // Local database handling (optional if only storing remotely)
                        val dbHelper = DatabaseHelper(this@RegistrationActivity)
                        val isInserted = dbHelper.insertUser(email, password)

                        if (isInserted) {
                            Toast.makeText(this@RegistrationActivity, "User saved locally", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@RegistrationActivity, "Failed to save user locally", Toast.LENGTH_SHORT).show()
                        }

                        // Proceed to LoginActivity after successful registration
                        val intent = Intent(this@RegistrationActivity, LoginActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                        finish()
                    } else {
                        // Handle server registration failure
                        val message = response.body()?.message ?: "Registration failed"
                        Toast.makeText(this@RegistrationActivity, message, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<RegistrationResponse>, t: Throwable) {
                    // Handle network or server errors
                    Toast.makeText(this@RegistrationActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }


}
