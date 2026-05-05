import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/study_provider.dart';

/// Tela de estatísticas de estudo.
class StatsScreen extends StatelessWidget {
  const StatsScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Consumer<StudyProvider>(
      builder: (context, provider, _) {
        return SingleChildScrollView(
          padding: const EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              _buildSummaryCards(provider),
              const SizedBox(height: 20),
              _buildSubjectBreakdown(provider),
              const SizedBox(height: 20),
              _buildWeeklyChart(provider),
            ],
          ),
        );
      },
    );
  }

  // MARK: - Cards de resumo

  Widget _buildSummaryCards(StudyProvider provider) {
    return Column(
      children: [
        Row(
          children: [
            Expanded(
              child: _statCard(
                title: 'Hoje',
                value: provider.todayTotalFormatted,
                subtitle: '${provider.todaySessions.length} sessões',
                icon: Icons.wb_sunny,
                color: Colors.orange,
              ),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: _statCard(
                title: 'Esta Semana',
                value: provider.weekTotalFormatted,
                subtitle: '${provider.weekSessions.length} sessões',
                icon: Icons.calendar_today,
                color: Colors.blue,
              ),
            ),
          ],
        ),
        const SizedBox(height: 12),
        Row(
          children: [
            Expanded(
              child: _statCard(
                title: 'Este Mês',
                value: provider.monthTotalFormatted,
                subtitle: '${provider.monthSessions.length} sessões',
                icon: Icons.date_range,
                color: Colors.purple,
              ),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: _statCard(
                title: 'Média/Dia',
                value: '${provider.dailyAverageHours.toStringAsFixed(1)}h',
                subtitle: 'últimos 30 dias',
                icon: Icons.trending_up,
                color: Colors.green,
              ),
            ),
          ],
        ),
      ],
    );
  }

  Widget _statCard({
    required String title,
    required String value,
    required String subtitle,
    required IconData icon,
    required Color color,
  }) {
    return Card(
      elevation: 0,
      color: color.withOpacity(0.08),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      child: Padding(
        padding: const EdgeInsets.all(14),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Icon(icon, color: color, size: 18),
                const SizedBox(width: 6),
                Text(title,
                    style: TextStyle(fontSize: 12, color: Colors.grey.shade600)),
              ],
            ),
            const SizedBox(height: 8),
            Text(value,
                style: TextStyle(
                    fontSize: 22, fontWeight: FontWeight.bold, color: color)),
            const SizedBox(height: 2),
            Text(subtitle,
                style: TextStyle(fontSize: 11, color: Colors.grey.shade500)),
          ],
        ),
      ),
    );
  }

  // MARK: - Horas por matéria

  Widget _buildSubjectBreakdown(StudyProvider provider) {
    final stats = provider.subjectStats;
    final maxHours = stats.isNotEmpty ? stats.first.value : 1.0;

    return Card(
      elevation: 0,
      color: Colors.grey.shade50,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Icon(Icons.bar_chart, color: Colors.blue.shade700),
                const SizedBox(width: 8),
                const Text('Horas por Matéria',
                    style:
                        TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
              ],
            ),
            const SizedBox(height: 16),
            if (stats.isEmpty)
              Text('Nenhum dado ainda',
                  style: TextStyle(color: Colors.grey.shade500))
            else
              ...stats.map((stat) {
                final barFraction = stat.value / maxHours;
                return Padding(
                  padding: const EdgeInsets.only(bottom: 10),
                  child: Row(
                    children: [
                      SizedBox(
                        width: 90,
                        child: Text(stat.key,
                            style: const TextStyle(fontSize: 13),
                            overflow: TextOverflow.ellipsis),
                      ),
                      const SizedBox(width: 8),
                      Expanded(
                        child: ClipRRect(
                          borderRadius: BorderRadius.circular(4),
                          child: LinearProgressIndicator(
                            value: barFraction,
                            minHeight: 20,
                            backgroundColor: Colors.grey.shade200,
                            valueColor: AlwaysStoppedAnimation(
                                _colorForSubject(stat.key)),
                          ),
                        ),
                      ),
                      const SizedBox(width: 8),
                      SizedBox(
                        width: 45,
                        child: Text(
                          '${stat.value.toStringAsFixed(1)}h',
                          textAlign: TextAlign.right,
                          style: TextStyle(
                              fontSize: 12, color: Colors.grey.shade600),
                        ),
                      ),
                    ],
                  ),
                );
              }),
          ],
        ),
      ),
    );
  }

  // MARK: - Gráfico semanal

  Widget _buildWeeklyChart(StudyProvider provider) {
    final data = provider.last7DaysData;
    final maxMin = data.fold(0, (m, e) => e.value > m ? e.value : m);
    final maxMinSafe = maxMin == 0 ? 1 : maxMin;

    return Card(
      elevation: 0,
      color: Colors.grey.shade50,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Icon(Icons.insert_chart_outlined, color: Colors.green.shade700),
                const SizedBox(width: 8),
                const Text('Últimos 7 Dias',
                    style:
                        TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
              ],
            ),
            const SizedBox(height: 16),
            SizedBox(
              height: 160,
              child: Row(
                crossAxisAlignment: CrossAxisAlignment.end,
                children: data.asMap().entries.map((entry) {
                  final i = entry.key;
                  final day = entry.value;
                  final isToday = i == data.length - 1;
                  final barHeight =
                      (day.value / maxMinSafe) * 120.0;

                  return Expanded(
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.end,
                      children: [
                        if (day.value > 0)
                          Text('${day.value}m',
                              style: TextStyle(
                                  fontSize: 9, color: Colors.grey.shade500)),
                        const SizedBox(height: 4),
                        Container(
                          height: barHeight < 4 ? 4 : barHeight,
                          margin: const EdgeInsets.symmetric(horizontal: 4),
                          decoration: BoxDecoration(
                            color: isToday
                                ? Colors.green
                                : Colors.blue.withOpacity(0.6),
                            borderRadius: BorderRadius.circular(4),
                          ),
                        ),
                        const SizedBox(height: 6),
                        Text(day.key,
                            style: TextStyle(
                                fontSize: 10, color: Colors.grey.shade600)),
                      ],
                    ),
                  );
                }).toList(),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Color _colorForSubject(String subject) {
    const colors = [
      Colors.blue,
      Colors.green,
      Colors.orange,
      Colors.purple,
      Colors.pink,
      Colors.cyan,
      Colors.indigo,
      Colors.teal,
      Colors.amber,
      Colors.deepOrange,
    ];
    return colors[subject.hashCode.abs() % colors.length];
  }
}
