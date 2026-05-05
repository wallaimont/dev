package com.nutrifitness.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.nutrifitness.R
import com.nutrifitness.databinding.ActivityFoodListBinding
import com.nutrifitness.databinding.DialogAddFoodBinding
import com.nutrifitness.ui.adapter.FoodAdapter
import com.nutrifitness.ui.viewmodel.FoodViewModel

class FoodListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFoodListBinding
    private val viewModel: FoodViewModel by viewModels()
    private lateinit var foodAdapter: FoodAdapter

    private val categories = mapOf(
        "Proteína" to "PROTEIN",
        "Carboidrato" to "CARB",
        "Gordura" to "FAT",
        "Vegetal" to "VEGETABLE",
        "Fruta" to "FRUIT",
        "Laticínio" to "DAIRY",
        "Grão/Leguminosa" to "GRAIN",
        "Outro" to "OTHER"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFoodListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupFoodList()
        setupSearch()
        setupFab()
        observeData()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupFoodList() {
        foodAdapter = FoodAdapter { }
        binding.rvFoodList.apply {
            layoutManager = LinearLayoutManager(this@FoodListActivity)
            adapter = foodAdapter
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.search(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupFab() {
        binding.fabAddFood.setOnClickListener {
            showAddFoodDialog()
        }
    }

    private fun showAddFoodDialog() {
        val dialogBinding = DialogAddFoodBinding.inflate(layoutInflater)

        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories.keys.toList())
        dialogBinding.spinnerCategory.setAdapter(categoryAdapter)
        dialogBinding.spinnerCategory.setText("Proteína", false)

        AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setPositiveButton("Salvar") { _, _ ->
                val name = dialogBinding.etFoodName.text.toString().trim()
                val categoryLabel = dialogBinding.spinnerCategory.text.toString()
                val category = categories[categoryLabel] ?: "OTHER"
                val calories = dialogBinding.etCalories.text.toString().toDoubleOrNull() ?: 0.0
                val protein = dialogBinding.etProtein.text.toString().toDoubleOrNull() ?: 0.0
                val carbs = dialogBinding.etCarbs.text.toString().toDoubleOrNull() ?: 0.0
                val fat = dialogBinding.etFat.text.toString().toDoubleOrNull() ?: 0.0
                val fiber = dialogBinding.etFiber.text.toString().toDoubleOrNull() ?: 0.0

                if (name.isBlank()) {
                    Toast.makeText(this, "Informe o nome do alimento", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                viewModel.addCustomFood(name, category, calories, protein, carbs, fat, fiber)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun observeData() {
        viewModel.foods.observe(this) { foods ->
            foodAdapter.submitList(foods)
        }

        viewModel.saveResult.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Alimento adicionado!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
