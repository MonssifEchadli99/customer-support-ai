package com.monssif.ticket_management_service.dto;

import com.monssif.ticket_management_service.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {

    private Long id;
    private String username;
    private String email;
    private String fullName;
    private UserRole role;
    private Boolean isActive;
    private LocalDateTime createdAt;

    private Long assignedTicketsCount;
    private Long createdTicketsCount;
    private Long commentsCount;
}