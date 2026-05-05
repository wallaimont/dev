package com.supportdesk.controller;

import com.supportdesk.dto.request.CreateCommentRequest;
import com.supportdesk.dto.response.*;
import com.supportdesk.security.UserPrincipal;
import com.supportdesk.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Comments")
@RestController
@RequestMapping("/api/v1/tickets/{ticketId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "Add comment")
    @PostMapping
    public ResponseEntity<ApiResponse<CommentResponse>> create(
            @PathVariable UUID ticketId,
            @Valid @RequestBody CreateCommentRequest req,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(commentService.create(ticketId, req, principal)));
    }

    @Operation(summary = "List comments")
    @GetMapping
    public ResponseEntity<ApiResponse<List<CommentResponse>>> list(
            @PathVariable UUID ticketId,
            @RequestParam(defaultValue = "false") boolean includeInternal,
            @AuthenticationPrincipal UserPrincipal principal) {
        boolean isAgent = principal.getAuthorities().stream()
                .anyMatch(a -> !a.getAuthority().equals("ROLE_CUSTOMER"));
        return ResponseEntity.ok(ApiResponse.ok(
                commentService.findByTicket(ticketId, includeInternal && isAgent)));
    }
}
