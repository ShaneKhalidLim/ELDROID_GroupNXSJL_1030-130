package com.limyusontudtud.souvseek

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton

class SubcriptionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subcription)

        val sub = findViewById<Button>(R.id.subscribeButton)
        sub.setOnClickListener {
            val intent = Intent(this, PaymentOptionActivity::class.java)
            intent.putExtra("isSubscription", true)
            intent.putExtra("subscriptionPrice", 9.99) // Fixed subscription price
            startActivity(intent)
        }

        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
        }
    }
}
