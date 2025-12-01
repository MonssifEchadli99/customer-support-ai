package com.monssif.ticket_management_service.dto;

import com.monssif.ticket_management_service.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryDTO {

    private Long id;
    private String username;
    private String email;
    private String fullName;
    private UserRole role;
    private Boolean isActive;
}