package com.supportdesk.service;

import com.supportdesk.domain.entity.*;
import com.supportdesk.dto.request.CreateCommentRequest;
import com.supportdesk.dto.response.*;
import com.supportdesk.exception.ResourceNotFoundException;
import com.supportdesk.repository.*;
import com.supportdesk.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentResponse create(UUID ticketId, CreateCommentRequest req, UserPrincipal principal) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", ticketId));
        User author = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", principal.getId()));

        Comment comment = Comment.builder()
                .ticket(ticket)
                .author(author)
                .body(req.getContent())
                .internal(req.isInternal())
                .build();
        comment = commentRepository.save(comment);
        return toResponse(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> findByTicket(UUID ticketId, boolean includeInternal) {
        List<Comment> comments = includeInternal
                                ? commentRepository.findByTicketId(ticketId, Pageable.unpaged()).getContent()
                                : commentRepository.findByTicketIdAndInternalFalse(ticketId, Pageable.unpaged()).getContent();
        return comments.stream().map(this::toResponse).toList();
    }

    private CommentResponse toResponse(Comment c) {
        return CommentResponse.builder()
                .id(c.getId())
                .content(c.getBody())
                .internal(c.isInternal())
                .author(UserSummaryResponse.builder()
                        .id(c.getAuthor().getId())
                        .fullName(c.getAuthor().getName())
                        .email(c.getAuthor().getEmail())
                        .build())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }
}
