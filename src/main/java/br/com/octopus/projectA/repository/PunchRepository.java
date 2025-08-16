package br.com.octopus.projectA.repository;

import br.com.octopus.projectA.entity.PunchEntity;
import br.com.octopus.projectA.entity.enuns.PunchType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PunchRepository extends JpaRepository<PunchEntity, UUID> {

    Optional<PunchEntity> findTopByAgentIdOrderByTimestampServerDesc(Long agentId);

    Page<PunchEntity> findByAgentIdAndTimestampServerBetween(Long agentId, Instant from, Instant to, Pageable pageable);

    Page<PunchEntity> findByAgentId(Long agentId, Pageable pageable);

    Optional<PunchEntity> findByIdempotencyKey(String idempotencyKey);

    Optional<PunchEntity> findTopByAgentIdAndTypeOrderByTimestampServerDesc(Long agentId, PunchType type);
}


