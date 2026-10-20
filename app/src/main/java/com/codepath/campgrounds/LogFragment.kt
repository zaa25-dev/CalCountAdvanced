package com.codepath.campgrounds

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LogFragment : Fragment() {

    private val calorieEntries = mutableListOf<CalorieEntry>()
    private lateinit var adapter: CalorieAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_log, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        adapter = CalorieAdapter(calorieEntries)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            (requireActivity().application as CalorieApplication).db.calorieDao().getAll().collect { databaseList ->
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

        view.findViewById<Button>(R.id.btnAddItem).setOnClickListener {
            showAddCalorieDialog()
        }

        return view
    }

    private fun showAddCalorieDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.popup, null)
        val etFoodName = dialogView.findViewById<EditText>(R.id.etFoodName)
        val etCalories = dialogView.findViewById<EditText>(R.id.etCalories)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("Submit", null)
            .setNegativeButton("Cancel") { d, _ -> d.dismiss() }
            .create()

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val foodName = etFoodName.text.toString().trim()
            val caloriesText = etCalories.text.toString().trim()

            if (foodName.isEmpty() || caloriesText.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in both fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val calories = caloriesText.toIntOrNull()
            if (calories == null) {
                Toast.makeText(requireContext(), "Please enter a valid number for calories", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                (requireActivity().application as CalorieApplication).db.calorieDao().insert(
                    CalEntity(foodName = foodName, calories = calories)
                )
            }
            dialog.dismiss()
        }
    }
}