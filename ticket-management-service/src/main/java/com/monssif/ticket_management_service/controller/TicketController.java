package com.monssif.ticket_management_service.controller;

import com.monssif.ticket_management_service.dto.TicketRequestDTO;
import com.monssif.ticket_management_service.dto.TicketResponseDTO;
import com.monssif.ticket_management_service.dto.TicketSummaryDTO;
import com.monssif.ticket_management_service.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
@Slf4j
public class TicketController {

    private final TicketService ticketService;

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponseDTO> getTicketById(@PathVariable("id") Long id){
        log.info("REST request to get ticket by ID: {}", id);
        TicketResponseDTO ticket = this.ticketService.getTicketById(id);
        return ResponseEntity.ok(ticket);
    }

    @PostMapping
    public ResponseEntity<TicketResponseDTO> createTicket(@RequestBody @Valid TicketRequestDTO requestDto){
        log.info("REST request to create ticket: {}", requestDto.getTitle());
        TicketResponseDTO createdTicket = this.ticketService.createTicket(requestDto);
        return new ResponseEntity<>(createdTicket, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<TicketSummaryDTO>> getAllTickets(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        log.info("REST request to get all tickets - Page: {}, Size: {}",
                pageable.getPageNumber(), pageable.getPageSize());
        Page<TicketSummaryDTO> tickets = ticketService.getAllTickets(pageable);
        return ResponseEntity.ok(tickets);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TicketResponseDTO> updateTicket(
            @PathVariable Long id,
            @Valid @RequestBody TicketRequestDTO requestDTO) {
        log.info("REST request to update ticket with ID: {}", id);
        TicketResponseDTO updatedTicket = ticketService.updateTicket(id, requestDTO);
        return ResponseEntity.ok(updatedTicket);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id){
        log.info("REST request to delete ticket with ID: {}", id);
        ticketService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<Page<TicketSummaryDTO>> getTicketsByCustomerId(
            @PathVariable Long customerId,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        log.info("REST request to get tickets for customer ID: {}", customerId);
        Page<TicketSummaryDTO> tickets = ticketService.getTicketsByCustomerId(customerId, pageable);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/agent/{agentId}")
    public ResponseEntity<Page<TicketSummaryDTO>> getTicketsByAgentId(
            @PathVariable Long agentId,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        log.info("REST request to get tickets for agent ID: {}", agentId);
        Page<TicketSummaryDTO> tickets = ticketService.getTicketsByAgentId(agentId, pageable);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/unassigned")
    public ResponseEntity<Page<TicketSummaryDTO>> getUnassignedTickets(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        log.info("REST request to get unassigned tickets");
        Page<TicketSummaryDTO> tickets = ticketService.getUnassignedTickets(pageable);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/status/{statusName}")
    public ResponseEntity<Page<TicketSummaryDTO>> getTicketsByStatus(
            @PathVariable String statusName,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        log.info("REST request to get tickets with status: {}", statusName);
        Page<TicketSummaryDTO> tickets = ticketService.getTicketsByStatus(statusName, pageable);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<TicketSummaryDTO>> searchTickets(
            @RequestParam("q") String searchTerm,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        log.info("REST request to search tickets with term: {}", searchTerm);
        Page<TicketSummaryDTO> tickets = ticketService.searchTickets(searchTerm, pageable);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/priority/{level}")
    public ResponseEntity<Page<TicketSummaryDTO>> getTicketsByPriorityLevel(
            @PathVariable Integer level,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        log.info("REST request to get tickets with priority level: {}", level);
        Page<TicketSummaryDTO> tickets = ticketService.getTicketsByPriorityLevel(level, pageable);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/category/{categoryName}")
    public ResponseEntity<Page<TicketSummaryDTO>> getTicketsByCategoryName(
            @PathVariable String categoryName,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        log.info("REST request to get tickets with category: {}", categoryName);
        Page<TicketSummaryDTO> tickets = ticketService.getTicketsByCategoryName(categoryName, pageable);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/date-range")
    public ResponseEntity<Page<TicketSummaryDTO>> getTicketsByDateRange(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        log.info("REST request to get tickets between {} and {}", startDate, endDate);
        Page<TicketSummaryDTO> tickets = ticketService.getTicketsByDateRange(startDate, endDate, pageable);
        return ResponseEntity.ok(tickets);
    }
}
