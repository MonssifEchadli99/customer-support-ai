package com.monssif.ticket_management_service.service;

import com.monssif.ticket_management_service.dto.WorkflowTransitionRequestDTO;
import com.monssif.ticket_management_service.dto.WorkflowTransitionResponseDTO;
import com.monssif.ticket_management_service.entity.*;
import com.monssif.ticket_management_service.exception.InvalidTicketOperationException;
import com.monssif.ticket_management_service.exception.TicketNotFoundException;
import com.monssif.ticket_management_service.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TicketWorkflowService {

    private final CommentRepository commentRepository;
    private final TicketRepository ticketRepository;
    private final TicketHistoryRepository ticketHistoryRepository;
    private final StatusRepository statusRepository;
    private final UserRepository userRepository;

    private static final Map<String, Set<String>> VALID_TRANSITIONS = Map.of(
            "OPEN", Set.of("IN_PROGRESS", "WAITING_CUSTOMER", "ON_HOLD", "CLOSED"),
            "IN_PROGRESS", Set.of("WAITING_CUSTOMER", "ON_HOLD", "RESOLVED", "OPEN"),
            "WAITING_CUSTOMER", Set.of("IN_PROGRESS", "ON_HOLD", "CLOSED"),
            "ON_HOLD", Set.of("OPEN", "IN_PROGRESS", "CLOSED"),
            "RESOLVED", Set.of("CLOSED", "IN_PROGRESS"),
            "CLOSED", Set.of()
    );

    @Transactional
    public WorkflowTransitionResponseDTO transitionTicketStatus(Long ticketId, WorkflowTransitionRequestDTO requestDTO) {
        log.info("Transitioning ticket {} to status ID {}", ticketId, requestDTO.getStatusId());

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));

        Status newStatus = statusRepository.findById(requestDTO.getStatusId())
                .orElseThrow(() -> new InvalidTicketOperationException(
                        "Status not found with ID: " + requestDTO.getStatusId()));

        Status oldStatus = ticket.getStatus();
        String oldStatusName = oldStatus.getName();
        String newStatusName = newStatus.getName();

        if (oldStatusName.equals(newStatusName)) {
            throw new InvalidTicketOperationException(
                    String.format("Ticket is already in status: %s", oldStatusName));
        }

        validateTransition(oldStatusName, newStatusName);

        User changedByUser = null;
        if (requestDTO.getChangedByUserId() != null) {
            changedByUser = userRepository.findById(requestDTO.getChangedByUserId())
                    .orElseThrow(() -> new InvalidTicketOperationException(
                            "User not found with ID: " + requestDTO.getChangedByUserId()));
        }

        ticket.setStatus(newStatus);

        LocalDateTime now = LocalDateTime.now();

        if ("RESOLVED".equals(newStatusName) && ticket.getResolvedAt() == null) {
            ticket.setResolvedAt(now);
            log.info("Ticket {} marked as resolved at {}", ticketId, now);
        }

        if ("RESOLVED".equals(oldStatusName) && !"CLOSED".equals(newStatusName)) {
            ticket.setResolvedAt(null);
            log.info("Ticket {} reopened, cleared resolvedAt timestamp", ticketId);
        }

        Ticket updatedTicket = ticketRepository.save(ticket);

        TicketHistory historyEntry = TicketHistory.builder()
                .ticket(updatedTicket)
                .fieldName("status")
                .oldValue(oldStatusName)
                .newValue(newStatusName)
                .changedBy(changedByUser)
                .build();

        if (requestDTO.getComment() != null && !requestDTO.getComment().isBlank()) {
            String systemComment = String.format(
                    "Status changed from %s to %s: %s",
                    oldStatusName,
                    newStatusName,
                    requestDTO.getComment()
            );

            Comment transitionComment = Comment.builder()
                    .content(systemComment)
                    .isInternal(true)
                    .ticket(updatedTicket)
                    .user(changedByUser)
                    .build();

            commentRepository.save(transitionComment);
            log.info("Saved transition comment in Comments table for user visibility");
        }

        ticketHistoryRepository.save(historyEntry);
        log.info("Created history entry for ticket {} status change", ticketId);

        log.info("Successfully transitioned ticket {} from {} to {}",
                ticketId, oldStatusName, newStatusName);

        return WorkflowTransitionResponseDTO.builder()
                .ticketId(updatedTicket.getId())
                .ticketTitle(updatedTicket.getTitle())
                .previousStatusName(oldStatusName)
                .previousStatusId(oldStatus.getId())
                .newStatusName(newStatusName)
                .newStatusId(newStatus.getId())
                .isStatusFinal(newStatus.getIsFinal())
                .transitionComment(requestDTO.getComment())
                .changedByUserName(changedByUser != null ? changedByUser.getFullName() : null)
                .changedByUserId(changedByUser != null ? changedByUser.getId() : null)
                .transitionedAt(now)
                .resolvedAt(updatedTicket.getResolvedAt())
                .isResolved(updatedTicket.isResolved())
                .message(String.format("Ticket successfully transitioned from %s to %s",
                        oldStatusName, newStatusName))
                .build();
    }


    private void validateTransition(String fromStatus, String toStatus) {
        Set<String> allowedTransitions = VALID_TRANSITIONS.getOrDefault(fromStatus, Set.of());

        if (!allowedTransitions.contains(toStatus)) {
            throw new InvalidTicketOperationException(
                    String.format("Invalid status transition: Cannot transition from %s to %s. Allowed transitions: %s",
                            fromStatus, toStatus, allowedTransitions.isEmpty() ? "NONE (terminal state)" : allowedTransitions));
        }
    }

    public List<String> getAvailableTransitions(Long ticketId) {
        log.info("Fetching available transitions for ticket {}", ticketId);

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));

        String currentStatus = ticket.getStatus().getName();
        Set<String> allowedTransitions = VALID_TRANSITIONS.getOrDefault(currentStatus, Set.of());

        return new ArrayList<>(allowedTransitions);
    }

    public boolean isTransitionValid(Long ticketId, Long targetStatusId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));

        Status targetStatus = statusRepository.findById(targetStatusId)
                .orElseThrow(() -> new InvalidTicketOperationException(
                        "Status not found with ID: " + targetStatusId));

        String currentStatusName = ticket.getStatus().getName();
        String targetStatusName = targetStatus.getName();

        Set<String> allowedTransitions = VALID_TRANSITIONS.getOrDefault(currentStatusName, Set.of());
        return allowedTransitions.contains(targetStatusName);
    }

    public Map<String, Set<String>> getWorkflowDefinition() {
        return new HashMap<>(VALID_TRANSITIONS);
    }
}