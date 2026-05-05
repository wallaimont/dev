# NutriFitness - Sistema de Alimentação Fitness para Android

App Android nativo em Kotlin para controle nutricional e acompanhamento de dieta fitness.

## Funcionalidades

### Dashboard Principal
- **Resumo diário** de calorias consumidas vs. meta
- **Barras de progresso** para macronutrientes (proteína, carboidratos, gordura)
- **Controle de água** com botões rápidos (+200ml, +500ml)
- **Lista de refeições** do dia com opção de excluir
- **Navegação por data** (dia anterior/próximo)

### Registro de Refeições
- Busca de alimentos por nome
- 6 tipos de refeição: Café da Manhã, Almoço, Lanche, Pré-Treino, Pós-Treino, Jantar
- Preview nutricional em tempo real ao selecionar alimento e quantidade
- Campo de observações por refeição

### Banco de Alimentos
- **42 alimentos pré-cadastrados** com dados nutricionais brasileiros
- Categorias: Proteína, Carboidrato, Gordura, Vegetal, Fruta, Laticínio, Grão/Leguminosa
- Busca por nome
- Cadastro de alimentos personalizados

### Perfil do Usuário
- Dados pessoais (nome, idade, peso, altura, sexo)
- Nível de atividade (Sedentário → Muito Ativo)
- Objetivo (Perder Peso, Manter, Ganhar Massa)
- **Cálculo automático** de metas usando fórmula de Mifflin-St Jeor:
  - TMB (Taxa Metabólica Basal)
  - TDEE (Gasto Energético Total Diário)
  - Distribuição de macros baseada no objetivo
  - Meta de água (35ml/kg)

## Arquitetura

```
com.nutrifitness/
├── NutriFitnessApp.kt          # Application com DI manual
├── data/
│   ├── model/                   # Entities Room + DTOs
│   │   ├── UserProfile.kt
│   │   ├── Food.kt
│   │   ├── MealEntry.kt
│   │   ├── WaterIntake.kt
│   │   ├── DailyNutritionSummary.kt
│   │   └── MealEntryWithFood.kt
│   ├── dao/                     # DAOs Room
│   │   ├── UserProfileDao.kt
│   │   ├── FoodDao.kt
│   │   ├── MealEntryDao.kt
│   │   └── WaterIntakeDao.kt
│   ├── repository/
│   │   └── NutriRepository.kt
│   └── NutriFitnessDatabase.kt  # Room DB + seed data
└── ui/
    ├── MainActivity.kt           # Dashboard
    ├── AddMealActivity.kt        # Adicionar refeição
    ├── ProfileActivity.kt        # Perfil e metas
    ├── FoodListActivity.kt       # Banco de alimentos
    ├── viewmodel/
    │   ├── DashboardViewModel.kt
    │   ├── AddMealViewModel.kt
    │   ├── ProfileViewModel.kt
    │   └── FoodViewModel.kt
    └── adapter/
        ├── MealEntryAdapter.kt
        └── FoodAdapter.kt
```

## Tecnologias

| Tecnologia | Uso |
|---|---|
| **Kotlin** | Linguagem principal |
| **Room** | Banco de dados local (SQLite) |
| **LiveData + ViewModel** | Arquitetura MVVM reativa |
| **Coroutines** | Operações assíncronas |
| **Material Design 3** | Interface moderna |
| **ViewBinding** | Acesso type-safe aos layouts |

## Requisitos

- Android Studio Hedgehog (2023.1.1) ou superior
- JDK 17
- Android SDK 34 (minSdk 26)

## Como Executar

1. Abra o projeto no Android Studio
2. Sincronize o Gradle
3. Execute no emulador ou dispositivo (API 26+)

## Alimentos Pré-Cadastrados

O banco de dados vem com 42 alimentos comuns na dieta fitness brasileira:

- **Proteínas**: Peito de frango, ovos, whey, tilápia, carne bovina, atum, salmão, tofu...
- **Carboidratos**: Arroz branco/integral, batata doce, aveia, pão integral, quinoa, tapioca...
- **Gorduras**: Azeite, abacate, castanha do pará, amendoim, pasta de amendoim
- **Vegetais**: Brócolis, espinafre, alface, tomate, cenoura
- **Frutas**: Banana, maçã, morango, mamão, melancia
- **Laticínios**: Iogurte grego, cottage, leite desnatado, minas frescal, ricota
- **Grãos**: Feijão preto, grão de bico, lentilha

## Cálculos Nutricionais

### TMB (Mifflin-St Jeor)
- **Homem**: 10 × peso(kg) + 6.25 × altura(cm) − 5 × idade + 5
- **Mulher**: 10 × peso(kg) + 6.25 × altura(cm) − 5 × idade − 161

### Multiplicadores de Atividade
| Nível | Fator |
|---|---|
| Sedentário | 1.2 |
| Levemente Ativo | 1.375 |
| Moderadamente Ativo | 1.55 |
| Ativo | 1.725 |
| Muito Ativo | 1.9 |

### Ajuste por Objetivo
- **Perder Peso**: TDEE − 500 kcal (déficit)
- **Manter**: TDEE
- **Ganhar Massa**: TDEE + 300 kcal (superávit)

### Distribuição de Macros
| Objetivo | Proteína | Carb | Gordura |
|---|---|---|---|
| Perder Peso | 40% | 30% | 30% |
| Manter | 30% | 40% | 30% |
| Ganhar Massa | 30% | 45% | 25% |
