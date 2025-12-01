package com.monssif.ticket_management_service.repository;

import com.monssif.ticket_management_service.entity.Comment;
import com.monssif.ticket_management_service.entity.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.ticket = :ticket ORDER BY c.createdAt ASC")
    Page<Comment> findByTicket(@Param("ticket") Ticket ticket, Pageable pageable);

    @Query("SELECT c FROM Comment c WHERE c.ticket = :ticket AND c.isInternal = false ORDER BY c.createdAt ASC")
    Page<Comment> findPublicCommentsByTicket(@Param("ticket") Ticket ticket, Pageable pageable);

    @Query("SELECT c FROM Comment c WHERE c.ticket = :ticket AND c.isInternal = true ORDER BY c.createdAt ASC")
    Page<Comment> findInternalCommentsByTicket(@Param("ticket") Ticket ticket, Pageable pageable);


    @Query("SELECT c FROM Comment c WHERE c.user.id = :userId ORDER BY c.createdAt DESC")
    Page<Comment> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.ticket.id = :ticketId")
    Long countByTicketId(@Param("ticketId") Long ticketId);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.ticket.id = :ticketId AND c.isInternal = false")
    Long countPublicCommentsByTicketId(@Param("ticketId") Long ticketId);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.ticket.id = :ticketId AND c.isInternal = true")
    Long countInternalCommentsByTicketId(@Param("ticketId") Long ticketId);

    @Query("SELECT c FROM Comment c WHERE c.aiSentimentScore < 0 ORDER BY c.aiSentimentScore ASC")
    Page<Comment> findCommentsWithNegativeSentiment(Pageable pageable);
}