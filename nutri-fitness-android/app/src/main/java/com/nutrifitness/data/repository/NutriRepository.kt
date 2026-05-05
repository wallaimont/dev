package com.nutrifitness.data.repository

import androidx.lifecycle.LiveData
import com.nutrifitness.data.dao.*
import com.nutrifitness.data.model.*

class NutriRepository(
    private val userProfileDao: UserProfileDao,
    private val foodDao: FoodDao,
    private val mealEntryDao: MealEntryDao,
    private val waterIntakeDao: WaterIntakeDao
) {
    // Profile
    fun getProfile(): LiveData<UserProfile?> = userProfileDao.getProfile()
    suspend fun getProfileSync(): UserProfile? = userProfileDao.getProfileSync()
    suspend fun saveProfile(profile: UserProfile) = userProfileDao.insertOrUpdate(profile)

    // Foods
    fun getAllFoods(): LiveData<List<Food>> = foodDao.getAllFoods()
    fun searchFoods(query: String): LiveData<List<Food>> = foodDao.searchFoods(query)
    fun getFoodsByCategory(category: String): LiveData<List<Food>> = foodDao.getFoodsByCategory(category)
    suspend fun getFoodById(id: Long): Food? = foodDao.getFoodById(id)
    suspend fun insertFood(food: Food): Long = foodDao.insert(food)
    suspend fun updateFood(food: Food) = foodDao.update(food)
    suspend fun deleteFood(food: Food) = foodDao.delete(food)
    suspend fun foodCount(): Int = foodDao.count()

    // Meals
    fun getMealEntriesForDate(date: String): LiveData<List<MealEntryWithFood>> =
        mealEntryDao.getMealEntriesForDate(date)

    fun getDailySummary(date: String): LiveData<DailyNutritionSummary?> =
        mealEntryDao.getDailySummary(date)

    fun getWeeklySummary(startDate: String, endDate: String): LiveData<List<DailyNutritionSummary>> =
        mealEntryDao.getWeeklySummary(startDate, endDate)

    suspend fun addMealEntry(entry: MealEntry): Long = mealEntryDao.insert(entry)
    suspend fun updateMealEntry(entry: MealEntry) = mealEntryDao.update(entry)
    suspend fun deleteMealEntry(id: Long) = mealEntryDao.deleteById(id)

    // Water
    fun getWaterIntakeForDate(date: String): LiveData<List<WaterIntake>> =
        waterIntakeDao.getWaterIntakeForDate(date)

    fun getTotalWaterForDate(date: String): LiveData<Int> =
        waterIntakeDao.getTotalWaterForDate(date)

    suspend fun addWaterIntake(intake: WaterIntake): Long = waterIntakeDao.insert(intake)
    suspend fun deleteWaterIntake(id: Long) = waterIntakeDao.deleteById(id)
}
