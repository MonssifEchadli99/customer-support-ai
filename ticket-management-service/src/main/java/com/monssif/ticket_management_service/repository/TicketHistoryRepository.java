package com.monssif.ticket_management_service.repository;

import com.monssif.ticket_management_service.entity.TicketHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketHistoryRepository extends JpaRepository<TicketHistory,Long> {
}
