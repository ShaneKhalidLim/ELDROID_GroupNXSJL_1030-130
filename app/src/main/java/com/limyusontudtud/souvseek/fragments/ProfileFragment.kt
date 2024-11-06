package com.limyusontudtud.souvseek.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.limyusontudtud.souvseek.LoginActivity
import com.limyusontudtud.souvseek.R
import com.limyusontudtud.souvseek.SubcriptionActivity
import com.limyusontudtud.souvseek.utils.PrefsManager

class ProfileFragment : Fragment() {
    private lateinit var subscriptionDuration: TextView
    private lateinit var subBtn: ImageButton
    private lateinit var logoutBtn: ImageButton
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Initialize FirebaseAuth
        firebaseAuth = Firebase.auth

        // Initialize UI elements
        subscriptionDuration = view.findViewById(R.id.subscriptionDuration)
        subBtn = view.findViewById(R.id.subscriptionBtn)
        logoutBtn = view.findViewById(R.id.signoutBtn)

        // Set up button click listeners
        subBtn.setOnClickListener {
            startActivity(Intent(activity, SubcriptionActivity::class.java))
        }

        logoutBtn.setOnClickListener {
            firebaseAuth.signOut()
            // Clear SharedPreferences to reset login status
            PrefsManager.logout(requireContext())
            // Navigate to LoginActivity after sign-out
            val loginIntent = Intent(activity, LoginActivity::class.java)
            loginIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(loginIntent)
        }

        // Set the countdown duration
        val durationMillis = 19 * 24 * 60 * 60 * 1000L +
                23 * 60 * 60 * 1000L +
                59 * 60 * 1000L +
                59 * 1000L

        // Start the countdown timer
        startCountdown(durationMillis)

        return view
    }

    private fun startCountdown(durationMillis: Long) {
        object : CountDownTimer(durationMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val days = millisUntilFinished / (1000 * 60 * 60 * 24)
                val hours = (millisUntilFinished / (1000 * 60 * 60)) % 24
                val minutes = (millisUntilFinished / (1000 * 60)) % 60
                val seconds = (millisUntilFinished / 1000) % 60

                val timeLeftFormatted = String.format(
                    "%d days, %02d hrs, %02d mins, %02d sec",
                    days, hours, minutes, seconds
                )
                subscriptionDuration.text = timeLeftFormatted
            }

            override fun onFinish() {
                subscriptionDuration.text = "Subscription expired"
            }
        }.start()
    }
}
