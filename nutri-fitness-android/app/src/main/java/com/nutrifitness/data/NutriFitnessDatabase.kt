package com.nutrifitness.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nutrifitness.data.dao.FoodDao
import com.nutrifitness.data.dao.MealEntryDao
import com.nutrifitness.data.dao.UserProfileDao
import com.nutrifitness.data.dao.WaterIntakeDao
import com.nutrifitness.data.model.Food
import com.nutrifitness.data.model.MealEntry
import com.nutrifitness.data.model.UserProfile
import com.nutrifitness.data.model.WaterIntake
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [UserProfile::class, Food::class, MealEntry::class, WaterIntake::class],
    version = 1,
    exportSchema = false
)
abstract class NutriFitnessDatabase : RoomDatabase() {

    abstract fun userProfileDao(): UserProfileDao
    abstract fun foodDao(): FoodDao
    abstract fun mealEntryDao(): MealEntryDao
    abstract fun waterIntakeDao(): WaterIntakeDao

    companion object {
        @Volatile
        private var INSTANCE: NutriFitnessDatabase? = null

        fun getDatabase(context: Context): NutriFitnessDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NutriFitnessDatabase::class.java,
                    "nutri_fitness_db"
                )
                    .addCallback(SeedCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class SeedCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateDatabase(database.foodDao())
                }
            }
        }

        private suspend fun populateDatabase(foodDao: FoodDao) {
            val foods = listOf(
                // Proteínas
                Food(name = "Peito de Frango Grelhado", category = "PROTEIN", caloriesPer100g = 165.0, proteinPer100g = 31.0, carbsPer100g = 0.0, fatPer100g = 3.6, fiberPer100g = 0.0),
                Food(name = "Ovo Inteiro Cozido", category = "PROTEIN", caloriesPer100g = 155.0, proteinPer100g = 13.0, carbsPer100g = 1.1, fatPer100g = 11.0, fiberPer100g = 0.0),
                Food(name = "Clara de Ovo", category = "PROTEIN", caloriesPer100g = 52.0, proteinPer100g = 11.0, carbsPer100g = 0.7, fatPer100g = 0.2, fiberPer100g = 0.0),
                Food(name = "Whey Protein (dose 30g)", category = "PROTEIN", caloriesPer100g = 400.0, proteinPer100g = 80.0, carbsPer100g = 6.7, fatPer100g = 3.3, fiberPer100g = 0.0),
                Food(name = "Tilápia Grelhada", category = "PROTEIN", caloriesPer100g = 128.0, proteinPer100g = 26.0, carbsPer100g = 0.0, fatPer100g = 2.7, fiberPer100g = 0.0),
                Food(name = "Carne Bovina Patinho", category = "PROTEIN", caloriesPer100g = 219.0, proteinPer100g = 35.9, carbsPer100g = 0.0, fatPer100g = 7.3, fiberPer100g = 0.0),
                Food(name = "Atum em Lata (água)", category = "PROTEIN", caloriesPer100g = 116.0, proteinPer100g = 25.5, carbsPer100g = 0.0, fatPer100g = 0.8, fiberPer100g = 0.0),
                Food(name = "Salmão Grelhado", category = "PROTEIN", caloriesPer100g = 208.0, proteinPer100g = 20.0, carbsPer100g = 0.0, fatPer100g = 13.0, fiberPer100g = 0.0),
                Food(name = "Peito de Peru", category = "PROTEIN", caloriesPer100g = 104.0, proteinPer100g = 17.0, carbsPer100g = 4.2, fatPer100g = 1.7, fiberPer100g = 0.0),
                Food(name = "Tofu Firme", category = "PROTEIN", caloriesPer100g = 144.0, proteinPer100g = 17.0, carbsPer100g = 3.0, fatPer100g = 8.7, fiberPer100g = 0.3),

                // Carboidratos
                Food(name = "Arroz Branco Cozido", category = "CARB", caloriesPer100g = 130.0, proteinPer100g = 2.7, carbsPer100g = 28.0, fatPer100g = 0.3, fiberPer100g = 0.4),
                Food(name = "Arroz Integral Cozido", category = "CARB", caloriesPer100g = 124.0, proteinPer100g = 2.6, carbsPer100g = 25.6, fatPer100g = 1.0, fiberPer100g = 1.8),
                Food(name = "Batata Doce Cozida", category = "CARB", caloriesPer100g = 86.0, proteinPer100g = 1.6, carbsPer100g = 20.1, fatPer100g = 0.1, fiberPer100g = 3.0),
                Food(name = "Macarrão Integral Cozido", category = "CARB", caloriesPer100g = 124.0, proteinPer100g = 5.3, carbsPer100g = 26.5, fatPer100g = 0.5, fiberPer100g = 3.2),
                Food(name = "Aveia em Flocos", category = "CARB", caloriesPer100g = 389.0, proteinPer100g = 16.9, carbsPer100g = 66.3, fatPer100g = 6.9, fiberPer100g = 10.6),
                Food(name = "Pão Integral", category = "CARB", caloriesPer100g = 247.0, proteinPer100g = 13.0, carbsPer100g = 41.0, fatPer100g = 3.4, fiberPer100g = 6.0),
                Food(name = "Mandioca Cozida", category = "CARB", caloriesPer100g = 160.0, proteinPer100g = 1.4, carbsPer100g = 38.1, fatPer100g = 0.3, fiberPer100g = 1.8),
                Food(name = "Quinoa Cozida", category = "CARB", caloriesPer100g = 120.0, proteinPer100g = 4.4, carbsPer100g = 21.3, fatPer100g = 1.9, fiberPer100g = 2.8),
                Food(name = "Tapioca (goma)", category = "CARB", caloriesPer100g = 358.0, proteinPer100g = 0.0, carbsPer100g = 88.7, fatPer100g = 0.0, fiberPer100g = 0.9),
                Food(name = "Banana Prata", category = "FRUIT", caloriesPer100g = 98.0, proteinPer100g = 1.3, carbsPer100g = 26.0, fatPer100g = 0.1, fiberPer100g = 2.0),

                // Gorduras saudáveis
                Food(name = "Azeite de Oliva", category = "FAT", caloriesPer100g = 884.0, proteinPer100g = 0.0, carbsPer100g = 0.0, fatPer100g = 100.0, fiberPer100g = 0.0),
                Food(name = "Abacate", category = "FAT", caloriesPer100g = 160.0, proteinPer100g = 2.0, carbsPer100g = 8.5, fatPer100g = 14.7, fiberPer100g = 6.7),
                Food(name = "Castanha do Pará", category = "FAT", caloriesPer100g = 656.0, proteinPer100g = 14.3, carbsPer100g = 12.3, fatPer100g = 66.4, fiberPer100g = 7.5),
                Food(name = "Amendoim Torrado", category = "FAT", caloriesPer100g = 567.0, proteinPer100g = 25.8, carbsPer100g = 16.1, fatPer100g = 49.2, fiberPer100g = 8.5),
                Food(name = "Pasta de Amendoim", category = "FAT", caloriesPer100g = 588.0, proteinPer100g = 25.0, carbsPer100g = 20.0, fatPer100g = 50.0, fiberPer100g = 6.0),

                // Vegetais
                Food(name = "Brócolis Cozido", category = "VEGETABLE", caloriesPer100g = 35.0, proteinPer100g = 2.4, carbsPer100g = 7.2, fatPer100g = 0.4, fiberPer100g = 3.3),
                Food(name = "Espinafre Cozido", category = "VEGETABLE", caloriesPer100g = 23.0, proteinPer100g = 2.9, carbsPer100g = 3.6, fatPer100g = 0.3, fiberPer100g = 2.4),
                Food(name = "Alface", category = "VEGETABLE", caloriesPer100g = 15.0, proteinPer100g = 1.4, carbsPer100g = 2.9, fatPer100g = 0.2, fiberPer100g = 1.3),
                Food(name = "Tomate", category = "VEGETABLE", caloriesPer100g = 18.0, proteinPer100g = 0.9, carbsPer100g = 3.9, fatPer100g = 0.2, fiberPer100g = 1.2),
                Food(name = "Cenoura Crua", category = "VEGETABLE", caloriesPer100g = 41.0, proteinPer100g = 0.9, carbsPer100g = 9.6, fatPer100g = 0.2, fiberPer100g = 2.8),

                // Frutas
                Food(name = "Maçã", category = "FRUIT", caloriesPer100g = 52.0, proteinPer100g = 0.3, carbsPer100g = 13.8, fatPer100g = 0.2, fiberPer100g = 2.4),
                Food(name = "Morango", category = "FRUIT", caloriesPer100g = 32.0, proteinPer100g = 0.7, carbsPer100g = 7.7, fatPer100g = 0.3, fiberPer100g = 2.0),
                Food(name = "Mamão Papaia", category = "FRUIT", caloriesPer100g = 43.0, proteinPer100g = 0.5, carbsPer100g = 11.0, fatPer100g = 0.3, fiberPer100g = 1.7),
                Food(name = "Melancia", category = "FRUIT", caloriesPer100g = 30.0, proteinPer100g = 0.6, carbsPer100g = 7.6, fatPer100g = 0.2, fiberPer100g = 0.4),

                // Laticínios
                Food(name = "Iogurte Grego Natural", category = "DAIRY", caloriesPer100g = 97.0, proteinPer100g = 9.0, carbsPer100g = 3.6, fatPer100g = 5.0, fiberPer100g = 0.0),
                Food(name = "Queijo Cottage", category = "DAIRY", caloriesPer100g = 98.0, proteinPer100g = 11.0, carbsPer100g = 3.4, fatPer100g = 4.3, fiberPer100g = 0.0),
                Food(name = "Leite Desnatado", category = "DAIRY", caloriesPer100g = 35.0, proteinPer100g = 3.4, carbsPer100g = 5.0, fatPer100g = 0.1, fiberPer100g = 0.0),
                Food(name = "Queijo Minas Frescal", category = "DAIRY", caloriesPer100g = 264.0, proteinPer100g = 17.4, carbsPer100g = 3.2, fatPer100g = 20.2, fiberPer100g = 0.0),
                Food(name = "Ricota", category = "DAIRY", caloriesPer100g = 140.0, proteinPer100g = 12.6, carbsPer100g = 3.5, fatPer100g = 8.1, fiberPer100g = 0.0),

                // Grãos/Leguminosas
                Food(name = "Feijão Preto Cozido", category = "GRAIN", caloriesPer100g = 77.0, proteinPer100g = 4.5, carbsPer100g = 14.0, fatPer100g = 0.5, fiberPer100g = 8.7),
                Food(name = "Grão de Bico Cozido", category = "GRAIN", caloriesPer100g = 164.0, proteinPer100g = 8.9, carbsPer100g = 27.4, fatPer100g = 2.6, fiberPer100g = 7.6),
                Food(name = "Lentilha Cozida", category = "GRAIN", caloriesPer100g = 116.0, proteinPer100g = 9.0, carbsPer100g = 20.0, fatPer100g = 0.4, fiberPer100g = 7.9)
            )
            foodDao.insertAll(foods)
        }
    }
}
