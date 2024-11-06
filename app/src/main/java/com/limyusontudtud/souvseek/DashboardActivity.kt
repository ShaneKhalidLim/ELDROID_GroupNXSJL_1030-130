package com.limyusontudtud.souvseek

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.limyusontudtud.souvseek.fragments.*
import com.limyusontudtud.souvseek.utils.PrefsManager

class DashboardActivity : AppCompatActivity() {

    private var currentFragment: Fragment? = null
    private lateinit var homeFragment: HomeFragment
    private lateinit var wishlistFragment: WishlistFragment
    private lateinit var orderFragment: OrderFragment
    private lateinit var profileFragment: ProfileFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        homeFragment = HomeFragment()
        wishlistFragment = WishlistFragment()
        orderFragment = OrderFragment()
        profileFragment = ProfileFragment()

        makeCurrentFragment(homeFragment)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.ic_home -> makeCurrentFragment(homeFragment)
                R.id.ic_wishlist -> makeCurrentFragment(wishlistFragment)
                R.id.ic_orderStatus -> makeCurrentFragment(orderFragment)
            }
            true
        }

        val profileButton = findViewById<ImageButton>(R.id.profileBtn)
        profileButton.setOnClickListener {
            toggleProfileFragment()
        }
    }

    private fun makeCurrentFragment(fragment: Fragment) {
        currentFragment = fragment
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_wrapper, fragment)
            commit()
        }
    }

    private fun toggleProfileFragment() {
        if (currentFragment is ProfileFragment) {
            supportFragmentManager.popBackStack()
            currentFragment = null
        } else {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fl_wrapper, profileFragment)
                addToBackStack(null)
                commit()
            }
            currentFragment = profileFragment
        }
    }
}
