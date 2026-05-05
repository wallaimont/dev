package com.nutrifitness.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.nutrifitness.data.model.Food

@Dao
interface FoodDao {

    @Query("SELECT * FROM foods ORDER BY name ASC")
    fun getAllFoods(): LiveData<List<Food>>

    @Query("SELECT * FROM foods WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchFoods(query: String): LiveData<List<Food>>

    @Query("SELECT * FROM foods WHERE category = :category ORDER BY name ASC")
    fun getFoodsByCategory(category: String): LiveData<List<Food>>

    @Query("SELECT * FROM foods WHERE id = :id")
    suspend fun getFoodById(id: Long): Food?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(food: Food): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(foods: List<Food>)

    @Update
    suspend fun update(food: Food)

    @Delete
    suspend fun delete(food: Food)

    @Query("SELECT COUNT(*) FROM foods")
    suspend fun count(): Int
}
