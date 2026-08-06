package com.flowpay.routing_service.service;

import com.flowpay.routing_service.exception.QueueFullException;
import com.flowpay.routing_service.model.Agent;
import com.flowpay.routing_service.model.Team;
import com.flowpay.routing_service.model.Ticket;
import com.flowpay.routing_service.model.enums.TicketStatus;
import com.flowpay.routing_service.repository.AgentRepository;
import com.flowpay.routing_service.repository.TeamRepository;
import com.flowpay.routing_service.repository.TicketRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;
    private final AgentRepository agentRepository;
    private final TeamRepository teamRepository;

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public String mapSubjectToTeamName(String subject) {
        String normalizedSubject = subject.trim().toLowerCase();

        if (normalizedSubject.contains("cartao") || normalizedSubject.contains("cartão") || normalizedSubject.contains("cards")) {
            return "CARDS";
        } else if (normalizedSubject.contains("emprestimo") || normalizedSubject.contains("empréstimo") || normalizedSubject.contains("loans")) {
            return "LOANS";
        } else {
            return "OTHER";
        }
    }

    @Transactional(noRollbackFor = QueueFullException.class)
    public Ticket createTicket(Long chatReference, String subject) {
        // mapeia o assunto
        String teamName = mapSubjectToTeamName(subject);

        // busca a entidade
        Team team = teamRepository.findByName(teamName).orElseThrow(() -> new RuntimeException("Erro interno: Time " + teamName + " não encontrado."));

        // cria solicitacao
        Ticket ticket = new Ticket();
        ticket.setChatReference(chatReference);
        ticket.setSubject(subject);
        ticket.setTeam(team);

        // procura atendente
        List<Agent> availableAgents = agentRepository.findAvailableAgentsByTeamWithLock(team);

        if (!availableAgents.isEmpty()) {
            // IN PROGRESS
            Agent selectedAgent = availableAgents.get(0); // Pega o mais desocupado (ORDER BY ASC)

            selectedAgent.setCurrentWorkload(selectedAgent.getCurrentWorkload() + 1);
            agentRepository.save(selectedAgent);

            ticket.setAgent(selectedAgent);
            ticket.setStatus(TicketStatus.IN_PROGRESS);

        } else {
            // QUEUED
            long queuedCount = ticketRepository.countByTeamIdAndStatus(team.getId(), TicketStatus.QUEUED);

            if (queuedCount < 3) {
                ticket.setStatus(TicketStatus.QUEUED);
            } else {
                // REJECTED
                ticket.setStatus(TicketStatus.REJECTED);
                ticketRepository.save(ticket);
                throw new QueueFullException();
            }
        }
        // salva no db
        return ticketRepository.save(ticket);
    }

    @Transactional
    public Ticket finishTicket(Long ticketId){
        Ticket ticketToFinish = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Ticket não encontrado com id: " + ticketId));
        if (ticketToFinish.getStatus() != TicketStatus.IN_PROGRESS){
            throw new IllegalStateException("Apenas tickets em andamento (IN_PROGRESS) podem ser finalizados.");
        }

        Agent agent = ticketToFinish.getAgent();
        Team team = ticketToFinish.getTeam();

        ticketToFinish.setStatus(TicketStatus.FINISHED);
        ticketRepository.save(ticketToFinish);

        agent.setCurrentWorkload(agent.getCurrentWorkload() - 1);
        agentRepository.save(agent);

        Optional<Ticket> queuedTicketOpt = ticketRepository
                .findFirstByTeamAndStatusOrderByCreatedAtAsc(team, TicketStatus.QUEUED);

        if (queuedTicketOpt.isPresent()) {
            Ticket queuedTicket = queuedTicketOpt.get();
            queuedTicket.setStatus(TicketStatus.IN_PROGRESS);
            queuedTicket.setAgent(agent);

            agent.setCurrentWorkload(agent.getCurrentWorkload() + 1);
            agentRepository.save(agent);

            ticketRepository.save(queuedTicket);
        }
        return ticketToFinish;
    }
}
