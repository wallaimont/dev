package com.supportdesk.controller;

import com.supportdesk.dto.request.*;
import com.supportdesk.dto.response.*;
import com.supportdesk.security.UserPrincipal;
import com.supportdesk.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Tickets")
@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @Operation(summary = "Create ticket")
    @PostMapping
    public ResponseEntity<ApiResponse<TicketResponse>> create(
            @Valid @RequestBody CreateTicketRequest req,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(ticketService.create(req, principal)));
    }

    @Operation(summary = "List tickets")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<TicketResponse>>> list(
            Pageable pageable,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.ok(ticketService.findAll(pageable, principal)));
    }

    @Operation(summary = "Get ticket by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TicketResponse>> get(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.ok(ticketService.findById(id, principal)));
    }

    @Operation(summary = "Update ticket")
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('AGENT','SUPERVISOR','ADMIN')")
    public ResponseEntity<ApiResponse<TicketResponse>> update(
            @PathVariable UUID id,
            @RequestBody UpdateTicketRequest req,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.ok(ticketService.update(id, req, principal)));
    }
}
