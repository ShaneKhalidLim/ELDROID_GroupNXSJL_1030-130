package com.limyusontudtud.souvseek

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.limyusontudtud.souvseek.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var databaseHelper: DatabaseHelper // Local database helper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize DatabaseHelper
        databaseHelper = DatabaseHelper(this)

        // Handle login button click
        binding.login.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Email and password cannot be empty", Toast.LENGTH_SHORT).show()
        // Set up listeners for the login, signup, and forgot password actions
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.login.setOnClickListener { performLogin() }
        binding.textViewSignUp.setOnClickListener { navigateToSignUp() }
        binding.forgotPassword.setOnClickListener { showForgotPasswordDialog() }
    }

    private fun performLogin() {
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    PrefsManager.setLoggedIn(this, email, true)
                    navigateToDashboard()
                } else {
                    showToast(task.exception.toString())
                }
            }
        } else {
            showToast("Fields cannot be empty")
        }
    }

        // Handle "Sign Up" text click
        binding.textViewSignUp.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
    private fun navigateToSignUp() {
        Intent(this, RegistrationActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(this)
        }
    }

        // Handle "Forgot Password" text click
        binding.forgotPassword.setOnClickListener {
            showForgotPasswordDialog()
    private fun showForgotPasswordDialog() {
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog_forgot, null)
        val userEmail = view.findViewById<EditText>(R.id.emailBox)

        builder.setView(view)
        val dialog = builder.create()

        view.findViewById<Button>(R.id.btnReset).setOnClickListener {
            handlePasswordReset(userEmail)
            dialog.dismiss()
        }

    private fun loginUser(email: String, password: String) {
        // Step 1: Validate credentials with the PHP backend
        RetrofitInstance.api.loginUser(email, password)
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    if (response.isSuccessful && response.body()?.status == "success") {
                        // Step 2: Check if the user exists in SQLite
                        val userExists = databaseHelper.checkUserCredentials(email, password)
                        if (userExists) {
                            // Proceed if the user exists locally
                            Toast.makeText(
                                this@LoginActivity,
                                "Welcome back! ${response.body()?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                            navigateToDashboard()
                        } else {
                            // Reject login if the user is not found locally
                            Toast.makeText(
                                this@LoginActivity,
                                "User not found in local records. Please register.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            response.body()?.message ?: "Invalid email or password",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun showForgotPasswordDialog() {
        // Inflate the custom dialog layout
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_forgot, null)
        val emailEditText = dialogView.findViewById<EditText>(R.id.emailBox)

        // Create and configure the dialog
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        // Set button actions
        dialogView.findViewById<Button>(R.id.btnCancel).setOnClickListener {
            dialog.dismiss() // Close the dialog on Cancel
        }

        dialogView.findViewById<Button>(R.id.btnReset).setOnClickListener {
            val email = emailEditText.text.toString().trim()
            if (email.isNotEmpty()) {
                // Send password reset request
                sendPasswordResetRequest(email)
                dialog.dismiss() // Close the dialog after sending the request
            } else {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show() // Display the dialog
    }

    private fun sendPasswordResetRequest(email: String) {
        // Call the PHP backend for password reset
        RetrofitInstance.api.forgotPassword(email)
            .enqueue(object : Callback<ForgotPasswordResponse> {
                override fun onResponse(
                    call: Call<ForgotPasswordResponse>,
                    response: Response<ForgotPasswordResponse>
                ) {
                    if (response.isSuccessful && response.body()?.status == "success") {
                        Toast.makeText(
                            this@LoginActivity,
                            response.body()?.message ?: "Reset email sent successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            response.body()?.message ?: "Failed to send reset email",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
        view.findViewById<Button>(R.id.btnCancel).setOnClickListener {
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(0))
        dialog.show()
    }

    private fun handlePasswordReset(emailField: EditText) {
        val email = emailField.text.toString()

        if (email.isEmpty()) {
            showToast("Email cannot be empty")
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Invalid email address")
            return
        }

        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showToast("Check your email")
                } else {
                    showToast("Error: ${task.exception?.message}")
                }

                override fun onFailure(call: Call<ForgotPasswordResponse>, t: Throwable) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun navigateToDashboard() {
        val intent = Intent(this, ShopOwnerDashboardActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToDashboard() {
        Intent(this, ShopOwnerDashboardActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(this)
        }
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
