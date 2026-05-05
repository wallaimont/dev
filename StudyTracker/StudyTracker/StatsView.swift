import SwiftUI

/// Tela de estatísticas de estudo
struct StatsView: View {
    @ObservedObject var viewModel: StudyViewModel
    
    var body: some View {
        ScrollView {
            VStack(spacing: 20) {
                
                // MARK: - Cards de Resumo
                summaryCards
                
                // MARK: - Horas por Matéria
                subjectBreakdown
                
                // MARK: - Últimos 7 dias
                weeklyChart
            }
            .padding()
        }
        .navigationTitle("Estatísticas")
    }
    
    // MARK: - Cards de Resumo
    
    private var summaryCards: some View {
        VStack(spacing: 12) {
            HStack(spacing: 12) {
                statCard(
                    title: "Hoje",
                    value: viewModel.todayTotalFormatted,
                    subtitle: "\(viewModel.todaySessions.count) sessões",
                    icon: "sun.max.fill",
                    color: .orange
                )
                statCard(
                    title: "Esta Semana",
                    value: viewModel.weekTotalFormatted,
                    subtitle: "\(viewModel.weekSessions.count) sessões",
                    icon: "calendar",
                    color: .blue
                )
            }
            
            HStack(spacing: 12) {
                statCard(
                    title: "Este Mês",
                    value: viewModel.monthTotalFormatted,
                    subtitle: "\(viewModel.monthSessions.count) sessões",
                    icon: "calendar.badge.clock",
                    color: .purple
                )
                statCard(
                    title: "Média/Dia",
                    value: String(format: "%.1fh", viewModel.dailyAverageHours),
                    subtitle: "últimos 30 dias",
                    icon: "chart.line.uptrend.xyaxis",
                    color: .green
                )
            }
        }
    }
    
    private func statCard(title: String, value: String, subtitle: String, icon: String, color: Color) -> some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack {
                Image(systemName: icon)
                    .foregroundColor(color)
                Text(title)
                    .font(.caption)
                    .foregroundColor(.secondary)
            }
            
            Text(value)
                .font(.title2)
                .fontWeight(.bold)
                .foregroundColor(color)
            
            Text(subtitle)
                .font(.caption2)
                .foregroundColor(.secondary)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding()
        .background(Color(.systemGray6))
        .cornerRadius(16)
    }
    
    // MARK: - Horas por Matéria
    
    private var subjectBreakdown: some View {
        VStack(alignment: .leading, spacing: 12) {
            HStack {
                Image(systemName: "chart.bar.fill")
                    .foregroundColor(.blue)
                Text("Horas por Matéria")
                    .font(.headline)
            }
            
            if viewModel.subjectStats.isEmpty {
                Text("Nenhum dado ainda")
                    .foregroundColor(.secondary)
                    .font(.subheadline)
                    .padding(.vertical, 8)
            } else {
                let maxHours = viewModel.subjectStats.first?.hours ?? 1
                
                ForEach(viewModel.subjectStats, id: \.subject) { stat in
                    HStack(spacing: 12) {
                        Text(stat.subject)
                            .font(.subheadline)
                            .frame(width: 100, alignment: .leading)
                        
                        GeometryReader { geometry in
                            let barWidth = max(4, geometry.size.width * CGFloat(stat.hours / maxHours))
                            RoundedRectangle(cornerRadius: 4)
                                .fill(colorForSubject(stat.subject))
                                .frame(width: barWidth, height: 24)
                        }
                        .frame(height: 24)
                        
                        Text(String(format: "%.1fh", stat.hours))
                            .font(.caption)
                            .fontWeight(.medium)
                            .foregroundColor(.secondary)
                            .frame(width: 45, alignment: .trailing)
                    }
                }
            }
        }
        .padding()
        .background(Color(.systemGray6))
        .cornerRadius(16)
    }
    
    // MARK: - Gráfico Semanal Simplificado
    
    private var weeklyChart: some View {
        VStack(alignment: .leading, spacing: 12) {
            HStack {
                Image(systemName: "chart.bar.xaxis")
                    .foregroundColor(.green)
                Text("Últimos 7 Dias")
                    .font(.headline)
            }
            
            let dailyData = last7DaysData()
            let maxMinutes = max(dailyData.map(\.minutes).max() ?? 1, 1)
            
            HStack(alignment: .bottom, spacing: 8) {
                ForEach(dailyData, id: \.label) { day in
                    VStack(spacing: 4) {
                        Text(day.minutes > 0 ? "\(day.minutes)m" : "")
                            .font(.system(size: 9))
                            .foregroundColor(.secondary)
                        
                        RoundedRectangle(cornerRadius: 4)
                            .fill(day.isToday ? Color.green : Color.blue.opacity(0.6))
                            .frame(height: max(4, CGFloat(day.minutes) / CGFloat(maxMinutes) * 120))
                        
                        Text(day.label)
                            .font(.system(size: 10))
                            .foregroundColor(.secondary)
                    }
                    .frame(maxWidth: .infinity)
                }
            }
            .frame(height: 160)
        }
        .padding()
        .background(Color(.systemGray6))
        .cornerRadius(16)
    }
    
    // MARK: - Helpers
    
    private struct DayData {
        let label: String
        let minutes: Int
        let isToday: Bool
    }
    
    private func last7DaysData() -> [DayData] {
        let calendar = Calendar.current
        let formatter = DateFormatter()
        formatter.locale = Locale(identifier: "pt_BR")
        formatter.dateFormat = "EEE"
        
        var result: [DayData] = []
        
        for daysAgo in (0..<7).reversed() {
            guard let date = calendar.date(byAdding: .day, value: -daysAgo, to: Date()) else { continue }
            let startOfDay = calendar.startOfDay(for: date)
            guard let endOfDay = calendar.date(byAdding: .day, value: 1, to: startOfDay) else { continue }
            
            let daySessions = viewModel.sessions.filter {
                $0.startDate >= startOfDay && $0.startDate < endOfDay
            }
            let totalMinutes = daySessions.reduce(0) { $0 + Int($1.duration) } / 60
            
            result.append(DayData(
                label: formatter.string(from: date).capitalized,
                minutes: totalMinutes,
                isToday: daysAgo == 0
            ))
        }
        
        return result
    }
    
    private func colorForSubject(_ subject: String) -> Color {
        let colors: [Color] = [.blue, .green, .orange, .purple, .pink, .cyan, .indigo, .mint, .teal, .yellow]
        let index = abs(subject.hashValue) % colors.count
        return colors[index]
    }
}

#Preview {
    NavigationStack {
        StatsView(viewModel: StudyViewModel())
    }
}
