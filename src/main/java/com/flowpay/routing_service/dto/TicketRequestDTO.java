package com.flowpay.routing_service.dto;

import lombok.*;

@Getter
@Setter
public class TicketRequestDTO {
    private Long chatReference;
    private String subject;
}
