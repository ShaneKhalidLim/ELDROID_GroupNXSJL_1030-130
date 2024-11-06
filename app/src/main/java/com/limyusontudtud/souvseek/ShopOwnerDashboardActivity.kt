package com.limyusontudtud.souvseek

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.limyusontudtud.souvseek.fragments.*

class ShopOwnerDashboardActivity : AppCompatActivity() {

    private var currentFragment: Fragment? = null
    private lateinit var ShopOwnerHomeFragment: ShopOwnerHomeFragment
    private lateinit var ShopOwnerItemsFragment: ShopOwnerItemsFragment
    private lateinit var ShopOwnerSalesFragment: ShopOwnerSalesFragment
    private lateinit var ShopOwnerProfileFragment: ShopOwnerProfileFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_owner_dashboard)

        ShopOwnerHomeFragment = ShopOwnerHomeFragment()
        ShopOwnerItemsFragment = ShopOwnerItemsFragment()
        ShopOwnerSalesFragment = ShopOwnerSalesFragment()
        ShopOwnerProfileFragment = ShopOwnerProfileFragment()

        makeCurrentFragment(ShopOwnerHomeFragment)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.ic_home -> makeCurrentFragment(ShopOwnerHomeFragment)
                R.id.ic_addshop -> makeCurrentFragment(ShopOwnerItemsFragment)
                R.id.ic_sales -> makeCurrentFragment(ShopOwnerSalesFragment)
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
                replace(R.id.fl_wrapper, ShopOwnerProfileFragment)
                addToBackStack(null)
                commit()
            }
            currentFragment = ShopOwnerProfileFragment
        }
    }

}