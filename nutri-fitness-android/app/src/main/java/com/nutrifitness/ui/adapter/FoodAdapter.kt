package com.nutrifitness.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nutrifitness.data.model.Food
import com.nutrifitness.databinding.ItemFoodBinding

class FoodAdapter(
    private val onFoodClick: (Food) -> Unit
) : ListAdapter<Food, FoodAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemFoodBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(food: Food) {
            binding.tvFoodItemName.text = food.name
            binding.tvFoodItemCategory.text = getCategoryLabel(food.category)
            binding.tvFoodItemCalories.text = "${food.caloriesPer100g.toInt()} kcal"

            binding.root.setOnClickListener {
                onFoodClick(food)
            }
        }
    }

    private fun getCategoryLabel(category: String): String = when (category) {
        "PROTEIN" -> "🥩 Proteína"
        "CARB" -> "🍚 Carboidrato"
        "FAT" -> "🥑 Gordura"
        "VEGETABLE" -> "🥦 Vegetal"
        "FRUIT" -> "🍎 Fruta"
        "DAIRY" -> "🥛 Laticínio"
        "GRAIN" -> "🫘 Grão"
        "OTHER" -> "📦 Outro"
        else -> category
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Food>() {
        override fun areItemsTheSame(oldItem: Food, newItem: Food) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Food, newItem: Food) = oldItem == newItem
    }
}
