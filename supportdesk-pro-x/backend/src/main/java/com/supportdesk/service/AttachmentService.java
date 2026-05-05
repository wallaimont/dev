package com.supportdesk.service;

import com.supportdesk.domain.entity.*;
import com.supportdesk.dto.response.AttachmentResponse;
import com.supportdesk.dto.response.UserSummaryResponse;
import com.supportdesk.exception.*;
import com.supportdesk.repository.*;
import com.supportdesk.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final Tika tika = new Tika();

    @Value("${app.storage.upload-dir:./uploads}")
    private String uploadDir;

    @Transactional
    public AttachmentResponse upload(UUID ticketId, MultipartFile file, UserPrincipal principal) throws IOException {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", ticketId));
        User uploader = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", principal.getId()));

        String mimeType = tika.detect(file.getInputStream());
        String storedName = UUID.randomUUID() + "_" + sanitize(Objects.requireNonNull(file.getOriginalFilename()));

        Path dir = Paths.get(uploadDir, ticketId.toString());
        Files.createDirectories(dir);
        Path target = dir.resolve(storedName);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        Attachment attachment = Attachment.builder()
                .ticket(ticket)
                .uploaderId(uploader.getId())
                .originalName(file.getOriginalFilename())
                .storedName(storedName)
                .contentType(mimeType)
                .sizeBytes(file.getSize())
                .storagePath(target.toString())
                .build();
        attachment = attachmentRepository.save(attachment);
        log.info("Attachment {} uploaded to ticket {}", storedName, ticketId);
        return toResponse(attachment);
    }

    @Transactional(readOnly = true)
    public List<AttachmentResponse> findByTicket(UUID ticketId) {
        return attachmentRepository.findByTicketId(ticketId).stream().map(this::toResponse).toList();
    }

    private String sanitize(String name) {
        return name.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private AttachmentResponse toResponse(Attachment a) {
        User uploadedBy = a.getUploaderId() != null ? userRepository.findById(a.getUploaderId()).orElse(null) : null;
        return AttachmentResponse.builder()
                .id(a.getId())
                .fileName(a.getOriginalName())
                .mimeType(a.getContentType())
                .sizeBytes(a.getSizeBytes())
                .downloadUrl("/api/v1/attachments/" + a.getId() + "/download")
                .uploadedAt(a.getCreatedAt())
                .uploadedBy(uploadedBy != null
                        ? UserSummaryResponse.builder().id(uploadedBy.getId())
                                .fullName(uploadedBy.getName())
                                .email(uploadedBy.getEmail()).build()
                        : null)
                .build();
    }
}
