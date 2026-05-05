package com.nutrifitness.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Perfil do usuário com metas nutricionais diárias.
 */
@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val name: String,
    val age: Int,
    val weightKg: Double,
    val heightCm: Double,
    val gender: String, // "M" ou "F"
    val activityLevel: String, // SEDENTARY, LIGHT, MODERATE, ACTIVE, VERY_ACTIVE
    val goal: String, // LOSE_WEIGHT, MAINTAIN, GAIN_MUSCLE
    val dailyCalorieTarget: Int,
    val dailyProteinTargetG: Int,
    val dailyCarbTargetG: Int,
    val dailyFatTargetG: Int,
    val dailyWaterTargetMl: Int = 2000
)
