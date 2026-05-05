package com.nutrifitness.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Registro diário de consumo de água.
 */
@Entity(tableName = "water_intake")
data class WaterIntake(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String, // formato yyyy-MM-dd
    val amountMl: Int,
    val time: String // formato HH:mm
)
