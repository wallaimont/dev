package com.nutrifitness.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.nutrifitness.databinding.ActivityAddMealBinding
import com.nutrifitness.ui.adapter.FoodAdapter
import com.nutrifitness.ui.viewmodel.AddMealViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AddMealActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddMealBinding
    private val viewModel: AddMealViewModel by viewModels()
    private lateinit var foodAdapter: FoodAdapter

    private val mealTypes = mapOf(
        "Café da Manhã" to "BREAKFAST",
        "Almoço" to "LUNCH",
        "Lanche" to "SNACK",
        "Pré-Treino" to "PRE_WORKOUT",
        "Pós-Treino" to "POST_WORKOUT",
        "Jantar" to "DINNER"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMealBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupMealTypeSpinner()
        setupFoodList()
        setupQuantityListener()
        setupSaveButton()
        observeData()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupMealTypeSpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, mealTypes.keys.toList())
        binding.spinnerMealType.setAdapter(adapter)
        binding.spinnerMealType.setText("Almoço", false)
    }

    private fun setupFoodList() {
        foodAdapter = FoodAdapter { food ->
            viewModel.selectFood(food)
        }
        binding.rvFoods.apply {
            layoutManager = LinearLayoutManager(this@AddMealActivity)
            adapter = foodAdapter
        }

        binding.etFoodSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.search(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupQuantityListener() {
        binding.etQuantity.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateNutritionPreview()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupSaveButton() {
        binding.btnSaveMeal.setOnClickListener {
            val selectedMealLabel = binding.spinnerMealType.text.toString()
            val mealType = mealTypes[selectedMealLabel]
            if (mealType == null) {
                Toast.makeText(this, "Selecione o tipo de refeição", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (viewModel.selectedFood.value == null) {
                Toast.makeText(this, "Selecione um alimento", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val quantity = binding.etQuantity.text.toString().toDoubleOrNull()
            if (quantity == null || quantity <= 0) {
                Toast.makeText(this, "Informe a quantidade em gramas", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val date = intent.getStringExtra("date")
                ?: LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
            val notes = binding.etNotes.text.toString()

            viewModel.saveMealEntry(date, mealType, quantity, notes)
        }
    }

    private fun observeData() {
        viewModel.foods.observe(this) { foods ->
            foodAdapter.submitList(foods)
        }

        viewModel.selectedFood.observe(this) { food ->
            if (food != null) {
                binding.cardSelectedFood.visibility = View.VISIBLE
                binding.tvSelectedFoodName.text = food.name
                binding.tvSelectedFoodInfo.text =
                    "Por 100g: ${food.caloriesPer100g.toInt()} kcal | P: ${food.proteinPer100g}g | C: ${food.carbsPer100g}g | G: ${food.fatPer100g}g"
                updateNutritionPreview()
            }
        }

        viewModel.saveResult.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Refeição adicionada!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun updateNutritionPreview() {
        val food = viewModel.selectedFood.value ?: return
        val quantity = binding.etQuantity.text.toString().toDoubleOrNull() ?: return

        val factor = quantity / 100.0
        binding.cardNutritionPreview.visibility = View.VISIBLE
        binding.tvPreviewCalories.text = "${(food.caloriesPer100g * factor).toInt()} kcal"
        binding.tvPreviewProtein.text = "P: ${"%.1f".format(food.proteinPer100g * factor)}g"
        binding.tvPreviewCarbs.text = "C: ${"%.1f".format(food.carbsPer100g * factor)}g"
        binding.tvPreviewFat.text = "G: ${"%.1f".format(food.fatPer100g * factor)}g"
    }
}
