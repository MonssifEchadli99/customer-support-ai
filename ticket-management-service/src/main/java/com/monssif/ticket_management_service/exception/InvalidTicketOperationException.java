package com.monssif.ticket_management_service.exception;

public class InvalidTicketOperationException extends RuntimeException{
    public InvalidTicketOperationException(String message){
        super(message);
    }
}
