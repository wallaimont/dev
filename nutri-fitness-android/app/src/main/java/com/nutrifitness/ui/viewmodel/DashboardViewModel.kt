package com.nutrifitness.ui.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.nutrifitness.NutriFitnessApp
import com.nutrifitness.data.model.DailyNutritionSummary
import com.nutrifitness.data.model.MealEntry
import com.nutrifitness.data.model.MealEntryWithFood
import com.nutrifitness.data.model.UserProfile
import com.nutrifitness.data.model.WaterIntake
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as NutriFitnessApp).repository

    private val _selectedDate = MutableLiveData(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
    val selectedDate: LiveData<String> = _selectedDate

    val userProfile: LiveData<UserProfile?> = repository.getProfile()

    val dailySummary: LiveData<DailyNutritionSummary?> = _selectedDate.switchMap { date ->
        repository.getDailySummary(date)
    }

    val mealEntries: LiveData<List<MealEntryWithFood>> = _selectedDate.switchMap { date ->
        repository.getMealEntriesForDate(date)
    }

    val totalWater: LiveData<Int> = _selectedDate.switchMap { date ->
        repository.getTotalWaterForDate(date)
    }

    fun setDate(date: String) {
        _selectedDate.value = date
    }

    fun previousDay() {
        val current = LocalDate.parse(_selectedDate.value)
        _selectedDate.value = current.minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE)
    }

    fun nextDay() {
        val current = LocalDate.parse(_selectedDate.value)
        val next = current.plusDays(1)
        if (!next.isAfter(LocalDate.now())) {
            _selectedDate.value = next.format(DateTimeFormatter.ISO_LOCAL_DATE)
        }
    }

    fun addWater(amountMl: Int) {
        viewModelScope.launch {
            val intake = WaterIntake(
                date = _selectedDate.value ?: LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE),
                amountMl = amountMl,
                time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
            )
            repository.addWaterIntake(intake)
        }
    }

    fun deleteMealEntry(id: Long) {
        viewModelScope.launch {
            repository.deleteMealEntry(id)
        }
    }
}
