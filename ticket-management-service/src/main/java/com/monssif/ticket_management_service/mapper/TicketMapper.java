package com.monssif.ticket_management_service.mapper;

import com.monssif.ticket_management_service.dto.TicketResponseDTO;
import com.monssif.ticket_management_service.dto.TicketSummaryDTO;
import com.monssif.ticket_management_service.entity.Ticket;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.WARN)
public interface TicketMapper {

    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "customer.fullName", target = "customerName")
    @Mapping(source = "customer.email", target = "customerEmail")
    @Mapping(source = "assignedAgent.id", target = "assignedAgentId")
    @Mapping(source = "assignedAgent.fullName", target = "assignedAgentName")
    @Mapping(source = "assignedAgent.email", target = "assignedAgentEmail")
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "priority.id", target = "priorityId")
    @Mapping(source = "priority.name", target = "priorityName")
    @Mapping(source = "priority.level", target = "priorityLevel")
    @Mapping(source = "status.id", target = "statusId")
    @Mapping(source = "status.name", target = "statusName")
    @Mapping(source = "status.isFinal", target = "isStatusFinal")
    @Mapping(source = "aiSuggestedCategory.id", target = "aiSuggestedCategoryId")
    @Mapping(source = "aiSuggestedCategory.name", target = "aiSuggestedCategoryName")
    @Mapping(target = "isAssigned", expression = "java(ticket.isAssigned())")
    @Mapping(target = "isResolved", expression = "java(ticket.isResolved())")
    @Mapping(target = "commentsCount", expression = "java(ticket.getComments() != null ? ticket.getComments().size() : 0)")
    TicketResponseDTO toResponseDTO(Ticket ticket);

    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "customer.fullName", target = "customerName")
    @Mapping(source = "assignedAgent.id", target = "assignedAgentId")
    @Mapping(source = "assignedAgent.fullName", target = "assignedAgentName")
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "priority.name", target = "priorityName")
    @Mapping(source = "priority.level", target = "priorityLevel")
    @Mapping(source = "status.name", target = "statusName")
    @Mapping(target = "isAssigned", expression = "java(ticket.isAssigned())")
    @Mapping(target = "isResolved", expression = "java(ticket.isResolved())")
    TicketSummaryDTO toSummaryDTO(Ticket ticket);
}
