package com.orionerp.modules.fiscal.service;

import com.orionerp.common.PageResponse;
import com.orionerp.exception.ResourceNotFoundException;
import com.orionerp.modules.fiscal.domain.Cfop;
import com.orionerp.modules.fiscal.domain.Cst;
import com.orionerp.modules.fiscal.domain.Ncm;
import com.orionerp.modules.fiscal.domain.RegraFiscal;
import com.orionerp.modules.fiscal.dto.RegraFiscalRequest;
import com.orionerp.modules.fiscal.dto.RegraFiscalResponse;
import com.orionerp.modules.fiscal.repository.CfopRepository;
import com.orionerp.modules.fiscal.repository.CstRepository;
import com.orionerp.modules.fiscal.repository.FiscalSpecifications;
import com.orionerp.modules.fiscal.repository.NcmRepository;
import com.orionerp.modules.fiscal.repository.RegraFiscalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegraFiscalService {

    private final RegraFiscalRepository regraFiscalRepository;
    private final NcmRepository ncmRepository;
    private final CfopRepository cfopRepository;
    private final CstRepository cstRepository;

    @Transactional(readOnly = true)
    public PageResponse<RegraFiscalResponse> list(Long empresaId, String ufOrigem, String ufDestino,
                                                  Long ncmId, Pageable pageable) {
        var spec = FiscalSpecifications.regraFiscalFilter(empresaId, ufOrigem, ufDestino, ncmId);
        var page = regraFiscalRepository.findAll(spec, pageable).map(RegraFiscalResponse::from);
        return PageResponse.from(page);
    }

    @Transactional(readOnly = true)
    public RegraFiscalResponse getById(Long id) {
        RegraFiscal entity = regraFiscalRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Regra fiscal não encontrada: " + id));
        return RegraFiscalResponse.from(entity);
    }

    @Transactional
    public RegraFiscalResponse create(RegraFiscalRequest request) {
        RegraFiscal entity = new RegraFiscal();
        entity.setEmpresaId(request.empresaId());
        entity.setUfOrigem(request.ufOrigem());
        entity.setUfDestino(request.ufDestino());
        entity.setAliquotaIcms(request.aliquotaIcms());
        entity.setAliquotaPis(request.aliquotaPis());
        entity.setAliquotaCofins(request.aliquotaCofins());
        entity.setAliquotaIpi(request.aliquotaIpi());
        entity.setReducaoBaseIcms(request.reducaoBaseIcms());

        resolveReferences(entity, request);

        return RegraFiscalResponse.from(regraFiscalRepository.save(entity));
    }

    @Transactional
    public void deactivate(Long id) {
        RegraFiscal entity = regraFiscalRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Regra fiscal não encontrada: " + id));
        entity.setAtivo(false);
        entity.setUpdatedAt(LocalDateTime.now());
        regraFiscalRepository.save(entity);
    }

    /**
     * Busca a regra fiscal mais específica aplicável.
     * Prioridade: UF origem + UF destino + NCM > UF origem + UF destino > NCM somente > regra genérica.
     */
    @Transactional(readOnly = true)
    public Optional<RegraFiscalResponse> findRegraAplicavel(Long empresaId, String ufOrigem,
                                                            String ufDestino, Long ncmId) {
        List<RegraFiscal> regras = regraFiscalRepository.findByEmpresaIdAndAtivoTrueOrderById(empresaId);

        return regras.stream()
                .filter(r -> matches(r, ufOrigem, ufDestino, ncmId))
                .max(Comparator.comparingInt(r -> calcularEspecificidade(r, ufOrigem, ufDestino, ncmId)))
                .map(RegraFiscalResponse::from);
    }

    private boolean matches(RegraFiscal r, String ufOrigem, String ufDestino, Long ncmId) {
        if (r.getUfOrigem() != null && !r.getUfOrigem().equalsIgnoreCase(ufOrigem)) return false;
        if (r.getUfDestino() != null && !r.getUfDestino().equalsIgnoreCase(ufDestino)) return false;
        if (r.getNcm() != null && ncmId != null && !r.getNcm().getId().equals(ncmId)) return false;
        if (r.getNcm() != null && ncmId == null) return false;
        return true;
    }

    int calcularEspecificidade(RegraFiscal r, String ufOrigem, String ufDestino, Long ncmId) {
        int score = 0;
        if (r.getUfOrigem() != null && r.getUfOrigem().equalsIgnoreCase(ufOrigem)) score += 2;
        if (r.getUfDestino() != null && r.getUfDestino().equalsIgnoreCase(ufDestino)) score += 2;
        if (r.getNcm() != null && ncmId != null && r.getNcm().getId().equals(ncmId)) score += 4;
        return score;
    }

    private void resolveReferences(RegraFiscal entity, RegraFiscalRequest request) {
        if (request.ncmId() != null) {
            entity.setNcm(ncmRepository.findById(request.ncmId())
                    .orElseThrow(() -> new ResourceNotFoundException("NCM não encontrado: " + request.ncmId())));
        }
        if (request.cfopId() != null) {
            entity.setCfop(cfopRepository.findById(request.cfopId())
                    .orElseThrow(() -> new ResourceNotFoundException("CFOP não encontrado: " + request.cfopId())));
        }
        if (request.cstIcmsId() != null) {
            entity.setCstIcms(cstRepository.findById(request.cstIcmsId())
                    .orElseThrow(() -> new ResourceNotFoundException("CST ICMS não encontrado: " + request.cstIcmsId())));
        }
        if (request.cstPisId() != null) {
            entity.setCstPis(cstRepository.findById(request.cstPisId())
                    .orElseThrow(() -> new ResourceNotFoundException("CST PIS não encontrado: " + request.cstPisId())));
        }
        if (request.cstCofinsId() != null) {
            entity.setCstCofins(cstRepository.findById(request.cstCofinsId())
                    .orElseThrow(() -> new ResourceNotFoundException("CST COFINS não encontrado: " + request.cstCofinsId())));
        }
    }
}
