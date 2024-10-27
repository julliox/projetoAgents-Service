package br.com.octopus.undergroundFiber.repository;

import br.com.octopus.undergroundFiber.entity.AgentEntity;
import br.com.octopus.undergroundFiber.entity.TurnEntity;
import org.springframework.data.jpa.repository.JpaRepository;
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

}