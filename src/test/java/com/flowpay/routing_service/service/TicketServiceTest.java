package com.flowpay.routing_service.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TicketServiceTest {

    // Como estamos testando apenas a conversão de texto, podemos instanciar o serviço de forma simples,
    // passando "null" para os repositórios (já que o método mapSubjectToTeamName não usa banco de dados).
    private final TicketService ticketService = new TicketService(null, null, null);

    @Test
    @DisplayName("Deve mapear corretamente os assuntos para o time correspondente")
    void shouldMapSubjectToCorrectTeamName() {

        // 1. Validando regras do time CARDS
        assertEquals("CARDS", ticketService.mapSubjectToTeamName("cartao"));
        assertEquals("CARDS", ticketService.mapSubjectToTeamName("problema com meu cartão"));

        // 2. Validando regras do time LOANS
        assertEquals("LOANS", ticketService.mapSubjectToTeamName("emprestimo"));
        assertEquals("LOANS", ticketService.mapSubjectToTeamName("quero um empréstimo"));

        // 3. Validando regras do time OTHER (qualquer outra coisa ou nulo)
        assertEquals("OTHER", ticketService.mapSubjectToTeamName("duvida sobre a conta"));
        assertEquals("OTHER", ticketService.mapSubjectToTeamName(null));
    }
}