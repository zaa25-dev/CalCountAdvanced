package com.codepath.campgrounds

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val calorieEntries = mutableListOf<CalorieEntry>()
    private lateinit var adapter: CalorieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Setup RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        adapter = CalorieAdapter(calorieEntries)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        lifecycleScope.launch {
            (application as CalorieApplication).db.calorieDao().getAll().collect { databaseList ->
                databaseList.map { entity ->
                    CalorieEntry(
                        entity.foodName ?: "",
                        entity.calories ?: 0
                    )
                }.also { mappedList ->
                    calorieEntries.clear()
                    calorieEntries.addAll(mappedList)
                    adapter.notifyDataSetChanged()
                }
            }
        }

        // "Log Food" Button
        val btnAddItem: Button = findViewById(R.id.btnAddItem)
        btnAddItem.setOnClickListener {
            showAddCalorieDialog()
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