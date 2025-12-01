package com.monssif.ticket_management_service.service;

import com.monssif.ticket_management_service.dto.TicketRequestDTO;
import com.monssif.ticket_management_service.dto.TicketResponseDTO;
import com.monssif.ticket_management_service.dto.TicketSummaryDTO;
import com.monssif.ticket_management_service.entity.Category;
import com.monssif.ticket_management_service.entity.Priority;
import com.monssif.ticket_management_service.entity.Status;
import com.monssif.ticket_management_service.entity.Ticket;
import com.monssif.ticket_management_service.entity.User;
import com.monssif.ticket_management_service.enums.EventType;
import com.monssif.ticket_management_service.events.TicketCreatedEvent;
import com.monssif.ticket_management_service.events.TicketUpdatedEvent;
import com.monssif.ticket_management_service.exception.InvalidTicketOperationException;
import com.monssif.ticket_management_service.exception.TicketNotFoundException;
import com.monssif.ticket_management_service.mapper.TicketMapper;
import com.monssif.ticket_management_service.messaging.AIAnalysisProducer;
import com.monssif.ticket_management_service.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketService {
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PriorityRepository priorityRepository;
    private final StatusRepository statusRepository;
    private final TicketMapper ticketMapper;
    private final AIAnalysisProducer producer;

    @Transactional
    public TicketResponseDTO createTicket(TicketRequestDTO requestDTO) {
        log.info("Creating new ticket with title: {}", requestDTO.getTitle());

        User customer = userRepository.findById(requestDTO.getCustomerId())
                .orElseThrow(() -> new InvalidTicketOperationException(
                        "Customer not found with ID: " + requestDTO.getCustomerId()));

        if (!customer.isCustomer()) {
            throw new InvalidTicketOperationException(
                    "User with ID " + requestDTO.getCustomerId() + " is not a customer");
        }

        if (!customer.getIsActive()) {
            throw new InvalidTicketOperationException("Customer account is not active");
        }

        Category category = categoryRepository.findById(requestDTO.getCategoryId())
                .orElseThrow(() -> new InvalidTicketOperationException(
                        "Category not found with ID: " + requestDTO.getCategoryId()));

        if (!category.getIsActive()) {
            throw new InvalidTicketOperationException("Category is not active");
        }

        Priority priority = priorityRepository.findById(requestDTO.getPriorityId())
                .orElseThrow(() -> new InvalidTicketOperationException(
                        "Priority not found with ID: " + requestDTO.getPriorityId()));

        Status status = statusRepository.findById(requestDTO.getStatusId())
                .orElseThrow(() -> new InvalidTicketOperationException(
                        "Status not found with ID: " + requestDTO.getStatusId()));

        Ticket ticket = Ticket.builder()
                .title(requestDTO.getTitle())
                .description(requestDTO.getDescription())
                .customer(customer)
                .category(category)
                .priority(priority)
                .status(status)
                .build();

        if (requestDTO.getAssignedAgentId() != null) {
            User agent = userRepository.findById(requestDTO.getAssignedAgentId())
                    .orElseThrow(() -> new InvalidTicketOperationException(
                            "Agent not found with ID: " + requestDTO.getAssignedAgentId()));

            if (!agent.isAgent() && !agent.isAdmin()) {
                throw new InvalidTicketOperationException(
                        "User with ID " + requestDTO.getAssignedAgentId() + " is not an agent or admin");
            }

            if (!agent.getIsActive()) {
                throw new InvalidTicketOperationException("Agent account is not active");
            }

            ticket.setAssignedAgent(agent);
        }

        Ticket savedTicket = ticketRepository.save(ticket);
        log.info("Ticket created successfully with ID: {}", savedTicket.getId());

        TicketCreatedEvent event = TicketCreatedEvent.builder()
                .ticketId(savedTicket.getId())
                .title(savedTicket.getTitle())
                .description(savedTicket.getDescription())
                .categoryName(savedTicket.getCategory().getName())
                .priorityName(savedTicket.getPriority().getName())
                .customerId(savedTicket.getCustomer().getId())
                .createdAt(savedTicket.getCreatedAt())
                .eventType(EventType.TICKET_CREATED.name())
                .build();

        producer.publishTicketCreated(event);

        return ticketMapper.toResponseDTO(savedTicket);
    }

    public TicketResponseDTO getTicketById(Long ticketId) {
        log.info("Fetching ticket with ID: {}", ticketId);

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));

        return ticketMapper.toResponseDTO(ticket);
    }

    public Page<TicketSummaryDTO> getAllTickets(Pageable pageable) {
        log.info("Fetching all tickets - Page: {}, Size: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<Ticket> ticketsPage = ticketRepository.findAll(pageable);
        return ticketsPage.map(ticketMapper::toSummaryDTO);
    }

    @Transactional
    public TicketResponseDTO updateTicket(Long ticketId, TicketRequestDTO requestDTO) {
        log.info("Updating ticket with ID: {}", ticketId);

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));

        ticket.setTitle(requestDTO.getTitle());
        ticket.setDescription(requestDTO.getDescription());

        if (!ticket.getCustomer().getId().equals(requestDTO.getCustomerId())) {
            User newCustomer = userRepository.findById(requestDTO.getCustomerId())
                    .orElseThrow(() -> new InvalidTicketOperationException(
                            "Customer not found with ID: " + requestDTO.getCustomerId()));

            if (!newCustomer.isCustomer()) {
                throw new InvalidTicketOperationException(
                        "User with ID " + requestDTO.getCustomerId() + " is not a customer");
            }

            ticket.setCustomer(newCustomer);
        }

        if (!ticket.getCategory().getId().equals(requestDTO.getCategoryId())) {
            Category newCategory = categoryRepository.findById(requestDTO.getCategoryId())
                    .orElseThrow(() -> new InvalidTicketOperationException(
                            "Category not found with ID: " + requestDTO.getCategoryId()));

            if (!newCategory.getIsActive()) {
                throw new InvalidTicketOperationException("Category is not active");
            }

            ticket.setCategory(newCategory);
        }

        if (!ticket.getPriority().getId().equals(requestDTO.getPriorityId())) {
            Priority newPriority = priorityRepository.findById(requestDTO.getPriorityId())
                    .orElseThrow(() -> new InvalidTicketOperationException(
                            "Priority not found with ID: " + requestDTO.getPriorityId()));

            ticket.setPriority(newPriority);
        }

        if (!ticket.getStatus().getId().equals(requestDTO.getStatusId())) {
            Status newStatus = statusRepository.findById(requestDTO.getStatusId())
                    .orElseThrow(() -> new InvalidTicketOperationException(
                            "Status not found with ID: " + requestDTO.getStatusId()));

            ticket.setStatus(newStatus);

            if (newStatus.getIsFinal() && ticket.getResolvedAt() == null) {
                ticket.setResolvedAt(LocalDateTime.now());
                log.info("Ticket {} marked as resolved", ticketId);
            }
        }

        if (requestDTO.getAssignedAgentId() != null) {
            if (ticket.getAssignedAgent() == null ||
                    !ticket.getAssignedAgent().getId().equals(requestDTO.getAssignedAgentId())) {

                User newAgent = userRepository.findById(requestDTO.getAssignedAgentId())
                        .orElseThrow(() -> new InvalidTicketOperationException(
                                "Agent not found with ID: " + requestDTO.getAssignedAgentId()));

                if (!newAgent.isAgent() && !newAgent.isAdmin()) {
                    throw new InvalidTicketOperationException(
                            "User with ID " + requestDTO.getAssignedAgentId() + " is not an agent or admin");
                }

                ticket.setAssignedAgent(newAgent);
            }
        } else {
            ticket.setAssignedAgent(null);
        }

        Ticket updatedTicket = ticketRepository.save(ticket);
        log.info("Ticket updated successfully with ID: {}", ticketId);

        TicketUpdatedEvent event = TicketUpdatedEvent.builder()
                .ticketId(updatedTicket.getId())
                .title(updatedTicket.getTitle())
                .description(updatedTicket.getDescription())
                .statusName(updatedTicket.getStatus().getName())
                .updatedAt(updatedTicket.getUpdatedAt())
                .eventType(EventType.TICKET_UPDATED.name())
                .build();

        producer.publishTicketUpdated(event);

        return ticketMapper.toResponseDTO(updatedTicket);
    }

    @Transactional
    public void deleteTicket(Long ticketId) {
        log.info("Deleting ticket with ID: {}", ticketId);

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));

        ticketRepository.delete(ticket);
        log.info("Ticket deleted successfully with ID: {}", ticketId);
    }

    public Page<TicketSummaryDTO> getTicketsByCustomerId(Long customerId, Pageable pageable) {
        log.info("Fetching tickets for customer ID: {}", customerId);

        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new InvalidTicketOperationException(
                        "Customer not found with ID: " + customerId));

        Page<Ticket> ticketsPage = ticketRepository.findByCustomer(customer, pageable);
        return ticketsPage.map(ticketMapper::toSummaryDTO);
    }

    public Page<TicketSummaryDTO> getTicketsByAgentId(Long agentId, Pageable pageable) {
        log.info("Fetching tickets for agent ID: {}", agentId);

        User agent = userRepository.findById(agentId)
                .orElseThrow(() -> new InvalidTicketOperationException(
                        "Agent not found with ID: " + agentId));

        Page<Ticket> ticketsPage = ticketRepository.findByAssignedAgent(agent, pageable);
        return ticketsPage.map(ticketMapper::toSummaryDTO);
    }

    public Page<TicketSummaryDTO> getUnassignedTickets(Pageable pageable) {
        log.info("Fetching unassigned tickets");

        Page<Ticket> ticketsPage = ticketRepository.findUnassignedTickets(pageable);
        return ticketsPage.map(ticketMapper::toSummaryDTO);
    }


    public Page<TicketSummaryDTO> getTicketsByStatus(String statusName, Pageable pageable) {
        log.info("Fetching tickets with status: {}", statusName);

        Page<Ticket> ticketsPage = ticketRepository.findByStatusName(statusName, pageable);
        return ticketsPage.map(ticketMapper::toSummaryDTO);
    }

    public Page<TicketSummaryDTO> searchTickets(String searchTerm, Pageable pageable) {
        log.info("Searching tickets with term: {}", searchTerm);

        Page<Ticket> ticketsPage = ticketRepository.searchByTitleOrDescription(searchTerm, pageable);
        return ticketsPage.map(ticketMapper::toSummaryDTO);
    }

    public Page<TicketSummaryDTO> getTicketsByPriorityLevel(Integer priorityLevel, Pageable pageable) {
        log.info("Fetching tickets with priority level: {}", priorityLevel);

        Page<Ticket> ticketsPage = ticketRepository.findByPriorityLevel(priorityLevel, pageable);
        return ticketsPage.map(ticketMapper::toSummaryDTO);
    }

    public Page<TicketSummaryDTO> getTicketsByCategoryName(String categoryName, Pageable pageable) {
        log.info("Fetching tickets with category: {}", categoryName);

        Page<Ticket> ticketsPage = ticketRepository.findByCategoryName(categoryName, pageable);
        return ticketsPage.map(ticketMapper::toSummaryDTO);
    }

    public Page<TicketSummaryDTO> getTicketsByDateRange(
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable) {
        log.info("Fetching tickets created between {} and {}", startDate, endDate);

        Page<Ticket> ticketsPage = ticketRepository.findByCreatedAtBetween(startDate, endDate, pageable);
        return ticketsPage.map(ticketMapper::toSummaryDTO);
    }



    @Transactional
    public void updateTicketWithAIAnalysis(Long ticketId, Double sentimentScore, Long suggestedCategoryId) {
        log.info("Updating ticket {} with AI analysis results", ticketId);

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));

        ticket.setAiSentimentScore(sentimentScore);

        if (suggestedCategoryId != null) {
            Category suggestedCategory = categoryRepository.findById(suggestedCategoryId)
                    .orElseThrow(() -> new InvalidTicketOperationException(
                            "AI Suggested Category not found with ID: " + suggestedCategoryId));
            ticket.setAiSuggestedCategory(suggestedCategory);
        }

        ticketRepository.save(ticket);
        log.info("Ticket {} updated with AI analysis - Sentiment: {}, Suggested Category: {}",
                ticketId, sentimentScore, suggestedCategoryId);
    }
}