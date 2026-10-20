package com.codepath.campgrounds

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.codepath.campgrounds.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPreferences = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val isDarkModeEnabled = sharedPreferences.getBoolean("DARK_MODE", false)
        val targetMode = if (isDarkModeEnabled) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        if (AppCompatDelegate.getDefaultNightMode() != targetMode) {
            AppCompatDelegate.setDefaultNightMode(targetMode)
        }

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val logFragment = LogFragment()
        val dashboardFragment = DashboardFragment()
        val settingsFragment = SettingsFragment()

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            val fragmentToLoad: Fragment = when (item.itemId) {
                R.id.nav_log -> logFragment
                R.id.nav_dashboard -> dashboardFragment
                R.id.nav_settings -> settingsFragment
                else -> logFragment
            }
            replaceFragment(fragmentToLoad)
            true
        }

        // Set default fragment only on the very first start
        if (savedInstanceState == null) {
            binding.bottomNavigationView.selectedItemId = R.id.nav_log
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}