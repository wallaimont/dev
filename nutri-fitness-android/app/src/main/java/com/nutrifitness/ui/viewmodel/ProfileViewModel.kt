package com.nutrifitness.ui.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.nutrifitness.NutriFitnessApp
import com.nutrifitness.data.model.UserProfile
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as NutriFitnessApp).repository

    val profile: LiveData<UserProfile?> = repository.getProfile()

    private val _saveResult = MutableLiveData<Boolean>()
    val saveResult: LiveData<Boolean> = _saveResult

    fun saveProfile(
        name: String, age: Int, weightKg: Double, heightCm: Double,
        gender: String, activityLevel: String, goal: String
    ) {
        viewModelScope.launch {
            val bmr = calculateBMR(weightKg, heightCm, age, gender)
            val tdee = calculateTDEE(bmr, activityLevel)
            val dailyCal = adjustForGoal(tdee, goal)
            val macros = calculateMacros(dailyCal, goal)

            val userProfile = UserProfile(
                name = name,
                age = age,
                weightKg = weightKg,
                heightCm = heightCm,
                gender = gender,
                activityLevel = activityLevel,
                goal = goal,
                dailyCalorieTarget = dailyCal,
                dailyProteinTargetG = macros.first,
                dailyCarbTargetG = macros.second,
                dailyFatTargetG = macros.third,
                dailyWaterTargetMl = calculateWater(weightKg)
            )
            repository.saveProfile(userProfile)
            _saveResult.postValue(true)
        }
    }

    // Fórmula de Mifflin-St Jeor
    private fun calculateBMR(weight: Double, height: Double, age: Int, gender: String): Double {
        return if (gender == "M") {
            10 * weight + 6.25 * height - 5 * age + 5
        } else {
            10 * weight + 6.25 * height - 5 * age - 161
        }
    }

    private fun calculateTDEE(bmr: Double, activityLevel: String): Double {
        val factor = when (activityLevel) {
            "SEDENTARY" -> 1.2
            "LIGHT" -> 1.375
            "MODERATE" -> 1.55
            "ACTIVE" -> 1.725
            "VERY_ACTIVE" -> 1.9
            else -> 1.2
        }
        return bmr * factor
    }

    private fun adjustForGoal(tdee: Double, goal: String): Int {
        return when (goal) {
            "LOSE_WEIGHT" -> (tdee - 500).toInt()
            "GAIN_MUSCLE" -> (tdee + 300).toInt()
            else -> tdee.toInt()
        }
    }

    // Retorna Triple(proteina_g, carb_g, gordura_g)
    private fun calculateMacros(calories: Int, goal: String): Triple<Int, Int, Int> {
        return when (goal) {
            "LOSE_WEIGHT" -> {
                val protein = (calories * 0.40 / 4).toInt()
                val fat = (calories * 0.30 / 9).toInt()
                val carbs = (calories * 0.30 / 4).toInt()
                Triple(protein, carbs, fat)
            }
            "GAIN_MUSCLE" -> {
                val protein = (calories * 0.30 / 4).toInt()
                val fat = (calories * 0.25 / 9).toInt()
                val carbs = (calories * 0.45 / 4).toInt()
                Triple(protein, carbs, fat)
            }
            else -> {
                val protein = (calories * 0.30 / 4).toInt()
                val fat = (calories * 0.30 / 9).toInt()
                val carbs = (calories * 0.40 / 4).toInt()
                Triple(protein, carbs, fat)
            }
        }
    }

    private fun calculateWater(weightKg: Double): Int {
        return (weightKg * 35).toInt() // 35ml por kg
    }
}
