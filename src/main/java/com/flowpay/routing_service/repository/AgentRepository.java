package com.flowpay.routing_service.repository;

import com.flowpay.routing_service.model.Agent;
import com.flowpay.routing_service.model.Team;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgentRepository extends JpaRepository<Agent, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Agent a WHERE a.team = :team AND a.currentWorkload < 3 ORDER BY a.currentWorkload ASC")
    List<Agent> findAvailableAgentsByTeamWithLock(@Param("team") Team team);
}