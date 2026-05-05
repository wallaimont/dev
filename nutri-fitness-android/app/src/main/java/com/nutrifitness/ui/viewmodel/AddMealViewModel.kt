package com.nutrifitness.ui.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.nutrifitness.NutriFitnessApp
import com.nutrifitness.data.model.Food
import com.nutrifitness.data.model.MealEntry
import kotlinx.coroutines.launch

class AddMealViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as NutriFitnessApp).repository

    private val _searchQuery = MutableLiveData("")
    val searchQuery: LiveData<String> = _searchQuery

    val foods: LiveData<List<Food>> = _searchQuery.switchMap { query ->
        if (query.isNullOrBlank()) {
            repository.getAllFoods()
        } else {
            repository.searchFoods(query)
        }
    }

    private val _selectedFood = MutableLiveData<Food?>()
    val selectedFood: LiveData<Food?> = _selectedFood

    private val _saveResult = MutableLiveData<Boolean>()
    val saveResult: LiveData<Boolean> = _saveResult

    fun search(query: String) {
        _searchQuery.value = query
    }

    fun selectFood(food: Food) {
        _selectedFood.value = food
    }

    fun saveMealEntry(date: String, mealType: String, quantityGrams: Double, notes: String) {
        val food = _selectedFood.value ?: return
        viewModelScope.launch {
            val factor = quantityGrams / 100.0
            val entry = MealEntry(
                foodId = food.id,
                date = date,
                mealType = mealType,
                quantityGrams = quantityGrams,
                calories = food.caloriesPer100g * factor,
                proteinG = food.proteinPer100g * factor,
                carbsG = food.carbsPer100g * factor,
                fatG = food.fatPer100g * factor,
                notes = notes
            )
            repository.addMealEntry(entry)
            _saveResult.postValue(true)
        }
    }
}
