package com.monssif.ticket_management_service.controller;

import com.monssif.ticket_management_service.dto.CommentRequestDTO;
import com.monssif.ticket_management_service.dto.CommentResponseDTO;
import com.monssif.ticket_management_service.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/ticket/{ticketId}")
    public ResponseEntity<CommentResponseDTO> addComment(
            @PathVariable Long ticketId,
            @Valid @RequestBody CommentRequestDTO requestDTO) {
        log.info("REST request to add comment to ticket {}", ticketId);
        CommentResponseDTO createdComment = commentService.addComment(ticketId, requestDTO);
        return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentResponseDTO> getCommentById(@PathVariable Long id) {
        log.info("REST request to get comment by ID: {}", id);
        CommentResponseDTO comment = commentService.getCommentById(id);
        return ResponseEntity.ok(comment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        log.info("REST request to delete comment with ID: {}", id);
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<Page<CommentResponseDTO>> getCommentsByTicketId(
            @PathVariable Long ticketId,
            @PageableDefault(size = 50, sort = "createdAt") Pageable pageable) {
        log.info("REST request to get all comments for ticket {}", ticketId);
        Page<CommentResponseDTO> comments = commentService.getCommentsByTicketId(ticketId, pageable);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/ticket/{ticketId}/public")
    public ResponseEntity<Page<CommentResponseDTO>> getPublicCommentsByTicketId(
            @PathVariable Long ticketId,
            @PageableDefault(size = 50, sort = "createdAt") Pageable pageable) {
        log.info("REST request to get public comments for ticket {}", ticketId);
        Page<CommentResponseDTO> comments = commentService.getPublicCommentsByTicketId(ticketId, pageable);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/ticket/{ticketId}/internal")
    public ResponseEntity<Page<CommentResponseDTO>> getInternalCommentsByTicketId(
            @PathVariable Long ticketId,
            @PageableDefault(size = 50, sort = "createdAt") Pageable pageable) {
        log.info("REST request to get internal comments for ticket {}", ticketId);
        Page<CommentResponseDTO> comments = commentService.getInternalCommentsByTicketId(ticketId, pageable);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<CommentResponseDTO>> getCommentsByUserId(
            @PathVariable Long userId,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        log.info("REST request to get comments by user {}", userId);
        Page<CommentResponseDTO> comments = commentService.getCommentsByUserId(userId, pageable);
        return ResponseEntity.ok(comments);
    }
}
