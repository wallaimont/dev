package com.nutrifitness

import android.app.Application
import com.nutrifitness.data.NutriFitnessDatabase
import com.nutrifitness.data.repository.NutriRepository

class NutriFitnessApp : Application() {

    val database by lazy { NutriFitnessDatabase.getDatabase(this) }

    val repository by lazy {
        NutriRepository(
            database.userProfileDao(),
            database.foodDao(),
            database.mealEntryDao(),
            database.waterIntakeDao()
        )
    }
}
