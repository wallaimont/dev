import SwiftUI

/// Tela principal com navegação por abas
struct ContentView: View {
    @StateObject private var viewModel = StudyViewModel()
    
    var body: some View {
        TabView {
            NavigationStack {
                TimerView(viewModel: viewModel)
            }
            .tabItem {
                Image(systemName: "play.circle.fill")
                Text("Estudar")
            }
            
            NavigationStack {
                HistoryView(viewModel: viewModel)
            }
            .tabItem {
                Image(systemName: "list.bullet.clipboard")
                Text("Histórico")
            }
            
            NavigationStack {
                StatsView(viewModel: viewModel)
            }
            .tabItem {
                Image(systemName: "chart.bar.fill")
                Text("Estatísticas")
            }
        }
        .tint(.blue)
    }
}

#Preview {
    ContentView()
}
