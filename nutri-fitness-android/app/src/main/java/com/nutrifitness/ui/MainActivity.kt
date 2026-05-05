package com.nutrifitness.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.nutrifitness.R
import com.nutrifitness.databinding.ActivityMainBinding
import com.nutrifitness.ui.adapter.MealEntryAdapter
import com.nutrifitness.ui.viewmodel.DashboardViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: DashboardViewModel by viewModels()
    private lateinit var mealAdapter: MealEntryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupNavigation()
        setupWaterButtons()
        observeData()
    }

    override fun onResume() {
        super.onResume()
        // Recarrega ao voltar de outras telas
        viewModel.setDate(viewModel.selectedDate.value ?: LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
    }

    private fun setupRecyclerView() {
        mealAdapter = MealEntryAdapter { entryId ->
            viewModel.deleteMealEntry(entryId)
        }
        binding.rvMealEntries.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = mealAdapter
        }
    }

    private fun setupNavigation() {
        binding.btnPreviousDay.setOnClickListener { viewModel.previousDay() }
        binding.btnNextDay.setOnClickListener { viewModel.nextDay() }

        binding.btnAddMeal.setOnClickListener {
            val intent = Intent(this, AddMealActivity::class.java)
            intent.putExtra("date", viewModel.selectedDate.value)
            startActivity(intent)
        }

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> true
                R.id.nav_foods -> {
                    startActivity(Intent(this, FoodListActivity::class.java))
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun setupWaterButtons() {
        binding.btnAddWater200.setOnClickListener { viewModel.addWater(200) }
        binding.btnAddWater500.setOnClickListener { viewModel.addWater(500) }
    }

    private fun observeData() {
        viewModel.selectedDate.observe(this) { dateStr ->
            val date = LocalDate.parse(dateStr)
            val formatter = DateTimeFormatter.ofPattern("dd 'de' MMMM, yyyy", Locale("pt", "BR"))
            val today = LocalDate.now()
            binding.tvSelectedDate.text = when (date) {
                today -> "Hoje - ${date.format(formatter)}"
                today.minusDays(1) -> "Ontem - ${date.format(formatter)}"
                else -> date.format(formatter)
            }
        }

        viewModel.userProfile.observe(this) { profile ->
            if (profile == null) {
                // Se não tem perfil, redireciona
                startActivity(Intent(this, ProfileActivity::class.java))
                return@observe
            }
            binding.tvCaloriesTarget.text = "/ ${profile.dailyCalorieTarget} kcal"
            binding.tvWaterTarget.text = "/ ${profile.dailyWaterTargetMl} ml"
            updateProgressBars(profile.dailyCalorieTarget, profile.dailyProteinTargetG,
                profile.dailyCarbTargetG, profile.dailyFatTargetG, profile.dailyWaterTargetMl)
        }

        viewModel.dailySummary.observe(this) { summary ->
            val cal = summary?.totalCalories?.toInt() ?: 0
            binding.tvCaloriesCurrent.text = cal.toString()

            val prot = summary?.totalProteinG?.toInt() ?: 0
            val carbs = summary?.totalCarbsG?.toInt() ?: 0
            val fat = summary?.totalFatG?.toInt() ?: 0

            val profile = viewModel.userProfile.value
            binding.tvProtein.text = "${prot}g / ${profile?.dailyProteinTargetG ?: 0}g"
            binding.tvCarbs.text = "${carbs}g / ${profile?.dailyCarbTargetG ?: 0}g"
            binding.tvFat.text = "${fat}g / ${profile?.dailyFatTargetG ?: 0}g"

            updateProgressValues(cal, prot, carbs, fat)
        }

        viewModel.mealEntries.observe(this) { entries ->
            mealAdapter.submitList(entries)
            binding.tvNoMeals.visibility = if (entries.isNullOrEmpty()) View.VISIBLE else View.GONE
            binding.rvMealEntries.visibility = if (entries.isNullOrEmpty()) View.GONE else View.VISIBLE
        }

        viewModel.totalWater.observe(this) { total ->
            binding.tvWaterCurrent.text = total.toString()
            val target = viewModel.userProfile.value?.dailyWaterTargetMl ?: 2000
            val progress = if (target > 0) (total * 100 / target).coerceAtMost(100) else 0
            binding.progressWater.progress = progress
        }
    }

    private fun updateProgressBars(calTarget: Int, protTarget: Int, carbTarget: Int, fatTarget: Int, waterTarget: Int) {
        binding.progressCalories.max = 100
        binding.progressProtein.max = 100
        binding.progressCarbs.max = 100
        binding.progressFat.max = 100
        binding.progressWater.max = 100
    }

    private fun updateProgressValues(cal: Int, prot: Int, carbs: Int, fat: Int) {
        val profile = viewModel.userProfile.value ?: return
        binding.progressCalories.progress = safeProgress(cal, profile.dailyCalorieTarget)
        binding.progressProtein.progress = safeProgress(prot, profile.dailyProteinTargetG)
        binding.progressCarbs.progress = safeProgress(carbs, profile.dailyCarbTargetG)
        binding.progressFat.progress = safeProgress(fat, profile.dailyFatTargetG)
    }

    private fun safeProgress(current: Int, target: Int): Int {
        if (target <= 0) return 0
        return (current * 100 / target).coerceAtMost(100)
    }
}
