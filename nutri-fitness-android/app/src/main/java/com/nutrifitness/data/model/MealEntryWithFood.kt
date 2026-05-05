package com.nutrifitness.data.model

/**
 * Entrada de refeição com nome do alimento (JOIN query result).
 */
data class MealEntryWithFood(
    val id: Long,
    val foodId: Long,
    val foodName: String,
    val foodCategory: String,
    val date: String,
    val mealType: String,
    val quantityGrams: Double,
    val calories: Double,
    val proteinG: Double,
    val carbsG: Double,
    val fatG: Double,
    val notes: String
)
