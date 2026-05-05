# StudyTracker 📚

Aplicativo iOS para estudantes calcularem e acompanharem suas horas de estudo.

## Funcionalidades

- **Cronômetro de Estudo**: Inicie e pare sessões de estudo com um toque
- **Seleção de Matéria**: Escolha entre 13 matérias pré-definidas ou crie a sua
- **Anotações**: Adicione notas durante cada sessão de estudo
- **Histórico Completo**: Veja todas as sessões agrupadas por dia, com filtro por matéria
- **Estatísticas Detalhadas**:
  - Total de horas hoje / semana / mês
  - Média diária de estudo
  - Gráfico de barras por matéria
  - Gráfico dos últimos 7 dias

## Requisitos

- macOS com Xcode 15+
- iOS 17.0+
- Swift 5.9+

## Como Rodar

1. Abra `StudyTracker.xcodeproj` no Xcode
2. Selecione um simulador iPhone
3. Pressione `Cmd + R` para compilar e executar

## Estrutura do Projeto

```
StudyTracker/
├── StudyTrackerApp.swift    # Entry point do app
├── ContentView.swift        # TabView com navegação principal
├── StudySession.swift       # Modelo de dados
├── StudyViewModel.swift     # ViewModel com lógica de negócio e persistência
├── TimerView.swift          # Tela do cronômetro
├── HistoryView.swift        # Tela de histórico
├── StatsView.swift          # Tela de estatísticas
└── Assets.xcassets/         # Recursos visuais
```

## Tecnologias

- **SwiftUI** — Interface declarativa
- **MVVM** — Arquitetura
- **UserDefaults** — Persistência local dos dados
- **Timer** — Cronômetro em tempo real
