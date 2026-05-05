package com.supportdesk.controller;

import com.supportdesk.dto.response.*;
import com.supportdesk.security.UserPrincipal;
import com.supportdesk.service.AttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Tag(name = "Attachments")
@RestController
@RequestMapping("/api/v1/tickets/{ticketId}/attachments")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;

    @Operation(summary = "Upload attachment")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<AttachmentResponse>> upload(
            @PathVariable UUID ticketId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserPrincipal principal) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(attachmentService.upload(ticketId, file, principal)));
    }

    @Operation(summary = "List attachments")
    @GetMapping
    public ResponseEntity<ApiResponse<List<AttachmentResponse>>> list(@PathVariable UUID ticketId) {
        return ResponseEntity.ok(ApiResponse.ok(attachmentService.findByTicket(ticketId)));
    }
}
