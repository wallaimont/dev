package com.nutrifitness.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.nutrifitness.data.model.WaterIntake

@Dao
interface WaterIntakeDao {

    @Query("SELECT * FROM water_intake WHERE date = :date ORDER BY time ASC")
    fun getWaterIntakeForDate(date: String): LiveData<List<WaterIntake>>

    @Query("SELECT COALESCE(SUM(amountMl), 0) FROM water_intake WHERE date = :date")
    fun getTotalWaterForDate(date: String): LiveData<Int>

    @Insert
    suspend fun insert(intake: WaterIntake): Long

    @Delete
    suspend fun delete(intake: WaterIntake)

    @Query("DELETE FROM water_intake WHERE id = :id")
    suspend fun deleteById(id: Long)
}
