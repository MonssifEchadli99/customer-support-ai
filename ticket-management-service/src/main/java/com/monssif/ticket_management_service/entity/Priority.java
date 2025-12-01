package com.monssif.ticket_management_service.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "priorities", uniqueConstraints = {
        @UniqueConstraint(name = "uk_priorities_name", columnNames = "name"),
        @UniqueConstraint(name = "uk_priorities_level", columnNames = "level")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Priority {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "priority_seq")
    @SequenceGenerator(name = "priority_seq", sequenceName = "priority_sequence", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @Column(name = "priority_level", nullable = false, unique = true)
    private Integer level;

    @Column(name = "description", length = 255)
    private String description;

    @OneToMany(mappedBy = "priority", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Ticket> tickets = new ArrayList<>();
}