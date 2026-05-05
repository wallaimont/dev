import Foundation
import SwiftUI

/// ViewModel principal que gerencia sessões de estudo, timer e persistência
@MainActor
class StudyViewModel: ObservableObject {
    
    // MARK: - Published Properties
    
    @Published var sessions: [StudySession] = []
    @Published var activeSession: StudySession?
    @Published var elapsedSeconds: Int = 0
    @Published var selectedSubject: String = DefaultSubjects.all[0]
    @Published var customSubject: String = ""
    @Published var sessionNotes: String = ""
    
    // MARK: - Timer
    
    private var timer: Timer?
    
    // MARK: - Computed Properties
    
    var isStudying: Bool {
        activeSession != nil
    }
    
    var elapsedFormatted: String {
        let hours = elapsedSeconds / 3600
        let minutes = (elapsedSeconds % 3600) / 60
        let seconds = elapsedSeconds % 60
        return String(format: "%02d:%02d:%02d", hours, minutes, seconds)
    }
    
    var todaySessions: [StudySession] {
        let calendar = Calendar.current
        return sessions.filter { calendar.isDateInToday($0.startDate) }
    }
    
    var todayTotalSeconds: Int {
        todaySessions.reduce(0) { $0 + Int($1.duration) }
    }
    
    var todayTotalFormatted: String {
        formatTotalTime(todayTotalSeconds)
    }
    
    var weekSessions: [StudySession] {
        let calendar = Calendar.current
        let now = Date()
        guard let weekAgo = calendar.date(byAdding: .day, value: -7, to: now) else { return [] }
        return sessions.filter { $0.startDate >= weekAgo }
    }
    
    var weekTotalSeconds: Int {
        weekSessions.reduce(0) { $0 + Int($1.duration) }
    }
    
    var weekTotalFormatted: String {
        formatTotalTime(weekTotalSeconds)
    }
    
    var monthSessions: [StudySession] {
        let calendar = Calendar.current
        let now = Date()
        guard let monthAgo = calendar.date(byAdding: .month, value: -1, to: now) else { return [] }
        return sessions.filter { $0.startDate >= monthAgo }
    }
    
    var monthTotalSeconds: Int {
        monthSessions.reduce(0) { $0 + Int($1.duration) }
    }
    
    var monthTotalFormatted: String {
        formatTotalTime(monthTotalSeconds)
    }
    
    /// Horas por matéria no mês
    var subjectStats: [(subject: String, hours: Double)] {
        var dict: [String: Double] = [:]
        for session in monthSessions {
            dict[session.subject, default: 0] += session.duration / 3600.0
        }
        return dict.map { (subject: $0.key, hours: $0.value) }
            .sorted { $0.hours > $1.hours }
    }
    
    /// Média diária no mês (horas)
    var dailyAverageHours: Double {
        guard !monthSessions.isEmpty else { return 0 }
        let calendar = Calendar.current
        let uniqueDays = Set(monthSessions.map { calendar.startOfDay(for: $0.startDate) })
        let totalHours = Double(monthTotalSeconds) / 3600.0
        return totalHours / Double(uniqueDays.count)
    }
    
    // MARK: - Inicialização
    
    init() {
        loadSessions()
    }
    
    // MARK: - Ações do Timer
    
    func startStudying() {
        let subject = selectedSubject == "Outro" ? customSubject : selectedSubject
        guard !subject.trimmingCharacters(in: .whitespaces).isEmpty else { return }
        
        let session = StudySession(subject: subject)
        activeSession = session
        elapsedSeconds = 0
        
        timer = Timer.scheduledTimer(withTimeInterval: 1, repeats: true) { [weak self] _ in
            Task { @MainActor in
                self?.elapsedSeconds += 1
            }
        }
    }
    
    func stopStudying() {
        timer?.invalidate()
        timer = nil
        
        guard var session = activeSession else { return }
        session.endDate = Date()
        session.notes = sessionNotes
        
        sessions.insert(session, at: 0)
        saveSessions()
        
        activeSession = nil
        elapsedSeconds = 0
        sessionNotes = ""
    }
    
    func deleteSession(_ session: StudySession) {
        sessions.removeAll { $0.id == session.id }
        saveSessions()
    }
    
    func deleteSessions(at offsets: IndexSet) {
        sessions.remove(atOffsets: offsets)
        saveSessions()
    }
    
    // MARK: - Persistência (UserDefaults)
    
    private let storageKey = "study_sessions"
    
    private func saveSessions() {
        if let data = try? JSONEncoder().encode(sessions) {
            UserDefaults.standard.set(data, forKey: storageKey)
        }
    }
    
    private func loadSessions() {
        guard let data = UserDefaults.standard.data(forKey: storageKey),
              let decoded = try? JSONDecoder().decode([StudySession].self, from: data) else { return }
        sessions = decoded
    }
    
    // MARK: - Helpers
    
    private func formatTotalTime(_ totalSeconds: Int) -> String {
        let hours = totalSeconds / 3600
        let minutes = (totalSeconds % 3600) / 60
        if hours > 0 {
            return String(format: "%dh %02dmin", hours, minutes)
        } else {
            return String(format: "%dmin", minutes)
        }
    }
}
