package com.flowpay.routing_service.repository;

import com.flowpay.routing_service.model.Team;
import com.flowpay.routing_service.model.Ticket;
import com.flowpay.routing_service.model.enums.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    long countByTeamIdAndStatus(Long teamId, TicketStatus status);

    Optional<Ticket> findFirstByTeamAndStatusOrderByCreatedAtAsc(Team team, TicketStatus status);
}