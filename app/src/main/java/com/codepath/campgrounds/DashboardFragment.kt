package com.codepath.campgrounds

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.color.MaterialColors
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        val lineChart: LineChart = view.findViewById(R.id.lineChart)
        val tvAverageCalories: TextView = view.findViewById(R.id.tvAverageCalories)
        val tvMinCalories: TextView = view.findViewById(R.id.tvMinCalories)
        val tvMaxCalories: TextView = view.findViewById(R.id.tvMaxCalories)
        val tvTotalCalories: TextView = view.findViewById(R.id.tvTotalCalories)

        val textColor = MaterialColors.getColor(requireContext(), android.R.attr.textColorPrimary, Color.WHITE)

        lineChart.description.isEnabled = false
        lineChart.setExtraOffsets(16f, 16f, 16f, 16f)
        lineChart.legend.textColor = textColor
        lineChart.setTouchEnabled(true)
        lineChart.isDragEnabled = true
        lineChart.setScaleEnabled(false)

        lineChart.xAxis.apply {
            this.textColor = textColor
            position = XAxis.XAxisPosition.BOTTOM
            setDrawLabels(true)
            setDrawGridLines(true)
            gridColor = Color.parseColor("#33888888")
            granularity = 1f
        }

        lineChart.axisLeft.apply {
            this.textColor = textColor
            setDrawLabels(true)
            setDrawGridLines(true)
            gridColor = Color.parseColor("#33888888")
        }

        lineChart.axisRight.isEnabled = false

        viewLifecycleOwner.lifecycleScope.launch {
            (activity?.application as? CalorieApplication)?.db?.calorieDao()?.getAll()?.collect { entries ->
                val calorieValues = entries.mapNotNull { it.calories }

                if (calorieValues.isNotEmpty()) {
                    val average = calorieValues.average().toInt()
                    val min = calorieValues.minOrNull() ?: 0
                    val max = calorieValues.maxOrNull() ?: 0
                    val total = calorieValues.sum()

                    tvAverageCalories.text = "$average cal"
                    tvMinCalories.text = "$min cal"
                    tvMaxCalories.text = "$max cal"
                    tvTotalCalories.text = "$total cal"

                    val chartEntries = calorieValues.mapIndexed { index, cal ->
                        Entry(index.toFloat() + 1, cal.toFloat())
                    }

                    val dataSet = LineDataSet(chartEntries, "Calories per Entry").apply {
                        color = Color.parseColor("#0F9D58")
                        setCircleColor(Color.parseColor("#0F9D58"))
                        lineWidth = 2.5f
                        circleRadius = 4f
                        valueTextColor = textColor
                        valueTextSize = 10f
                        setDrawValues(true)
                        mode = LineDataSet.Mode.CUBIC_BEZIER
                    }

                    lineChart.data = LineData(dataSet)
                    lineChart.invalidate()
                } else {
                    tvAverageCalories.text = "0 cal"
                    tvMinCalories.text = "0 cal"
                    tvMaxCalories.text = "0 cal"
                    tvTotalCalories.text = "0 cal"

                    lineChart.clear()
                    lineChart.invalidate()
                }
            }
        }

        return view
    }
}