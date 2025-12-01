package com.monssif.ticket_management_service.service;

import com.monssif.ticket_management_service.dto.CommentRequestDTO;
import com.monssif.ticket_management_service.dto.CommentResponseDTO;
import com.monssif.ticket_management_service.entity.Comment;
import com.monssif.ticket_management_service.entity.Ticket;
import com.monssif.ticket_management_service.entity.User;
import com.monssif.ticket_management_service.enums.EventType;
import com.monssif.ticket_management_service.events.CommentCreatedEvent;
import com.monssif.ticket_management_service.exception.CommentNotFoundException;
import com.monssif.ticket_management_service.exception.InvalidTicketOperationException;
import com.monssif.ticket_management_service.exception.TicketNotFoundException;
import com.monssif.ticket_management_service.mapper.CommentMapper;
import com.monssif.ticket_management_service.messaging.AIAnalysisProducer;
import com.monssif.ticket_management_service.repository.CommentRepository;
import com.monssif.ticket_management_service.repository.TicketRepository;
import com.monssif.ticket_management_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;
    private final AIAnalysisProducer producer;

    @Transactional
    public CommentResponseDTO addComment(Long ticketId, CommentRequestDTO requestDTO) {
        log.info("Adding comment to ticket {}", ticketId);

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));

        User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new InvalidTicketOperationException(
                        "User not found with ID: " + requestDTO.getUserId()));

        if (!user.getIsActive()) {
            throw new InvalidTicketOperationException("User account is not active");
        }

        if (requestDTO.getIsInternal() && user.isCustomer()) {
            throw new InvalidTicketOperationException(
                    "Customers cannot create internal comments. Only agents and admins can create internal notes.");
        }

        Comment comment = Comment.builder()
                .content(requestDTO.getContent())
                .isInternal(requestDTO.getIsInternal())
                .ticket(ticket)
                .user(user)
                .build();

        Comment savedComment = commentRepository.save(comment);
        log.info("Comment created with ID: {} on ticket {}", savedComment.getId(), ticketId);

        CommentCreatedEvent event = CommentCreatedEvent.builder()
                .commentId(savedComment.getId())
                .ticketId(ticketId)
                .content(savedComment.getContent())
                .isInternal(savedComment.getIsInternal())
                .userId(savedComment.getUser().getId())
                .createdAt(savedComment.getCreatedAt())
                .eventType(EventType.COMMENT_CREATED.name())
                .build();

        producer.publishCommentCreated(event);

        return commentMapper.toResponseDTO(savedComment);
    }


    public CommentResponseDTO getCommentById(Long commentId) {
        log.info("Fetching comment with ID: {}", commentId);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));

        return commentMapper.toResponseDTO(comment);
    }

    public Page<CommentResponseDTO> getCommentsByTicketId(Long ticketId, Pageable pageable) {
        log.info("Fetching all comments for ticket {}", ticketId);

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));

        Page<Comment> commentsPage = commentRepository.findByTicket(ticket, pageable);
        return commentsPage.map(commentMapper::toResponseDTO);
    }


    public Page<CommentResponseDTO> getPublicCommentsByTicketId(Long ticketId, Pageable pageable) {
        log.info("Fetching public comments for ticket {}", ticketId);

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));

        Page<Comment> commentsPage = commentRepository.findPublicCommentsByTicket(ticket, pageable);
        return commentsPage.map(commentMapper::toResponseDTO);
    }


    public Page<CommentResponseDTO> getInternalCommentsByTicketId(Long ticketId, Pageable pageable) {
        log.info("Fetching internal comments for ticket {}", ticketId);

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));

        Page<Comment> commentsPage = commentRepository.findInternalCommentsByTicket(ticket, pageable);
        return commentsPage.map(commentMapper::toResponseDTO);
    }

    public Page<CommentResponseDTO> getCommentsByUserId(Long userId, Pageable pageable) {
        log.info("Fetching comments by user {}", userId);

        userRepository.findById(userId)
                .orElseThrow(() -> new InvalidTicketOperationException(
                        "User not found with ID: " + userId));

        Page<Comment> commentsPage = commentRepository.findByUserId(userId, pageable);
        return commentsPage.map(commentMapper::toResponseDTO);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        log.info("Deleting comment with ID: {}", commentId);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));

        commentRepository.delete(comment);
        log.info("Comment deleted successfully with ID: {}", commentId);
    }


    @Transactional
    public void updateCommentWithAISentiment(Long commentId, Double sentimentScore) {
        log.info("Updating comment {} with AI sentiment: {}", commentId, sentimentScore);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));

        if (sentimentScore < -1.0 || sentimentScore > 1.0) {
            log.warn("Invalid sentiment score {} for comment {}. Must be between -1.0 and 1.0",
                    sentimentScore, commentId);
            throw new InvalidTicketOperationException(
                    "Sentiment score must be between -1.0 and 1.0");
        }

        comment.setAiSentimentScore(sentimentScore);
        commentRepository.save(comment);

        log.info("Successfully updated comment {} with AI sentiment score", commentId);
    }
}