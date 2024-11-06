package com.limyusontudtud.souvseek

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.limyusontudtud.souvseek.databinding.ActivityLoginBinding
import com.limyusontudtud.souvseek.utils.PrefsManager

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        // Check if the user is already logged in
        if (PrefsManager.isLoggedIn(this)) {
            navigateToDashboard()
        }

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

    private fun navigateToSignUp() {
        Intent(this, RegistrationActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(this)
        }
    }

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
            }
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
