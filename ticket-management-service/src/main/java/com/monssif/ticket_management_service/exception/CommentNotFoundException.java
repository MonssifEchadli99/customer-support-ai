package com.monssif.ticket_management_service.exception;

public class CommentNotFoundException extends RuntimeException {

    public CommentNotFoundException(String message) {
        super(message);
    }

    public CommentNotFoundException(Long commentId) {
        super("Comment not found with ID: " + commentId);
    }
}