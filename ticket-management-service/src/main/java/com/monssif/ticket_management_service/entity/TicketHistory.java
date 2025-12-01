package com.monssif.ticket_management_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "ticket_history", indexes = {
        @Index(name = "idx_ticket_history_ticket_id", columnList = "ticket_id"),
        @Index(name = "idx_ticket_history_changed_at", columnList = "changed_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ticket_history_seq")
    @SequenceGenerator(name = "ticket_history_seq", sequenceName = "ticket_history_sequence", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "field_name", nullable = false, length = 100)
    private String fieldName;

    @Column(name = "old_value", columnDefinition = "CLOB")
    private String oldValue;

    @Column(name = "new_value", columnDefinition = "CLOB")
    private String newValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by", nullable = false)
    private User changedBy;

    @CreationTimestamp
    @Column(name = "changed_at", nullable = false, updatable = false)
    private LocalDateTime changedAt;
}