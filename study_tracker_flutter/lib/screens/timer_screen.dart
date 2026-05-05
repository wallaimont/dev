import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/study_provider.dart';
import '../models/study_session.dart';

/// Tela principal com cronômetro de estudo.
class TimerScreen extends StatelessWidget {
  const TimerScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Consumer<StudyProvider>(
      builder: (context, provider, _) {
        return SingleChildScrollView(
          padding: const EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              _buildTodaySummary(provider),
              const SizedBox(height: 24),
              _buildTimerDisplay(provider),
              const SizedBox(height: 24),
              if (!provider.isStudying) _buildSubjectPicker(provider),
              const SizedBox(height: 24),
              _buildActionButton(provider),
              if (provider.isStudying) ...[
                const SizedBox(height: 20),
                _buildNotesField(provider),
              ],
            ],
          ),
        );
      },
    );
  }

  Widget _buildTodaySummary(StudyProvider provider) {
    return Card(
      elevation: 0,
      color: Colors.blue.shade50,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Icon(Icons.access_time_filled, color: Colors.blue.shade700),
                const SizedBox(width: 8),
                Text('Hoje',
                    style: TextStyle(
                        fontWeight: FontWeight.bold,
                        fontSize: 16,
                        color: Colors.blue.shade700)),
              ],
            ),
            const SizedBox(height: 8),
            Text(
              provider.todayTotalFormatted,
              style: TextStyle(
                  fontSize: 24,
                  fontWeight: FontWeight.bold,
                  color: Colors.blue.shade700),
            ),
            Text(
              '${provider.todaySessions.length} sessões',
              style: TextStyle(fontSize: 13, color: Colors.blue.shade400),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildTimerDisplay(StudyProvider provider) {
    return Column(
      children: [
        Text(
          provider.elapsedFormatted,
          style: TextStyle(
            fontSize: 56,
            fontWeight: FontWeight.w200,
            fontFamily: 'monospace',
            color: provider.isStudying ? Colors.black87 : Colors.grey,
          ),
        ),
        if (provider.isStudying && provider.activeSession != null) ...[
          const SizedBox(height: 8),
          Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              const Icon(Icons.book, color: Colors.green, size: 20),
              const SizedBox(width: 6),
              Text(
                provider.activeSession!.subject,
                style: const TextStyle(
                    fontSize: 18, fontWeight: FontWeight.w500),
              ),
            ],
          ),
        ],
      ],
    );
  }

  Widget _buildSubjectPicker(StudyProvider provider) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text('Matéria',
            style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
        const SizedBox(height: 12),
        Wrap(
          spacing: 8,
          runSpacing: 8,
          children: DefaultSubjects.all.map((subject) {
            final isSelected = provider.selectedSubject == subject;
            return ChoiceChip(
              label: Text(subject),
              selected: isSelected,
              selectedColor: Colors.blue,
              labelStyle: TextStyle(
                color: isSelected ? Colors.white : Colors.black87,
                fontSize: 13,
              ),
              onSelected: (_) => provider.selectedSubject = subject,
            );
          }).toList(),
        ),
        if (provider.selectedSubject == 'Outro') ...[
          const SizedBox(height: 12),
          TextField(
            decoration: const InputDecoration(
              labelText: 'Nome da matéria',
              border: OutlineInputBorder(),
            ),
            onChanged: (value) => provider.customSubject = value,
          ),
        ],
      ],
    );
  }

  Widget _buildActionButton(StudyProvider provider) {
    final isStudying = provider.isStudying;
    return SizedBox(
      height: 56,
      child: ElevatedButton.icon(
        onPressed: () {
          if (isStudying) {
            provider.stopStudying();
          } else {
            provider.startStudying();
          }
        },
        icon: Icon(isStudying ? Icons.stop : Icons.play_arrow),
        label: Text(
          isStudying ? 'Parar Estudo' : 'Começar a Estudar',
          style: const TextStyle(fontSize: 18, fontWeight: FontWeight.w600),
        ),
        style: ElevatedButton.styleFrom(
          backgroundColor: isStudying ? Colors.red : Colors.green,
          foregroundColor: Colors.white,
          shape:
              RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
        ),
      ),
    );
  }

  Widget _buildNotesField(StudyProvider provider) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text('Anotações da sessão',
            style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
        const SizedBox(height: 8),
        TextField(
          maxLines: 4,
          decoration: InputDecoration(
            hintText: 'Escreva suas anotações aqui...',
            border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
            filled: true,
            fillColor: Colors.grey.shade100,
          ),
          onChanged: (value) => provider.sessionNotes = value,
        ),
      ],
    );
  }
}
