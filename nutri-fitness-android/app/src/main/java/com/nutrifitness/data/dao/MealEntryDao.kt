package com.nutrifitness.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.nutrifitness.data.model.DailyNutritionSummary
import com.nutrifitness.data.model.MealEntry
import com.nutrifitness.data.model.MealEntryWithFood

@Dao
interface MealEntryDao {

    @Query("""
        SELECT me.id, me.foodId, f.name AS foodName, f.category AS foodCategory,
               me.date, me.mealType, me.quantityGrams,
               me.calories, me.proteinG, me.carbsG, me.fatG, me.notes
        FROM meal_entries me
        INNER JOIN foods f ON me.foodId = f.id
        WHERE me.date = :date
        ORDER BY CASE me.mealType
            WHEN 'BREAKFAST' THEN 1
            WHEN 'LUNCH' THEN 2
            WHEN 'SNACK' THEN 3
            WHEN 'PRE_WORKOUT' THEN 4
            WHEN 'POST_WORKOUT' THEN 5
            WHEN 'DINNER' THEN 6
            ELSE 7 END
    """)
    fun getMealEntriesForDate(date: String): LiveData<List<MealEntryWithFood>>

    @Query("""
        SELECT me.date,
               SUM(me.calories) AS totalCalories,
               SUM(me.proteinG) AS totalProteinG,
               SUM(me.carbsG) AS totalCarbsG,
               SUM(me.fatG) AS totalFatG,
               0.0 AS totalFiberG,
               COUNT(*) AS mealCount
        FROM meal_entries me
        WHERE me.date = :date
    """)
    fun getDailySummary(date: String): LiveData<DailyNutritionSummary?>

    @Query("""
        SELECT me.date,
               SUM(me.calories) AS totalCalories,
               SUM(me.proteinG) AS totalProteinG,
               SUM(me.carbsG) AS totalCarbsG,
               SUM(me.fatG) AS totalFatG,
               0.0 AS totalFiberG,
               COUNT(*) AS mealCount
        FROM meal_entries me
        WHERE me.date BETWEEN :startDate AND :endDate
        GROUP BY me.date
        ORDER BY me.date ASC
    """)
    fun getWeeklySummary(startDate: String, endDate: String): LiveData<List<DailyNutritionSummary>>

    @Insert
    suspend fun insert(entry: MealEntry): Long

    @Update
    suspend fun update(entry: MealEntry)

    @Delete
    suspend fun delete(entry: MealEntry)

    @Query("DELETE FROM meal_entries WHERE id = :id")
    suspend fun deleteById(id: Long)
}
