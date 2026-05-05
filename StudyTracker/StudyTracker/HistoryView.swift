import SwiftUI

/// Tela de histórico de sessões de estudo
struct HistoryView: View {
    @ObservedObject var viewModel: StudyViewModel
    @State private var filterSubject: String? = nil
    
    private var filteredSessions: [StudySession] {
        if let filter = filterSubject {
            return viewModel.sessions.filter { $0.subject == filter }
        }
        return viewModel.sessions
    }
    
    private var uniqueSubjects: [String] {
        Array(Set(viewModel.sessions.map(\.subject))).sorted()
    }
    
    private var groupedSessions: [(key: String, sessions: [StudySession])] {
        let formatter = DateFormatter()
        formatter.locale = Locale(identifier: "pt_BR")
        formatter.dateStyle = .full
        
        let grouped = Dictionary(grouping: filteredSessions) { session in
            formatter.string(from: session.startDate)
        }
        
        return grouped.map { (key: $0.key, sessions: $0.value) }
            .sorted { ($0.sessions.first?.startDate ?? .distantPast) > ($1.sessions.first?.startDate ?? .distantPast) }
    }
    
    var body: some View {
        Group {
            if viewModel.sessions.isEmpty {
                emptyState
            } else {
                sessionsList
            }
        }
        .navigationTitle("Histórico")
    }
    
    // MARK: - Componentes
    
    private var emptyState: some View {
        VStack(spacing: 16) {
            Image(systemName: "books.vertical")
                .font(.system(size: 60))
                .foregroundColor(.secondary)
            Text("Nenhuma sessão registrada")
                .font(.title3)
                .foregroundColor(.secondary)
            Text("Comece a estudar para ver seu histórico aqui!")
                .font(.subheadline)
                .foregroundColor(.secondary)
                .multilineTextAlignment(.center)
        }
        .padding()
    }
    
    private var sessionsList: some View {
        List {
            // Filtro por matéria
            Section {
                ScrollView(.horizontal, showsIndicators: false) {
                    HStack(spacing: 8) {
                        filterChip(title: "Todas", isSelected: filterSubject == nil) {
                            filterSubject = nil
                        }
                        ForEach(uniqueSubjects, id: \.self) { subject in
                            filterChip(title: subject, isSelected: filterSubject == subject) {
                                filterSubject = subject
                            }
                        }
                    }
                    .padding(.vertical, 4)
                }
            }
            
            // Sessões agrupadas por dia
            ForEach(groupedSessions, id: \.key) { group in
                Section(header: Text(group.key)) {
                    ForEach(group.sessions) { session in
                        sessionRow(session)
                    }
                    .onDelete { offsets in
                        let sessionsToDelete = offsets.map { group.sessions[$0] }
                        for session in sessionsToDelete {
                            viewModel.deleteSession(session)
                        }
                    }
                }
            }
        }
        .listStyle(.insetGrouped)
    }
    
    private func filterChip(title: String, isSelected: Bool, action: @escaping () -> Void) -> some View {
        Button(action: action) {
            Text(title)
                .font(.subheadline)
                .padding(.horizontal, 14)
                .padding(.vertical, 6)
                .background(isSelected ? Color.blue : Color(.systemGray5))
                .foregroundColor(isSelected ? .white : .primary)
                .cornerRadius(20)
        }
    }
    
    private func sessionRow(_ session: StudySession) -> some View {
        VStack(alignment: .leading, spacing: 6) {
            HStack {
                Image(systemName: "book.fill")
                    .foregroundColor(.blue)
                Text(session.subject)
                    .font(.headline)
                Spacer()
                Text(session.formattedDuration)
                    .font(.subheadline)
                    .fontWeight(.semibold)
                    .foregroundColor(.blue)
            }
            
            HStack {
                Image(systemName: "clock")
                    .font(.caption)
                    .foregroundColor(.secondary)
                Text(formatTime(session.startDate))
                    .font(.caption)
                    .foregroundColor(.secondary)
                
                if let end = session.endDate {
                    Text("→")
                        .font(.caption)
                        .foregroundColor(.secondary)
                    Text(formatTime(end))
                        .font(.caption)
                        .foregroundColor(.secondary)
                }
            }
            
            if !session.notes.isEmpty {
                Text(session.notes)
                    .font(.caption)
                    .foregroundColor(.secondary)
                    .lineLimit(2)
            }
        }
        .padding(.vertical, 4)
    }
    
    private func formatTime(_ date: Date) -> String {
        let formatter = DateFormatter()
        formatter.locale = Locale(identifier: "pt_BR")
        formatter.timeStyle = .short
        return formatter.string(from: date)
    }
}

#Preview {
    NavigationStack {
        HistoryView(viewModel: StudyViewModel())
    }
}
