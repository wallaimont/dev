import 'dart:async';
import 'package:flutter/foundation.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../models/study_session.dart';

/// Provider principal — gerencia sessões de estudo, timer e persistência.
class StudyProvider extends ChangeNotifier {
  List<StudySession> _sessions = [];
  StudySession? _activeSession;
  int _elapsedSeconds = 0;
  String _selectedSubject = DefaultSubjects.all[0];
  String _customSubject = '';
  String _sessionNotes = '';
  Timer? _timer;

  // --------------- Getters ---------------

  List<StudySession> get sessions => _sessions;
  StudySession? get activeSession => _activeSession;
  int get elapsedSeconds => _elapsedSeconds;
  bool get isStudying => _activeSession != null;

  String get selectedSubject => _selectedSubject;
  set selectedSubject(String value) {
    _selectedSubject = value;
    notifyListeners();
  }

  String get customSubject => _customSubject;
  set customSubject(String value) {
    _customSubject = value;
    notifyListeners();
  }

  String get sessionNotes => _sessionNotes;
  set sessionNotes(String value) {
    _sessionNotes = value;
    notifyListeners();
  }

  String get elapsedFormatted {
    final h = _elapsedSeconds ~/ 3600;
    final m = (_elapsedSeconds % 3600) ~/ 60;
    final s = _elapsedSeconds % 60;
    return '${h.toString().padLeft(2, '0')}:'
        '${m.toString().padLeft(2, '0')}:'
        '${s.toString().padLeft(2, '0')}';
  }

  // --------------- Filtros de período ---------------

  List<StudySession> get todaySessions {
    final now = DateTime.now();
    final startOfDay = DateTime(now.year, now.month, now.day);
    return _sessions.where((s) => s.startDate.isAfter(startOfDay)).toList();
  }

  int get todayTotalSeconds =>
      todaySessions.fold(0, (sum, s) => sum + s.durationSeconds);

  String get todayTotalFormatted => _formatTotal(todayTotalSeconds);

  List<StudySession> get weekSessions {
    final weekAgo = DateTime.now().subtract(const Duration(days: 7));
    return _sessions.where((s) => s.startDate.isAfter(weekAgo)).toList();
  }

  int get weekTotalSeconds =>
      weekSessions.fold(0, (sum, s) => sum + s.durationSeconds);

  String get weekTotalFormatted => _formatTotal(weekTotalSeconds);

  List<StudySession> get monthSessions {
    final now = DateTime.now();
    final monthAgo = DateTime(now.year, now.month - 1, now.day);
    return _sessions.where((s) => s.startDate.isAfter(monthAgo)).toList();
  }

  int get monthTotalSeconds =>
      monthSessions.fold(0, (sum, s) => sum + s.durationSeconds);

  String get monthTotalFormatted => _formatTotal(monthTotalSeconds);

  /// Horas por matéria no mês, ordenado desc.
  List<MapEntry<String, double>> get subjectStats {
    final map = <String, double>{};
    for (final s in monthSessions) {
      map[s.subject] = (map[s.subject] ?? 0) + s.durationSeconds / 3600.0;
    }
    final entries = map.entries.toList()
      ..sort((a, b) => b.value.compareTo(a.value));
    return entries;
  }

  /// Média diária (horas) nos últimos 30 dias.
  double get dailyAverageHours {
    if (monthSessions.isEmpty) return 0;
    final uniqueDays = monthSessions
        .map((s) =>
            DateTime(s.startDate.year, s.startDate.month, s.startDate.day))
        .toSet();
    return (monthTotalSeconds / 3600.0) / uniqueDays.length;
  }

  /// Minutos por dia nos últimos 7 dias.
  List<MapEntry<String, int>> get last7DaysData {
    final now = DateTime.now();
    final weekdays = ['Dom', 'Seg', 'Ter', 'Qua', 'Qui', 'Sex', 'Sáb'];
    final result = <MapEntry<String, int>>[];

    for (var i = 6; i >= 0; i--) {
      final day = now.subtract(Duration(days: i));
      final start = DateTime(day.year, day.month, day.day);
      final end = start.add(const Duration(days: 1));
      final mins = _sessions
              .where((s) => s.startDate.isAfter(start) && s.startDate.isBefore(end))
              .fold(0, (sum, s) => sum + s.durationSeconds) ~/
          60;
      result.add(MapEntry(weekdays[day.weekday % 7], mins));
    }
    return result;
  }

  // --------------- Ações ---------------

  StudyProvider() {
    _loadSessions();
  }

  void startStudying() {
    final subject =
        _selectedSubject == 'Outro' ? _customSubject : _selectedSubject;
    if (subject.trim().isEmpty) return;

    _activeSession = StudySession(
      id: DateTime.now().millisecondsSinceEpoch.toString(),
      subject: subject,
      startDate: DateTime.now(),
    );
    _elapsedSeconds = 0;

    _timer = Timer.periodic(const Duration(seconds: 1), (_) {
      _elapsedSeconds++;
      notifyListeners();
    });
    notifyListeners();
  }

  void stopStudying() {
    _timer?.cancel();
    _timer = null;

    if (_activeSession != null) {
      _activeSession!.endDate = DateTime.now();
      _activeSession!.notes = _sessionNotes;
      _sessions.insert(0, _activeSession!);
      _saveSessions();
    }

    _activeSession = null;
    _elapsedSeconds = 0;
    _sessionNotes = '';
    notifyListeners();
  }

  void deleteSession(String id) {
    _sessions.removeWhere((s) => s.id == id);
    _saveSessions();
    notifyListeners();
  }

  // --------------- Persistência ---------------

  static const _storageKey = 'study_sessions';

  Future<void> _saveSessions() async {
    final prefs = await SharedPreferences.getInstance();
    prefs.setString(_storageKey, StudySession.encodeList(_sessions));
  }

  Future<void> _loadSessions() async {
    final prefs = await SharedPreferences.getInstance();
    final data = prefs.getString(_storageKey);
    if (data != null) {
      _sessions = StudySession.decodeList(data);
      notifyListeners();
    }
  }

  // --------------- Helpers ---------------

  String _formatTotal(int totalSeconds) {
    final h = totalSeconds ~/ 3600;
    final m = (totalSeconds % 3600) ~/ 60;
    if (h > 0) return '${h}h ${m.toString().padLeft(2, '0')}min';
    return '${m}min';
  }

  @override
  void dispose() {
    _timer?.cancel();
    super.dispose();
  }
}
