package com.monssif.ticket_management_service.service;

import com.monssif.ticket_management_service.dto.AssignmentRequestDTO;
import com.monssif.ticket_management_service.dto.AssignmentResponseDTO;
import com.monssif.ticket_management_service.entity.Ticket;
import com.monssif.ticket_management_service.entity.TicketHistory;
import com.monssif.ticket_management_service.entity.User;
import com.monssif.ticket_management_service.exception.InvalidTicketOperationException;
import com.monssif.ticket_management_service.exception.TicketNotFoundException;
import com.monssif.ticket_management_service.repository.TicketHistoryRepository;
import com.monssif.ticket_management_service.repository.TicketRepository;
import com.monssif.ticket_management_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketAssignmentService {
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final TicketHistoryRepository ticketHistoryRepository;

    @Transactional
    public AssignmentResponseDTO assignTicket(Long ticketId, AssignmentRequestDTO requestDTO) {
        log.info("Assigning ticket {} to agent {}", ticketId, requestDTO.getAgentId());

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));

        if (ticket.getAssignedAgent() != null) {
            throw new InvalidTicketOperationException(
                    String.format("Ticket %d is already assigned to %s. Use reassign endpoint instead.",
                            ticketId, ticket.getAssignedAgent().getFullName()));
        }

        User agent = validateAndGetAgent(requestDTO.getAgentId());

        ticket.setAssignedAgent(agent);
        Ticket savedTicket = ticketRepository.save(ticket);

        createAssignmentHistory(ticket, null, agent);

        log.info("Ticket {} successfully assigned to agent {}", ticketId, agent.getFullName());

        return buildAssignmentResponse(savedTicket, null, agent, requestDTO.getNote(),
                String.format("Ticket successfully assigned to %s", agent.getFullName()));
    }

    @Transactional
    public AssignmentResponseDTO reassignTicket(Long ticketId, AssignmentRequestDTO requestDTO) {
        log.info("Reassigning ticket {} to agent {}", ticketId, requestDTO.getAgentId());

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));

        User previousAgent = ticket.getAssignedAgent();

        if (previousAgent == null) {
            throw new InvalidTicketOperationException(
                    String.format("Ticket %d is not currently assigned. Use assign endpoint instead.", ticketId));
        }

        User newAgent = validateAndGetAgent(requestDTO.getAgentId());

        if (previousAgent.getId().equals(newAgent.getId())) {
            throw new InvalidTicketOperationException(
                    String.format("Ticket is already assigned to %s", newAgent.getFullName()));
        }

        ticket.setAssignedAgent(newAgent);
        Ticket savedTicket = ticketRepository.save(ticket);

        createAssignmentHistory(ticket, previousAgent, newAgent);

        log.info("Ticket {} successfully reassigned from {} to {}",
                ticketId, previousAgent.getFullName(), newAgent.getFullName());

        return buildAssignmentResponse(savedTicket, previousAgent, newAgent, requestDTO.getNote(),
                String.format("Ticket reassigned from %s to %s",
                        previousAgent.getFullName(), newAgent.getFullName()));
    }

    @Transactional
    public AssignmentResponseDTO unassignTicket(Long ticketId, String note) {
        log.info("Unassigning ticket {}", ticketId);

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));

        User previousAgent = ticket.getAssignedAgent();

        if (previousAgent == null) {
            throw new InvalidTicketOperationException(
                    String.format("Ticket %d is not currently assigned", ticketId));
        }

        ticket.setAssignedAgent(null);
        Ticket savedTicket = ticketRepository.save(ticket);

        createAssignmentHistory(ticket, previousAgent, null);

        log.info("Ticket {} successfully unassigned from {}", ticketId, previousAgent.getFullName());

        return buildAssignmentResponse(savedTicket, previousAgent, null, note,
                String.format("Ticket unassigned from %s", previousAgent.getFullName()));
    }

    private User validateAndGetAgent(Long agentId) {
        User agent = userRepository.findById(agentId)
                .orElseThrow(() -> new InvalidTicketOperationException(
                        "Agent not found with ID: " + agentId));

        if (!agent.getIsActive()) {
            throw new InvalidTicketOperationException(
                    String.format("Agent %s is not active", agent.getFullName()));
        }

        if (!agent.isAgent() && !agent.isAdmin()) {
            throw new InvalidTicketOperationException(
                    String.format("User %s is not an agent or admin. Role: %s",
                            agent.getFullName(), agent.getRole()));
        }

        return agent;
    }

    private void createAssignmentHistory(Ticket ticket, User previousAgent, User newAgent) {
        String oldValue = previousAgent != null ? previousAgent.getFullName() : "Unassigned";
        String newValue = newAgent != null ? newAgent.getFullName() : "Unassigned";
        User changedBy = newAgent != null ? newAgent : previousAgent;

        TicketHistory historyEntry = TicketHistory.builder()
                .ticket(ticket)
                .fieldName("assigned_agent")
                .oldValue(oldValue)
                .newValue(newValue)
                .changedBy(changedBy)
                .build();

        ticketHistoryRepository.save(historyEntry);
        log.debug("Created history entry for ticket {} assignment change", ticket.getId());
    }

    private AssignmentResponseDTO buildAssignmentResponse(
            Ticket ticket,
            User previousAgent,
            User currentAgent,
            String note,
            String message) {

        return AssignmentResponseDTO.builder()
                .ticketId(ticket.getId())
                .ticketTitle(ticket.getTitle())
                .currentAgentId(currentAgent != null ? currentAgent.getId() : null)
                .currentAgentName(currentAgent != null ? currentAgent.getFullName() : null)
                .currentAgentEmail(currentAgent != null ? currentAgent.getEmail() : null)
                .previousAgentId(previousAgent != null ? previousAgent.getId() : null)
                .previousAgentName(previousAgent != null ? previousAgent.getFullName() : null)
                .assignedAt(LocalDateTime.now())
                .note(note)
                .message(message)
                .build();
    }
}