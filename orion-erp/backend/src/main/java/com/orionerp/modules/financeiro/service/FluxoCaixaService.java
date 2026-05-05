package com.orionerp.modules.financeiro.service;

import com.orionerp.common.PageResponse;
import com.orionerp.modules.financeiro.dto.FluxoCaixaResponse;
import com.orionerp.modules.financeiro.repository.FluxoCaixaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class FluxoCaixaService {

    private final FluxoCaixaRepository fluxoCaixaRepository;

    @Transactional(readOnly = true)
    public PageResponse<FluxoCaixaResponse> list(Long empresaId, Long filialId,
                                                 Long contaBancariaId, String tipo,
                                                 LocalDate dataInicio, LocalDate dataFim,
                                                 int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataLancamento"));
        var spec = FinanceiroSpecifications.fluxoCaixaFilter(empresaId, filialId, contaBancariaId, tipo, dataInicio, dataFim);
        var result = fluxoCaixaRepository.findAll(spec, pageable)
                .map(fc -> new FluxoCaixaResponse(
                        fc.getId(), fc.getUuid(), fc.getEmpresaId(), fc.getFilialId(),
                        fc.getContaBancaria().getId(), fc.getTipo(), fc.getValor(),
                        fc.getDataLancamento(), fc.getDescricao(), fc.getTituloId(), fc.getBaixaId()));
        return PageResponse.from(result);
    }
}
