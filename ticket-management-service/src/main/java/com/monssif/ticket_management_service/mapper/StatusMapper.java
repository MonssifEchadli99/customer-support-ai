package com.monssif.ticket_management_service.mapper;

import com.monssif.ticket_management_service.dto.StatusDTO;
import com.monssif.ticket_management_service.entity.Status;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StatusMapper {

    @Mapping(target = "ticketCount", ignore = true)
    StatusDTO toDTO(Status status);
}