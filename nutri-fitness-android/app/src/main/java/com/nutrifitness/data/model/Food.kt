package com.nutrifitness.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Alimento do banco de dados com informações nutricionais por 100g.
 */
@Entity(tableName = "foods")
data class Food(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String, // PROTEIN, CARB, FAT, VEGETABLE, FRUIT, DAIRY, GRAIN, OTHER
    val caloriesPer100g: Double,
    val proteinPer100g: Double,
    val carbsPer100g: Double,
    val fatPer100g: Double,
    val fiberPer100g: Double = 0.0,
    val sodiumMgPer100g: Double = 0.0,
    val isCustom: Boolean = false
)
