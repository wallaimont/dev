import SwiftUI

/// Tela principal com o cronômetro de estudo
struct TimerView: View {
    @ObservedObject var viewModel: StudyViewModel
    
    var body: some View {
        ScrollView {
            VStack(spacing: 24) {
                
                // MARK: - Resumo do Dia
                todaySummaryCard
                
                // MARK: - Timer Display
                timerDisplay
                
                // MARK: - Seleção de Matéria
                if !viewModel.isStudying {
                    subjectPicker
                }
                
                // MARK: - Botão Iniciar/Parar
                actionButton
                
                // MARK: - Campo de Notas (durante estudo)
                if viewModel.isStudying {
                    notesField
                }
            }
            .padding()
        }
        .navigationTitle("Estudar")
    }
    
    // MARK: - Componentes
    
    private var todaySummaryCard: some View {
        VStack(spacing: 8) {
            HStack {
                Image(systemName: "clock.fill")
                    .foregroundColor(.blue)
                Text("Hoje")
                    .font(.headline)
                Spacer()
            }
            
            HStack {
                VStack(alignment: .leading) {
                    Text(viewModel.todayTotalFormatted)
                        .font(.title2)
                        .fontWeight(.bold)
                        .foregroundColor(.blue)
                    Text("\(viewModel.todaySessions.count) sessões")
                        .font(.caption)
                        .foregroundColor(.secondary)
                }
                Spacer()
            }
        }
        .padding()
        .background(Color(.systemGray6))
        .cornerRadius(16)
    }
    
    private var timerDisplay: some View {
        VStack(spacing: 12) {
            Text(viewModel.elapsedFormatted)
                .font(.system(size: 64, weight: .thin, design: .monospaced))
                .foregroundColor(viewModel.isStudying ? .primary : .secondary)
            
            if viewModel.isStudying, let session = viewModel.activeSession {
                HStack {
                    Image(systemName: "book.fill")
                        .foregroundColor(.green)
                    Text(session.subject)
                        .font(.title3)
                        .fontWeight(.medium)
                }
            }
        }
        .padding(.vertical, 20)
    }
    
    private var subjectPicker: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("Matéria")
                .font(.headline)
            
            LazyVGrid(columns: [
                GridItem(.adaptive(minimum: 100), spacing: 8)
            ], spacing: 8) {
                ForEach(DefaultSubjects.all, id: \.self) { subject in
                    Button {
                        viewModel.selectedSubject = subject
                    } label: {
                        Text(subject)
                            .font(.subheadline)
                            .padding(.horizontal, 12)
                            .padding(.vertical, 8)
                            .frame(maxWidth: .infinity)
                            .background(
                                viewModel.selectedSubject == subject
                                ? Color.blue
                                : Color(.systemGray5)
                            )
                            .foregroundColor(
                                viewModel.selectedSubject == subject
                                ? .white
                                : .primary
                            )
                            .cornerRadius(10)
                    }
                }
            }
            
            if viewModel.selectedSubject == "Outro" {
                TextField("Nome da matéria", text: $viewModel.customSubject)
                    .textFieldStyle(.roundedBorder)
            }
        }
    }
    
    private var actionButton: some View {
        Button {
            if viewModel.isStudying {
                viewModel.stopStudying()
            } else {
                viewModel.startStudying()
            }
        } label: {
            HStack(spacing: 8) {
                Image(systemName: viewModel.isStudying ? "stop.fill" : "play.fill")
                Text(viewModel.isStudying ? "Parar Estudo" : "Começar a Estudar")
                    .fontWeight(.semibold)
            }
            .frame(maxWidth: .infinity)
            .padding()
            .background(viewModel.isStudying ? Color.red : Color.green)
            .foregroundColor(.white)
            .cornerRadius(16)
            .font(.title3)
        }
    }
    
    private var notesField: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text("Anotações da sessão")
                .font(.headline)
            
            TextEditor(text: $viewModel.sessionNotes)
                .frame(minHeight: 80)
                .padding(8)
                .background(Color(.systemGray6))
                .cornerRadius(12)
                .overlay(
                    RoundedRectangle(cornerRadius: 12)
                        .stroke(Color(.systemGray4), lineWidth: 1)
                )
        }
    }
}

#Preview {
    NavigationStack {
        TimerView(viewModel: StudyViewModel())
    }
}
