package com.supportdesk.repository;

import com.supportdesk.domain.entity.Ticket;
import com.supportdesk.domain.enums.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID>, JpaSpecificationExecutor<Ticket> {

    Page<Ticket> findByReporterId(UUID reporterId, Pageable pageable);

    Page<Ticket> findByAssigneeId(UUID assigneeId, Pageable pageable);

    @Query("SELECT t FROM Ticket t WHERE t.slaDeadlineAt < :now AND t.slaBreached = FALSE AND t.status NOT IN :excluded")
    List<Ticket> findTicketsBeyondSla(Instant now, List<TicketStatus> excluded);

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.status = :status")
    long countByStatus(TicketStatus status);

    @Query(value = "SELECT NEXTVAL('ticket_seq')", nativeQuery = true)
    long nextTicketSequence();
}
