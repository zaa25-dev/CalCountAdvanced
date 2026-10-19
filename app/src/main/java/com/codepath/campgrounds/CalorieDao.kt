package com.codepath.campgrounds

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CalorieDao {
    @Query("SELECT * FROM calorie_table")
    fun getAll(): Flow<List<CalEntity>>

    @Insert
    fun insert(entry: CalEntity)

    @Insert
    fun insertAll(entries: List<CalEntity>)

    @Query("DELETE FROM calorie_table")
    fun deleteAll()
}