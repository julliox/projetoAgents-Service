package br.com.octopus.undergroundFiber.repository;

import br.com.octopus.undergroundFiber.entity.AgentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgentRepository extends JpaRepository<AgentEntity, Long> {
}
