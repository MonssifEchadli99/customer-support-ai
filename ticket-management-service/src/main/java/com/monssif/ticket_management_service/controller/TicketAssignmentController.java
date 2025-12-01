package com.monssif.ticket_management_service.controller;

import com.monssif.ticket_management_service.dto.AssignmentRequestDTO;
import com.monssif.ticket_management_service.dto.AssignmentResponseDTO;
import com.monssif.ticket_management_service.service.TicketAssignmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tickets/assignments")
@RequiredArgsConstructor
@Slf4j
public class TicketAssignmentController {

    private final TicketAssignmentService assignmentService;

    @PostMapping("/{ticketId}/assign")
    public ResponseEntity<AssignmentResponseDTO> assignTicket(
            @PathVariable Long ticketId,
            @Valid @RequestBody AssignmentRequestDTO requestDTO) {
        log.info("REST request to assign ticket {} to agent {}", ticketId, requestDTO.getAgentId());
        AssignmentResponseDTO response = assignmentService.assignTicket(ticketId, requestDTO);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{ticketId}/reassign")
    public ResponseEntity<AssignmentResponseDTO> reassignTicket(
            @PathVariable Long ticketId,
            @Valid @RequestBody AssignmentRequestDTO requestDTO) {
        log.info("REST request to reassign ticket {} to agent {}", ticketId, requestDTO.getAgentId());
        AssignmentResponseDTO response = assignmentService.reassignTicket(ticketId, requestDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{ticketId}/unassign")
    public ResponseEntity<AssignmentResponseDTO> unassignTicket(
            @PathVariable Long ticketId,
            @RequestParam(required = false) String note) {
        log.info("REST request to unassign ticket {}", ticketId);
        AssignmentResponseDTO response = assignmentService.unassignTicket(ticketId, note);
        return ResponseEntity.ok(response);
    }
}