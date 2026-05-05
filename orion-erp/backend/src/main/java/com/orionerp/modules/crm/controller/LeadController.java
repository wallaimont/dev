package com.orionerp.modules.crm.controller;

import com.orionerp.common.ApiResponse;
import com.orionerp.common.PageResponse;
import com.orionerp.modules.crm.dto.LeadRequest;
import com.orionerp.modules.crm.dto.LeadResponse;
import com.orionerp.modules.crm.service.LeadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/crm/leads")
@RequiredArgsConstructor
public class LeadController {

    private final LeadService leadService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<LeadResponse>>> list(
            @RequestParam(required = false) Long empresaId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long responsavelId,
            @RequestParam(required = false) String term,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(
                leadService.list(empresaId, status, responsavelId, term, pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LeadResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(leadService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<LeadResponse>> create(@Valid @RequestBody LeadRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(leadService.create(request)));
    }

    @PostMapping("/{id}/avancar")
    public ResponseEntity<ApiResponse<LeadResponse>> avancarStatus(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(leadService.avancarStatus(id)));
    }

    @PostMapping("/{id}/converter")
    public ResponseEntity<ApiResponse<LeadResponse>> converter(
            @PathVariable Long id, @RequestParam Long clienteId) {
        return ResponseEntity.ok(ApiResponse.ok(leadService.converter(id, clienteId)));
    }

    @PostMapping("/{id}/perder")
    public ResponseEntity<ApiResponse<LeadResponse>> perder(
            @PathVariable Long id, @RequestParam(required = false) String motivo) {
        return ResponseEntity.ok(ApiResponse.ok(leadService.perder(id, motivo)));
    }
}
