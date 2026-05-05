package com.nutrifitness.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.nutrifitness.databinding.ActivityProfileBinding
import com.nutrifitness.ui.viewmodel.ProfileViewModel

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private val viewModel: ProfileViewModel by viewModels()

    private val genderOptions = mapOf("Masculino" to "M", "Feminino" to "F")
    private val activityOptions = mapOf(
        "Sedentário" to "SEDENTARY",
        "Levemente Ativo" to "LIGHT",
        "Moderadamente Ativo" to "MODERATE",
        "Ativo" to "ACTIVE",
        "Muito Ativo" to "VERY_ACTIVE"
    )
    private val goalOptions = mapOf(
        "Perder Peso" to "LOSE_WEIGHT",
        "Manter Peso" to "MAINTAIN",
        "Ganhar Massa" to "GAIN_MUSCLE"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupSpinners()
        setupSaveButton()
        observeData()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupSpinners() {
        binding.spinnerGender.setAdapter(
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, genderOptions.keys.toList())
        )
        binding.spinnerActivityLevel.setAdapter(
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, activityOptions.keys.toList())
        )
        binding.spinnerGoal.setAdapter(
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, goalOptions.keys.toList())
        )
    }

    private fun setupSaveButton() {
        binding.btnSaveProfile.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            if (name.isBlank()) {
                Toast.makeText(this, "Informe seu nome", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val age = binding.etAge.text.toString().toIntOrNull()
            if (age == null || age <= 0) {
                Toast.makeText(this, "Informe sua idade", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val weight = binding.etWeight.text.toString().toDoubleOrNull()
            if (weight == null || weight <= 0) {
                Toast.makeText(this, "Informe seu peso", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val height = binding.etHeight.text.toString().toDoubleOrNull()
            if (height == null || height <= 0) {
                Toast.makeText(this, "Informe sua altura", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val gender = genderOptions[binding.spinnerGender.text.toString()]
            if (gender == null) {
                Toast.makeText(this, "Selecione o sexo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val activityLevel = activityOptions[binding.spinnerActivityLevel.text.toString()]
            if (activityLevel == null) {
                Toast.makeText(this, "Selecione o nível de atividade", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val goal = goalOptions[binding.spinnerGoal.text.toString()]
            if (goal == null) {
                Toast.makeText(this, "Selecione o objetivo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.saveProfile(name, age, weight, height, gender, activityLevel, goal)
        }
    }

    private fun observeData() {
        viewModel.profile.observe(this) { profile ->
            if (profile != null) {
                binding.etName.setText(profile.name)
                binding.etAge.setText(profile.age.toString())
                binding.etWeight.setText(profile.weightKg.toString())
                binding.etHeight.setText(profile.heightCm.toString())

                val genderLabel = genderOptions.entries.find { it.value == profile.gender }?.key
                genderLabel?.let { binding.spinnerGender.setText(it, false) }

                val activityLabel = activityOptions.entries.find { it.value == profile.activityLevel }?.key
                activityLabel?.let { binding.spinnerActivityLevel.setText(it, false) }

                val goalLabel = goalOptions.entries.find { it.value == profile.goal }?.key
                goalLabel?.let { binding.spinnerGoal.setText(it, false) }

                // Mostrar metas calculadas
                binding.cardCalculatedGoals.visibility = android.view.View.VISIBLE
                binding.tvGoalCalories.text = "🔥 ${profile.dailyCalorieTarget} kcal/dia"
                binding.tvGoalMacros.text = "P: ${profile.dailyProteinTargetG}g | C: ${profile.dailyCarbTargetG}g | G: ${profile.dailyFatTargetG}g"
                binding.tvGoalWater.text = "💧 ${profile.dailyWaterTargetMl} ml/dia"
            }
        }

        viewModel.saveResult.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Perfil salvo com sucesso!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
