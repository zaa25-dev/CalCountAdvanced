package com.codepath.campgrounds

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.codepath.campgrounds.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val calorieEntries = mutableListOf<CalorieEntry>()
    private lateinit var adapter: CalorieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPreferences = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val isDarkModeEnabled = sharedPreferences.getBoolean("DARK_MODE", false)
        if (isDarkModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup RecyclerView
        adapter = CalorieAdapter(calorieEntries)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        lifecycleScope.launch {
            (application as CalorieApplication).db.calorieDao().getAll().collect { databaseList ->
                val mappedList = databaseList.map { entity ->
                    CalorieEntry(
                        entity.foodName ?: "",
                        entity.calories ?: 0
                    )
                }
                calorieEntries.clear()
                calorieEntries.addAll(mappedList)
                adapter.notifyDataSetChanged()
            }
        }

        val settingsFragment = SettingsFragment()

        // Handle navigation item clicks
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_settings -> {
                    binding.mainContentLayout.visibility = View.GONE
                    binding.fragmentContainer.visibility = View.VISIBLE
                    replaceFragment(settingsFragment)
                    true
                }
                else -> false
            }
        }

        // Add Item Dialog
        binding.btnAddItem.setOnClickListener {
            showAddCalorieDialog()
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun removeFragment(fragment: Fragment) {
        if (fragment.isAdded) {
            supportFragmentManager.beginTransaction()
                .remove(fragment)
                .commit()
        }
    }

    private fun showAddCalorieDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.popup, null)
        val etFoodName = dialogView.findViewById<EditText>(R.id.etFoodName)
        val etCalories = dialogView.findViewById<EditText>(R.id.etCalories)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Submit", null)
            .setNegativeButton("Cancel") { d, _ -> d.dismiss() }
            .create()

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val foodName = etFoodName.text.toString().trim()
            val caloriesText = etCalories.text.toString().trim()

            if (foodName.isEmpty() || caloriesText.isEmpty()) {
                Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val calories = caloriesText.toIntOrNull()
            if (calories == null) {
                Toast.makeText(this, "Please enter a valid number for calories", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch(Dispatchers.IO) {
                (application as CalorieApplication).db.calorieDao().insert(
                    CalEntity(
                        foodName = foodName,
                        calories = calories
                    )
                )
            }

            dialog.dismiss()
        }
    }
}