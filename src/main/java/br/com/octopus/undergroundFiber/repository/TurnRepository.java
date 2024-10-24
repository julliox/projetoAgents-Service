package br.com.octopus.undergroundFiber.repository;

import br.com.octopus.undergroundFiber.entity.AgentEntity;
import br.com.octopus.undergroundFiber.entity.TurnEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TurnRepository extends JpaRepository<TurnEntity, Long> {

    List<TurnEntity> findAllByClientId(Long id);

    Optional<TurnEntity> findByClientAndDataTurno(AgentEntity client, LocalDate dataTurno);
    List<TurnEntity> findByClientAndDataTurnoIn(AgentEntity client, List<LocalDate> dataTurno);

}
