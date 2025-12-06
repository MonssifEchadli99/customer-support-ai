package com.monssif.ticket_management_service.mapper;

import com.monssif.ticket_management_service.dto.CommentResponseDTO;
import com.monssif.ticket_management_service.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    @Mapping(source = "ticket.id", target = "ticketId")
    @Mapping(source = "ticket.title", target = "ticketTitle")

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.fullName", target = "userName")
    @Mapping(source = "user.email", target = "userEmail")
    @Mapping(source = "user.role", target = "userRole")

    @Mapping(target = "isPublic", expression = "java(comment.isPublic())")
    @Mapping(target = "visibility", expression = "java(comment.getIsInternal() ? \"Internal (Agents Only)\" : \"Public\")")
    CommentResponseDTO toResponseDTO(Comment comment);
}