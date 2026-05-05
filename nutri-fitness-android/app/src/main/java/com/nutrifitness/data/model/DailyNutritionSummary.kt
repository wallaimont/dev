package com.nutrifitness.data.model

/**
 * Resumo nutricional diário (não é entity, é um DTO de consulta).
 */
data class DailyNutritionSummary(
    val date: String,
    val totalCalories: Double,
    val totalProteinG: Double,
    val totalCarbsG: Double,
    val totalFatG: Double,
    val totalFiberG: Double,
    val mealCount: Int
)
