package com.codepath.campgrounds

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CalorieAdapter(
    private val entries: MutableList<CalorieEntry>
) : RecyclerView.Adapter<CalorieAdapter.CalorieViewHolder>() {

    class CalorieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvFoodName: TextView = itemView.findViewById(R.id.tvFoodName)
        val tvCalories: TextView = itemView.findViewById(R.id.tvCalories)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalorieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_wishlist, parent, false)
        return CalorieViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalorieViewHolder, position: Int) {
        val entry = entries[position]
        val context = holder.itemView.context
        holder.tvFoodName.text = entry.foodName
        holder.tvCalories.text = context.getString(R.string.calories_format, entry.calories)
    }

    override fun getItemCount(): Int = entries.size

    fun addItem(entry: CalorieEntry) {
        entries.add(entry)
        notifyItemInserted(entries.size - 1)
    }
}