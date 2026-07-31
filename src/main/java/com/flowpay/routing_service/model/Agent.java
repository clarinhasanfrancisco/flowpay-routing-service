package com.flowpay.routing_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "agents")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Agent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @ManyToOne(optional = false)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(name = "current_workload", nullable = false)
    private Integer currentWorkload = 0;
}
