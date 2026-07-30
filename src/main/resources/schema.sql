CREATE TABLE IF NOT EXISTS teams (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS agents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    team_id BIGINT NOT NULL,
    current_workload INT NOT NULL DEFAULT 0,
    CONSTRAINT fk_agents_team FOREIGN KEY (team_id) REFERENCES teams(id),
    CONSTRAINT chk_workload CHECK (current_workload >= 0 AND current_workload <= 3)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS tickets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    chat_reference BIGINT NOT NULL,
    subject VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    team_id BIGINT NOT NULL,
    agent_id BIGINT NULL,
    created_at DATETIME(6) NOT NULL,
    CONSTRAINT fk_tickets_team FOREIGN KEY (team_id) REFERENCES teams(id),
    CONSTRAINT fk_tickets_agent FOREIGN KEY (agent_id) REFERENCES agents(id)
) ENGINE=InnoDB;