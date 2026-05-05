import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';
import '../providers/study_provider.dart';
import '../models/study_session.dart';

/// Tela de histórico de sessões de estudo.
class HistoryScreen extends StatefulWidget {
  const HistoryScreen({super.key});

  @override
  State<HistoryScreen> createState() => _HistoryScreenState();
}

class _HistoryScreenState extends State<HistoryScreen> {
  String? _filterSubject;

  @override
  Widget build(BuildContext context) {
    return Consumer<StudyProvider>(
      builder: (context, provider, _) {
        if (provider.sessions.isEmpty) {
          return _buildEmptyState();
        }
        return _buildSessionsList(provider);
      },
    );
  }

  Widget _buildEmptyState() {
    return Center(
      child: Padding(
        padding: const EdgeInsets.all(32),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.menu_book, size: 64, color: Colors.grey.shade400),
            const SizedBox(height: 16),
            Text('Nenhuma sessão registrada',
                style: TextStyle(fontSize: 18, color: Colors.grey.shade600)),
            const SizedBox(height: 8),
            Text('Comece a estudar para ver seu histórico aqui!',
                textAlign: TextAlign.center,
                style: TextStyle(fontSize: 14, color: Colors.grey.shade500)),
          ],
        ),
      ),
    );
  }

  Widget _buildSessionsList(StudyProvider provider) {
    final filtered = _filterSubject != null
        ? provider.sessions.where((s) => s.subject == _filterSubject).toList()
        : provider.sessions;

    // Agrupar por dia
    final dateFormat = DateFormat("EEEE, d 'de' MMMM", 'pt_BR');
    final grouped = <String, List<StudySession>>{};
    for (final s in filtered) {
      final key = dateFormat.format(s.startDate);
      grouped.putIfAbsent(key, () => []).add(s);
    }
    final groups = grouped.entries.toList();

    final uniqueSubjects = provider.sessions.map((s) => s.subject).toSet().toList()..sort();

    return Column(
      children: [
        // Filtro por matéria
        SizedBox(
          height: 48,
          child: ListView(
            scrollDirection: Axis.horizontal,
            padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
            children: [
              _buildFilterChip('Todas', _filterSubject == null, () {
                setState(() => _filterSubject = null);
              }),
              ...uniqueSubjects.map((subject) => _buildFilterChip(
                    subject,
                    _filterSubject == subject,
                    () => setState(() => _filterSubject = subject),
                  )),
            ],
          ),
        ),
        // Lista agrupada
        Expanded(
          child: ListView.builder(
            padding: const EdgeInsets.symmetric(horizontal: 12),
            itemCount: groups.length,
            itemBuilder: (context, index) {
              final group = groups[index];
              return Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Padding(
                    padding: const EdgeInsets.only(top: 16, bottom: 8, left: 4),
                    child: Text(
                      group.key,
                      style: TextStyle(
                          fontWeight: FontWeight.bold,
                          fontSize: 14,
                          color: Colors.grey.shade600),
                    ),
                  ),
                  ...group.value.map((session) => _buildSessionCard(session, provider)),
                ],
              );
            },
          ),
        ),
      ],
    );
  }

  Widget _buildFilterChip(String label, bool selected, VoidCallback onTap) {
    return Padding(
      padding: const EdgeInsets.only(right: 8),
      child: GestureDetector(
        onTap: onTap,
        child: Chip(
          label: Text(label,
              style: TextStyle(
                  color: selected ? Colors.white : Colors.black87,
                  fontSize: 12)),
          backgroundColor: selected ? Colors.blue : Colors.grey.shade200,
        ),
      ),
    );
  }

  Widget _buildSessionCard(StudySession session, StudyProvider provider) {
    final timeFormat = DateFormat('HH:mm', 'pt_BR');

    return Dismissible(
      key: Key(session.id),
      direction: DismissDirection.endToStart,
      background: Container(
        alignment: Alignment.centerRight,
        padding: const EdgeInsets.only(right: 20),
        decoration: BoxDecoration(
          color: Colors.red,
          borderRadius: BorderRadius.circular(12),
        ),
        child: const Icon(Icons.delete, color: Colors.white),
      ),
      onDismissed: (_) => provider.deleteSession(session.id),
      child: Card(
        elevation: 0,
        color: Colors.grey.shade50,
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
        margin: const EdgeInsets.only(bottom: 8),
        child: Padding(
          padding: const EdgeInsets.all(14),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                children: [
                  Icon(Icons.book, color: Colors.blue.shade600, size: 20),
                  const SizedBox(width: 8),
                  Expanded(
                    child: Text(session.subject,
                        style: const TextStyle(
                            fontWeight: FontWeight.bold, fontSize: 15)),
                  ),
                  Text(
                    session.formattedDuration,
                    style: TextStyle(
                        fontWeight: FontWeight.w600,
                        color: Colors.blue.shade700,
                        fontSize: 14),
                  ),
                ],
              ),
              const SizedBox(height: 6),
              Row(
                children: [
                  Icon(Icons.schedule, size: 14, color: Colors.grey.shade500),
                  const SizedBox(width: 4),
                  Text(
                    timeFormat.format(session.startDate),
                    style: TextStyle(fontSize: 12, color: Colors.grey.shade500),
                  ),
                  if (session.endDate != null) ...[
                    Text(' → ',
                        style: TextStyle(
                            fontSize: 12, color: Colors.grey.shade500)),
                    Text(
                      timeFormat.format(session.endDate!),
                      style:
                          TextStyle(fontSize: 12, color: Colors.grey.shade500),
                    ),
                  ],
                ],
              ),
              if (session.notes.isNotEmpty) ...[
                const SizedBox(height: 6),
                Text(
                  session.notes,
                  maxLines: 2,
                  overflow: TextOverflow.ellipsis,
                  style: TextStyle(fontSize: 12, color: Colors.grey.shade500),
                ),
              ],
            ],
          ),
        ),
      ),
    );
  }
}
