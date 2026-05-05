import 'dart:convert';

/// Representa uma sessão de estudo do aluno.
class StudySession {
  final String id;
  String subject;
  final DateTime startDate;
  DateTime? endDate;
  String notes;

  StudySession({
    required this.id,
    required this.subject,
    required this.startDate,
    this.endDate,
    this.notes = '',
  });

  /// Duração em segundos.
  int get durationSeconds {
    final end = endDate ?? DateTime.now();
    return end.difference(startDate).inSeconds;
  }

  /// Duração formatada como "Xh Ym" ou "Ym Zs".
  String get formattedDuration {
    final total = durationSeconds;
    final hours = total ~/ 3600;
    final minutes = (total % 3600) ~/ 60;
    final seconds = total % 60;

    if (hours > 0) {
      return '${hours}h ${minutes.toString().padLeft(2, '0')}min';
    } else if (minutes > 0) {
      return '${minutes}min ${seconds.toString().padLeft(2, '0')}s';
    } else {
      return '${seconds}s';
    }
  }

  bool get isActive => endDate == null;

  Map<String, dynamic> toJson() => {
        'id': id,
        'subject': subject,
        'startDate': startDate.toIso8601String(),
        'endDate': endDate?.toIso8601String(),
        'notes': notes,
      };

  factory StudySession.fromJson(Map<String, dynamic> json) => StudySession(
        id: json['id'] as String,
        subject: json['subject'] as String,
        startDate: DateTime.parse(json['startDate'] as String),
        endDate: json['endDate'] != null
            ? DateTime.parse(json['endDate'] as String)
            : null,
        notes: json['notes'] as String? ?? '',
      );

  static String encodeList(List<StudySession> sessions) =>
      jsonEncode(sessions.map((s) => s.toJson()).toList());

  static List<StudySession> decodeList(String jsonStr) {
    final list = jsonDecode(jsonStr) as List<dynamic>;
    return list
        .map((e) => StudySession.fromJson(e as Map<String, dynamic>))
        .toList();
  }
}

/// Matérias pré-definidas comuns.
class DefaultSubjects {
  static const List<String> all = [
    'Matemática',
    'Português',
    'Física',
    'Química',
    'Biologia',
    'História',
    'Geografia',
    'Inglês',
    'Redação',
    'Filosofia',
    'Sociologia',
    'Programação',
    'Outro',
  ];
}
