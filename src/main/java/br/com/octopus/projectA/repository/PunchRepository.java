package br.com.octopus.projectA.repository;

import br.com.octopus.projectA.entity.PunchEntity;
import br.com.octopus.projectA.entity.enuns.PunchType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PunchRepository extends JpaRepository<PunchEntity, UUID>, JpaSpecificationExecutor<PunchEntity> {

    Optional<PunchEntity> findTopByAgentIdOrderByTimestampServerDesc(Long agentId);

    Page<PunchEntity> findByAgentIdAndTimestampServerBetween(Long agentId, Instant from, Instant to, Pageable pageable);

    Page<PunchEntity> findByAgentId(Long agentId, Pageable pageable);

    Optional<PunchEntity> findByIdempotencyKey(String idempotencyKey);

    Optional<PunchEntity> findTopByAgentIdAndTypeOrderByTimestampServerDesc(Long agentId, PunchType type);

    /**
     * Busca todos os agentes que estão atualmente online (último registro é ENTRADA).
     * Retorna apenas os agentIds.
     * Usa uma query nativa mais eficiente para buscar agentes cujo último registro é ENTRADA.
     */
    @Query(value = "SELECT DISTINCT p1.AGENT_ID FROM TBL_PUNCH p1 " +
           "WHERE p1.TIMESTAMP_SERVER = (" +
           "    SELECT MAX(p2.TIMESTAMP_SERVER) " +
           "    FROM TBL_PUNCH p2 " +
           "    WHERE p2.AGENT_ID = p1.AGENT_ID" +
           ") " +
           "AND p1.TYPE = :punchType", nativeQuery = true)
    List<Long> findOnlineAgentIds(@Param("punchType") String punchType);

    /**
     * Busca histórico de mudanças de status com filtros opcionais.
     * Nota: Usar Specifications através do método no service para construir query dinamicamente.
     */
}


