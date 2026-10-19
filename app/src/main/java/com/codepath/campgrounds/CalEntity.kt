package com.codepath.campgrounds

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calorie_table")
data class CalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "food_name") val foodName: String?,
    @ColumnInfo(name = "calories") val calories: Int?
)