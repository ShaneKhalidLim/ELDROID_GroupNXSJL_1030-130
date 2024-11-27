package com.limyusontudtud.souvseek

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.limyusontudtud.souvseek.fragments.*

class DashboardActivity : AppCompatActivity() {

    private var currentFragment: Fragment? = null
    private val homeFragment = HomeFragment()
    private val wishlistFragment = WishlistFragment()
    private val orderFragment = OrderFragment()
    private val profileFragment = ProfileFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Initialize with HomeFragment
        setFragment(homeFragment)

        setupBottomNavigation()
        setupProfileButton()
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.ic_home -> setFragment(homeFragment)
                R.id.ic_wishlist -> setFragment(wishlistFragment)
                R.id.ic_orderStatus -> setFragment(orderFragment)
                else -> false
            }
            true
        }
    }

    private fun setupProfileButton() {
        val profileButton = findViewById<ImageButton>(R.id.profileBtn)
        profileButton.setOnClickListener {
            toggleProfileFragment()
        }
    }

    private fun setFragment(fragment: Fragment) {
        if (currentFragment != fragment) {
            currentFragment = fragment
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fl_wrapper, fragment)
                commit()
            }
        }
    }

    private fun toggleProfileFragment() {
        if (currentFragment != profileFragment) {
            setFragment(profileFragment)
            supportFragmentManager.beginTransaction()
                .addToBackStack(null)
                .commit()
        } else {
            supportFragmentManager.popBackStack()
            currentFragment = homeFragment
        }
    }
}
