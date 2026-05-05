package com.nutrifitness.ui.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.nutrifitness.NutriFitnessApp
import com.nutrifitness.data.model.Food
import kotlinx.coroutines.launch

class FoodViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as NutriFitnessApp).repository

    private val _searchQuery = MutableLiveData("")

    val foods: LiveData<List<Food>> = _searchQuery.switchMap { query ->
        if (query.isNullOrBlank()) {
            repository.getAllFoods()
        } else {
            repository.searchFoods(query)
        }
    }

    private val _saveResult = MutableLiveData<Boolean>()
    val saveResult: LiveData<Boolean> = _saveResult

    fun search(query: String) {
        _searchQuery.value = query
    }

    fun addCustomFood(
        name: String, category: String,
        calories: Double, protein: Double, carbs: Double, fat: Double, fiber: Double
    ) {
        viewModelScope.launch {
            val food = Food(
                name = name,
                category = category,
                caloriesPer100g = calories,
                proteinPer100g = protein,
                carbsPer100g = carbs,
                fatPer100g = fat,
                fiberPer100g = fiber,
                isCustom = true
            )
            repository.insertFood(food)
            _saveResult.postValue(true)
        }
    }

    fun deleteFood(food: Food) {
        viewModelScope.launch {
            repository.deleteFood(food)
        }
    }
}
