import Foundation

/// Representa uma sessão de estudo do aluno
struct StudySession: Identifiable, Codable {
    let id: UUID
    var subject: String
    var startDate: Date
    var endDate: Date?
    var notes: String
    
    /// Duração em segundos
    var duration: TimeInterval {
        let end = endDate ?? Date()
        return end.timeIntervalSince(startDate)
    }
    
    /// Duração formatada como "Xh Ym"
    var formattedDuration: String {
        let totalSeconds = Int(duration)
        let hours = totalSeconds / 3600
        let minutes = (totalSeconds % 3600) / 60
        let seconds = totalSeconds % 60
        
        if hours > 0 {
            return String(format: "%dh %02dmin", hours, minutes)
        } else if minutes > 0 {
            return String(format: "%dmin %02ds", minutes, seconds)
        } else {
            return String(format: "%ds", seconds)
        }
    }
    
    /// Data formatada para exibição
    var formattedDate: String {
        let formatter = DateFormatter()
        formatter.locale = Locale(identifier: "pt_BR")
        formatter.dateStyle = .medium
        formatter.timeStyle = .short
        return formatter.string(from: startDate)
    }
    
    var isActive: Bool {
        endDate == nil
    }
    
    init(id: UUID = UUID(), subject: String, startDate: Date = Date(), endDate: Date? = nil, notes: String = "") {
        self.id = id
        self.subject = subject
        self.startDate = startDate
        self.endDate = endDate
        self.notes = notes
    }
}

/// Matérias pré-definidas comuns
enum DefaultSubjects {
    static let all: [String] = [
        "Matemática",
        "Português",
        "Física",
        "Química",
        "Biologia",
        "História",
        "Geografia",
        "Inglês",
        "Redação",
        "Filosofia",
        "Sociologia",
        "Programação",
        "Outro"
    ]
}
