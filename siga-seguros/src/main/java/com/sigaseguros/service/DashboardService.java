package com.sigaseguros.service;

import com.sigaseguros.dto.*;
import com.sigaseguros.enums.*;
import com.sigaseguros.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ClienteRepository clienteRepository;
    private final PropostaRepository propostaRepository;
    private final ApoliceRepository apoliceRepository;
    private final SinistroRepository sinistroRepository;
    private final RenovacaoRepository renovacaoRepository;
    private final LancamentoFinanceiroRepository lancamentoRepository;
    private final ComissaoRepository comissaoRepository;
    private final PropostaService propostaService;
    private final ApoliceService apoliceService;
    private final SinistroService sinistroService;

    @Transactional(readOnly = true)
    public DashboardDTO getDashboard() {
        int anoAtual = Year.now().getValue();
        LocalDate hoje = LocalDate.now();

        DashboardDTO dashboard = new DashboardDTO();

        // Cards
        dashboard.setTotalClientes(clienteRepository.countByActiveTrue());
        dashboard.setPropostasEmAberto(propostaRepository.countByStatusAndActiveTrue(StatusProposta.EM_ANALISE)
                + propostaRepository.countByStatusAndActiveTrue(StatusProposta.COTADO));
        dashboard.setPropostasAprovadas(propostaRepository.countByStatusAndActiveTrue(StatusProposta.APROVADO));
        dashboard.setApolicesAtivas(apoliceRepository.countByStatusAndActiveTrue(StatusApolice.ATIVA));
        dashboard.setApolicesVencendo30Dias(apoliceRepository.findVencendoEntre(hoje, hoje.plusDays(30)).size());
        dashboard.setSinistrosAbertos(sinistroRepository.countByStatusAndActiveTrue(StatusSinistro.ABERTO)
                + sinistroRepository.countByStatusAndActiveTrue(StatusSinistro.EM_ANALISE));
        dashboard.setContasReceberPendentes(lancamentoRepository.sumByTipoAndStatus(TipoLancamento.RECEITA, StatusFinanceiro.PENDENTE));
        dashboard.setContasPagarPendentes(lancamentoRepository.sumByTipoAndStatus(TipoLancamento.DESPESA, StatusFinanceiro.PENDENTE));
        dashboard.setComissoesMes(comissaoRepository.sumByStatus(StatusComissao.PENDENTE));

        // Graficos
        dashboard.setPropostasPorStatus(mapFromList(propostaRepository.countByStatus()));
        dashboard.setApolicesPorSeguradora(mapFromList(apoliceRepository.countBySeguradora()));
        dashboard.setSinistrosPorStatus(mapFromList(sinistroRepository.countByStatus()));
        dashboard.setRenovacoesPorStatus(mapFromList(renovacaoRepository.countByStatus()));

        // Faturamento por mes
        Map<String, BigDecimal> faturamento = new LinkedHashMap<>();
        String[] meses = {"Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"};
        for (int i = 0; i < 12; i++) faturamento.put(meses[i], BigDecimal.ZERO);
        for (Object[] row : lancamentoRepository.faturamentoMensal(anoAtual)) {
            int mes = ((Number) row[0]).intValue();
            BigDecimal valor = (BigDecimal) row[1];
            if (mes >= 1 && mes <= 12) faturamento.put(meses[mes - 1], valor);
        }
        dashboard.setFaturamentoPorMes(faturamento);

        Map<String, BigDecimal> comissoes = new LinkedHashMap<>();
        for (int i = 0; i < 12; i++) comissoes.put(meses[i], BigDecimal.ZERO);
        for (Object[] row : comissaoRepository.comissoesMensal(anoAtual)) {
            int mes = ((Number) row[0]).intValue();
            BigDecimal valor = (BigDecimal) row[1];
            if (mes >= 1 && mes <= 12) comissoes.put(meses[mes - 1], valor);
        }
        dashboard.setComissoesPorMes(comissoes);

        // Listagens rapidas
        dashboard.setUltimasPropostas(propostaService.ultimasPropostas());
        dashboard.setApolicesVencendo(apoliceService.vencendoEm30Dias());
        dashboard.setSinistrosEmAberto(sinistroService.sinistrosEmAberto());

        return dashboard;
    }

    private Map<String, Long> mapFromList(List<Object[]> rows) {
        Map<String, Long> map = new LinkedHashMap<>();
        for (Object[] row : rows) {
            map.put(String.valueOf(row[0]), ((Number) row[1]).longValue());
        }
        return map;
    }
}
