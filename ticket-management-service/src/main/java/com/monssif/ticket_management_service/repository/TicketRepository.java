package com.monssif.ticket_management_service.repository;

import com.monssif.ticket_management_service.entity.Ticket;
import com.monssif.ticket_management_service.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;


@Repository
public interface TicketRepository extends JpaRepository<Ticket,Long> {

    Page<Ticket> findByCustomer(User customer, Pageable pageable);

    Page<Ticket> findByAssignedAgent(User assignedAgent, Pageable pageable);

    @Query("SELECT t FROM Ticket t WHERE LOWER(t.status.name) = LOWER(:statusName)")
    Page<Ticket> findByStatusName(@Param("statusName") String statusName, Pageable pageable);

    @Query("SELECT t FROM Ticket t WHERE t.priority.level = :priorityLevel")
    Page<Ticket> findByPriorityLevel(@Param("priorityLevel") Integer priorityLevel, Pageable pageable);

    @Query("SELECT t FROM Ticket t WHERE LOWER(t.category.name) = LOWER(:categoryName)")
    Page<Ticket> findByCategoryName(@Param("categoryName") String categoryName, Pageable pageable);

    @Query("SELECT t FROM Ticket t WHERE t.assignedAgent IS NULL")
    Page<Ticket> findUnassignedTickets(Pageable pageable);

    @Query("SELECT t FROM Ticket t WHERE t.resolvedAt IS NOT NULL")
    Page<Ticket> findResolvedTickets(Pageable pageable);

    @Query("SELECT t FROM Ticket t WHERE t.createdAt BETWEEN :startDate AND :endDate")
    Page<Ticket> findByCreatedAtBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    @Query("SELECT t FROM Ticket t " +
            "WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :searchTerm, '%'))" +
            "OR LOWER(t.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Ticket> searchByTitleOrDescription(@Param("searchTerm") String searchTerm, Pageable pageable);


    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.status.name = :statusName")
    Long countByStatusName(@Param("statusName") String statusName);


    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.assignedAgent.id = :agentId")
    Long countByAssignedAgentId(@Param("agentId") Long agentId);

    @Query("SELECT t FROM Ticket t WHERE t.aiSentimentScore < 0 ORDER BY t.aiSentimentScore ASC")
    Page<Ticket> findTicketsWithNegativeSentiment(Pageable pageable);

    @Query("SELECT t FROM Ticket t WHERE t.aiSuggestedCategory IS NOT NULL AND t.category != t.aiSuggestedCategory")
    Page<Ticket> findTicketsWithDifferentAISuggestedCategory(Pageable pageable);
}
