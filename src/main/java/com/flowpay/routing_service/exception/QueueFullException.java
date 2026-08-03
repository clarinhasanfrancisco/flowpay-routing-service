package com.flowpay.routing_service.exception;

public class QueueFullException extends RuntimeException {
    public QueueFullException() {
        super("Estamos com um alto volume de atendimentos no momento. Tente novamente mais tarde!");
    }

    public QueueFullException(String message) {
        super(message);
    }
}