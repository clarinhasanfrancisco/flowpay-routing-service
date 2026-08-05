package com.flowpay.routing_service.dto;

import jakarta.validation.constraints.*;

public record TicketRequestDTO (
        @NotNull
        Long chatReference,

        @NotBlank
        String subject
){}
