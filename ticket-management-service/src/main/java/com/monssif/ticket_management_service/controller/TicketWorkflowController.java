package com.monssif.ticket_management_service.controller;

import com.monssif.ticket_management_service.dto.WorkflowTransitionRequestDTO;
import com.monssif.ticket_management_service.dto.WorkflowTransitionResponseDTO;
import com.monssif.ticket_management_service.service.TicketWorkflowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;


@RestController
@RequestMapping("/tickets/workflow")
@RequiredArgsConstructor
@Slf4j
public class TicketWorkflowController {

    private final TicketWorkflowService workflowService;


    @PostMapping("/{ticketId}/transition")
    public ResponseEntity<WorkflowTransitionResponseDTO> transitionTicketStatus(
            @PathVariable Long ticketId,
            @Valid @RequestBody WorkflowTransitionRequestDTO requestDTO) {
        log.info("REST request to transition ticket {} to status {}", ticketId, requestDTO.getStatusId());
        WorkflowTransitionResponseDTO response = workflowService.transitionTicketStatus(ticketId, requestDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{ticketId}/available-transitions")
    public ResponseEntity<List<String>> getAvailableTransitions(@PathVariable Long ticketId) {
        log.info("REST request to get available transitions for ticket {}", ticketId);
        List<String> availableTransitions = workflowService.getAvailableTransitions(ticketId);
        return ResponseEntity.ok(availableTransitions);
    }

    @GetMapping("/{ticketId}/can-transition/{statusId}")
    public ResponseEntity<Map<String, Boolean>> isTransitionValid(
            @PathVariable Long ticketId,
            @PathVariable Long statusId) {
        log.info("REST request to check if ticket {} can transition to status {}", ticketId, statusId);
        boolean isValid = workflowService.isTransitionValid(ticketId, statusId);
        return ResponseEntity.ok(Map.of("canTransition", isValid));
    }


    @GetMapping("/definition")
    public ResponseEntity<Map<String, Set<String>>> getWorkflowDefinition() {
        log.info("REST request to get workflow definition");
        Map<String, Set<String>> workflowDefinition = workflowService.getWorkflowDefinition();
        return ResponseEntity.ok(workflowDefinition);
    }
}
