package com.monssif.ticket_management_service.repository;

import com.monssif.ticket_management_service.entity.Priority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface PriorityRepository extends JpaRepository<Priority, Long> {

    Optional<Priority> findByName(String name);


    Optional<Priority> findByLevel(Integer level);

    @Query("SELECT p FROM Priority p ORDER BY p.level ASC")
    List<Priority> findAllOrderedByLevel();

    boolean existsByName(String name);

    boolean existsByLevel(Integer level);


    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.priority.id = :priorityId")
    Long countTicketsByPriorityId(@Param("priorityId") Long priorityId);
}