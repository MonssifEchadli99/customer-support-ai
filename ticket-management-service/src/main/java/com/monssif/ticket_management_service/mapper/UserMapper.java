package com.monssif.ticket_management_service.mapper;

import com.monssif.ticket_management_service.dto.UserResponseDTO;
import com.monssif.ticket_management_service.dto.UserSummaryDTO;
import com.monssif.ticket_management_service.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "assignedTicketsCount", ignore = true)
    @Mapping(target = "createdTicketsCount", ignore = true)
    @Mapping(target = "commentsCount", ignore = true)
    UserResponseDTO toResponseDTO(User user);

    UserSummaryDTO toSummaryDTO(User user);
}