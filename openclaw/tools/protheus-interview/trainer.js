#!/usr/bin/env node

import readline from 'node:readline/promises';
import { stdin as input, stdout as output } from 'node:process';

const questionBank = [
  { theme: 'ADVPL', q: 'Explique a diferença entre UDF e ponto de entrada no Protheus.' },
  { theme: 'ADVPL', q: 'Quando você usaria FWMsExcelXlsx em vez de geração de arquivo texto?' },
  { theme: 'Protheus', q: 'Como funciona a atualização de dicionário (SXs) e quais cuidados você toma?' },
  { theme: 'Protheus', q: 'Descreva seu processo para subir customizações entre ambientes (dev/hml/prod).' },
  { theme: 'Banco', q: 'Quais cuidados de performance você toma com consultas em tabelas grandes do Protheus?' },
  { theme: 'SQL', q: 'Explique um caso em que índice melhorou uma rotina de relatório.' },
  { theme: 'Integrações', q: 'Como você estruturaria uma integração REST com o Protheus para evitar retrabalho?' },
  { theme: 'Boas práticas', q: 'Como você testa regressão em rotinas ADVPL sem impactar usuários?' },
];

function clampScore(value) {
  const n = Number(value);
  if (Number.isNaN(n)) return 0;
  return Math.max(0, Math.min(5, n));
}

function avg(numbers) {
  if (!numbers.length) return 0;
  return numbers.reduce((a, b) => a + b, 0) / numbers.length;
}

function pickQuestions(total) {
  const copy = [...questionBank];
  for (let i = copy.length - 1; i > 0; i -= 1) {
    const j = Math.floor(Math.random() * (i + 1));
    [copy[i], copy[j]] = [copy[j], copy[i]];
  }
  return copy.slice(0, total);
}

function studyPlan(themeAverages) {
  const weak = Object.entries(themeAverages)
    .filter(([, score]) => score < 3)
    .sort((a, b) => a[1] - b[1]);

  if (!weak.length) {
    return ['Continue praticando simulações com tempo cronometrado e revisão de casos reais.'];
  }

  return weak.map(([theme, score]) => {
    const rounded = score.toFixed(1);
    return `Foco em ${theme} (média ${rounded}/5): revisar fundamentos, implementar 1 exercício prático e explicar a solução em voz alta.`;
  });
}

async function run() {
  const rl = readline.createInterface({ input, output });
  try {
    console.log('\n=== Simulador de Entrevista TOTVS Protheus ===\n');
    const totalInput = await rl.question('Quantas perguntas deseja praticar? (1-8): ');
    const requested = Math.max(1, Math.min(8, Number(totalInput) || 5));
    const selected = pickQuestions(requested);

    const results = [];

    for (let i = 0; i < selected.length; i += 1) {
      const item = selected[i];
      console.log(`\n[${i + 1}/${selected.length}] Tema: ${item.theme}`);
      console.log(`Pergunta: ${item.q}`);
      await rl.question('Sua resposta (pressione Enter ao concluir): ');
      const scoreInput = await rl.question('Autoavaliação (0-5): ');
      const score = clampScore(scoreInput);
      results.push({ theme: item.theme, score });
    }

    const scores = results.map((r) => r.score);
    const overall = avg(scores);

    const byTheme = {};
    for (const r of results) {
      byTheme[r.theme] = byTheme[r.theme] || [];
      byTheme[r.theme].push(r.score);
    }

    const themeAverages = Object.fromEntries(
      Object.entries(byTheme).map(([theme, values]) => [theme, avg(values)]),
    );

    console.log('\n=== Resultado ===');
    console.log(`Média geral: ${overall.toFixed(2)}/5`);
    console.log('Média por tema:');
    for (const [theme, value] of Object.entries(themeAverages)) {
      console.log(`- ${theme}: ${value.toFixed(2)}/5`);
    }

    console.log('\nPlano de estudo recomendado:');
    for (const line of studyPlan(themeAverages)) {
      console.log(`- ${line}`);
    }
  } finally {
    rl.close();
  }
}

run();
