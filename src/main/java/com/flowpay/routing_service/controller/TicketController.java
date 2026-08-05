package com.flowpay.routing_service.controller;

import com.flowpay.routing_service.dto.TicketRequestDTO;
import com.flowpay.routing_service.model.Ticket;
import com.flowpay.routing_service.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
public class TicketController {
    private final TicketService ticketService;

    @PostMapping
    public ResponseEntity<Ticket> createTicket(@RequestBody TicketRequestDTO request) {
        Ticket createdTicket = ticketService.createTicket(request.getChatReference(), request.getSubject());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTicket);
    }
}
