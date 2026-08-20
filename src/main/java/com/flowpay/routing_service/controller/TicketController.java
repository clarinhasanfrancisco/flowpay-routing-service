package com.flowpay.routing_service.controller;

import com.flowpay.routing_service.dto.TicketRequestDTO;
import com.flowpay.routing_service.model.Ticket;
import com.flowpay.routing_service.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
public class TicketController {
    private final TicketService ticketService;

    @PostMapping
    public ResponseEntity<Ticket> createTicket(@Valid @RequestBody TicketRequestDTO request) {
        Ticket createdTicket = ticketService.createTicket(request.chatReference(), request.subject());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTicket);
    }

    @PostMapping("/{chatReference}/finish")
    public ResponseEntity<Ticket> finishTicket(@PathVariable Long chatReference) {
        Ticket finishedTicket = ticketService.finishTicket(chatReference);
        return ResponseEntity.ok(finishedTicket);
    }
}
