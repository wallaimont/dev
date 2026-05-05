package com.orionerp.modules.administration.service;

import com.orionerp.common.PageResponse;
import com.orionerp.modules.administration.dto.PermissaoResponse;
import com.orionerp.modules.administration.repository.PermissaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PermissaoService {

    private final PermissaoRepository permissaoRepository;

    @Transactional(readOnly = true)
    public PageResponse<PermissaoResponse> list(String modulo, String term, int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "modulo", "recurso", "acao"));
        var result = permissaoRepository.findAll(AdministrationSpecifications.permissaoFilter(modulo, term), pageable)
                .map(p -> new PermissaoResponse(
                        p.getId(),
                        p.getUuid(),
                        p.getModulo(),
                        p.getRecurso(),
                        p.getAcao(),
                        p.getDescricao()
                ));
        return PageResponse.from(result);
    }
}
