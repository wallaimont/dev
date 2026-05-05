package com.nutrifitness.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nutrifitness.R
import com.nutrifitness.data.model.MealEntryWithFood
import com.nutrifitness.databinding.ItemMealEntryBinding

class MealEntryAdapter(
    private val onDeleteClick: (Long) -> Unit
) : ListAdapter<MealEntryWithFood, MealEntryAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMealEntryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemMealEntryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(entry: MealEntryWithFood) {
            binding.tvFoodName.text = entry.foodName
            binding.tvMealType.text = getMealTypeLabel(entry.mealType)
            binding.tvQuantity.text = "${entry.quantityGrams.toInt()}g"
            binding.tvEntryCalories.text = "${entry.calories.toInt()} kcal"
            binding.tvEntryMacros.text = "P:${"%.1f".format(entry.proteinG)}g C:${"%.1f".format(entry.carbsG)}g G:${"%.1f".format(entry.fatG)}g"

            val color = getMealTypeColor(entry.mealType)
            binding.viewMealTypeIndicator.setBackgroundColor(
                ContextCompat.getColor(binding.root.context, color)
            )

            binding.btnDeleteEntry.setOnClickListener {
                onDeleteClick(entry.id)
            }
        }
    }

    private fun getMealTypeLabel(type: String): String = when (type) {
        "BREAKFAST" -> "Café da Manhã"
        "LUNCH" -> "Almoço"
        "SNACK" -> "Lanche"
        "PRE_WORKOUT" -> "Pré-Treino"
        "POST_WORKOUT" -> "Pós-Treino"
        "DINNER" -> "Jantar"
        else -> type
    }

    private fun getMealTypeColor(type: String): Int = when (type) {
        "BREAKFAST" -> R.color.breakfast_color
        "LUNCH" -> R.color.lunch_color
        "SNACK" -> R.color.snack_color
        "PRE_WORKOUT" -> R.color.preworkout_color
        "POST_WORKOUT" -> R.color.postworkout_color
        "DINNER" -> R.color.dinner_color
        else -> R.color.primary
    }

    companion object DiffCallback : DiffUtil.ItemCallback<MealEntryWithFood>() {
        override fun areItemsTheSame(oldItem: MealEntryWithFood, newItem: MealEntryWithFood) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: MealEntryWithFood, newItem: MealEntryWithFood) =
            oldItem == newItem
    }
}
