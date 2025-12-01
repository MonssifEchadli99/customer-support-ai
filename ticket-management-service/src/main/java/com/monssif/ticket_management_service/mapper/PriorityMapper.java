package com.monssif.ticket_management_service.mapper;

import com.monssif.ticket_management_service.dto.PriorityDTO;
import com.monssif.ticket_management_service.entity.Priority;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PriorityMapper {

    @Mapping(target = "ticketCount", ignore = true)
    PriorityDTO toDTO(Priority priority);
}