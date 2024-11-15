package br.com.octopus.projectA.repository;

import br.com.octopus.projectA.entity.AgentEntity;
import br.com.octopus.projectA.entity.TurnEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TurnRepository extends JpaRepository<TurnEntity, Long> {

    List<TurnEntity> findAllByAgentId(Long id);

    Optional<TurnEntity> findByAgentAndDataTurno(AgentEntity agent, LocalDate dataTurno);
    List<TurnEntity> findByAgentAndDataTurnoIn(AgentEntity agent, List<LocalDate> dataTurno);
    List<TurnEntity> findByDataTurnoBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Busca os turnos de um agente específico dentro de um intervalo de datas.
     *
     * @param agentId ID do agente.
     * @param start   Data e hora de início.
     * @param end     Data e hora de término.
     * @return Lista de TurnEntity correspondentes.
     */
    List<TurnEntity> findByAgentIdAndDataTurnoBetween(Long agentId, LocalDate start, LocalDate end);



    @Query(value = "SELECT " +
            "a.ID AS agentId, " +
            "a.NAME AS agentName, " +
            "a.EMAIL, " +
            "a.PHONE_NUMBER, " +
            "a.DES_INFO, " +
            "a.ADMISSION_DATE, " +
            "a.STATUS AS status, " +
            "t.ID AS turnId, " +
            "t.DATE AS turnDate, " +
            "tt.DESCRICAO AS turnDescription, " +
            "CASE " +
            "   WHEN a.ADMISSION_DATE <= :oneYearAgo THEN tt.VALOR_SENIOR " +
            "   ELSE tt.VALOR_JUNIOR " +
            "END AS turnValue " +
            "FROM TBL_AGENT a " +
            "JOIN TBL_TURN t ON t.AGENT_ID = a.ID " +
            "JOIN TBL_TIPO_TURNO tt ON t.TIPO_TURNO_ID = tt.ID " +
            "WHERE a.ID = :agentId " +
            "AND t.DATE BETWEEN :startOfMonth AND :endOfMonth", nativeQuery = true)
    List<Object[]> findPaymentSlipsByAgentAndMonthNative(
            @Param("agentId") Long agentId,
            @Param("oneYearAgo") LocalDate oneYearAgo,
            @Param("startOfMonth") LocalDate startOfMonth,
            @Param("endOfMonth") LocalDate endOfMonth
    );

}