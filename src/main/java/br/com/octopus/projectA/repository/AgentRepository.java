package br.com.octopus.projectA.repository;

import br.com.octopus.projectA.entity.AgentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgentRepository extends JpaRepository<AgentEntity, Long> {
}
