package com.flowpay.routing_service.seeder;

import com.flowpay.routing_service.model.Agent;
import com.flowpay.routing_service.model.Team;
import com.flowpay.routing_service.repository.AgentRepository;
import com.flowpay.routing_service.repository.TeamRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class DataBaseSeeder implements CommandLineRunner {
    private final TeamRepository teamRepository;
    private final AgentRepository agentRepository;

    @Override
    public void run(String... args) throws Exception {
        if (teamRepository.count() == 0) {

            Team cardsTeam = teamRepository.save(new Team(null, "CARDS"));
            Team loansTeam = teamRepository.save(new Team(null, "LOANS"));
            Team otherTeam = teamRepository.save(new Team(null, "OTHER"));

            List<Agent> initialAgents = List.of(
                    // Time CARDS
                    new Agent(null, "Camila Silva", cardsTeam, 0),
                    new Agent(null, "Carlos Eduardo", cardsTeam, 0),
                    new Agent(null, "César Augusto", cardsTeam, 0),

                    // Time LOANS
                    new Agent(null, "Lucas Mendes", loansTeam, 0),
                    new Agent(null, "Lívia Rocha", loansTeam, 0),
                    new Agent(null, "Leonardo Ramos", loansTeam, 0),

                    // Time OTHER
                    new Agent(null, "Otávio Faria", otherTeam, 0),
                    new Agent(null, "Olívia Castro", otherTeam, 0),
                    new Agent(null, "Orlando Costa", otherTeam, 0)
            );

            agentRepository.saveAll(initialAgents);

            System.out.println("Carga inicial de dados concluída com sucesso! Times e agentes padrão cadastrados.");
        } else {
            System.out.println("Banco de dados já possui registros. Carga inicial ignorada.");
        }
    }

}
