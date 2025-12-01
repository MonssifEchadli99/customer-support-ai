package com.monssif.ticket_management_service.repository;

import com.monssif.ticket_management_service.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StatusRepository extends JpaRepository<Status, Long> {

    Optional<Status> findByName(String name);


    @Query("SELECT s FROM Status s WHERE s.isFinal = true")
    List<Status> findAllFinalStatuses();


    @Query("SELECT s FROM Status s WHERE s.isFinal = false")
    List<Status> findAllActiveStatuses();


    boolean existsByName(String name);


    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.status.id = :statusId")
    Long countTicketsByStatusId(@Param("statusId") Long statusId);
}
