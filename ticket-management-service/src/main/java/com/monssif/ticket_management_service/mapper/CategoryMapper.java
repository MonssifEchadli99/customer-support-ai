package com.monssif.ticket_management_service.mapper;

import com.monssif.ticket_management_service.dto.CategoryDTO;
import com.monssif.ticket_management_service.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {

    @Mapping(target = "ticketCount", ignore = true)
    CategoryDTO toDTO(Category category);
}