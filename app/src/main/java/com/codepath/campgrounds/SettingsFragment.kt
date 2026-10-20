package com.codepath.campgrounds

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.materialswitch.MaterialSwitch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        val switchDarkMode = view.findViewById<MaterialSwitch>(R.id.switchDarkMode)
        val btnResetData = view.findViewById<Button>(R.id.btnResetData)

        val sharedPreferences = requireContext().getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val isDarkModeEnabled = sharedPreferences.getBoolean("DARK_MODE", false)

        switchDarkMode.isChecked = isDarkModeEnabled

        switchDarkMode.setOnCheckedChangeListener { buttonView, isChecked ->
            if (!buttonView.isPressed) return@setOnCheckedChangeListener

            val currentSetting = sharedPreferences.getBoolean("DARK_MODE", false)
            if (currentSetting != isChecked) {
                sharedPreferences.edit().putBoolean("DARK_MODE", isChecked).apply()

                val newMode = if (isChecked) {
                    AppCompatDelegate.MODE_NIGHT_YES
                } else {
                    AppCompatDelegate.MODE_NIGHT_NO
                }
                AppCompatDelegate.setDefaultNightMode(newMode)
            }
        }

        btnResetData.setOnClickListener {
            showResetConfirmationDialog()
        }

        return view
    }

    private fun showResetConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Reset All Entries")
            .setMessage("Are you sure you want to delete all logged calorie entries? This cannot be undone.")
            .setPositiveButton("Delete All") { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                    (requireActivity().application as CalorieApplication).db.calorieDao().deleteAll()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "All entries deleted", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}