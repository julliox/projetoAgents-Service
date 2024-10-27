package br.com.octopus.undergroundFiber.repository;

import br.com.octopus.undergroundFiber.entity.TipoTurnoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TipoTurnoRepository extends JpaRepository<TipoTurnoEntity, Long> {

    Optional<TipoTurnoEntity> findByDescricao(String descricao);

    boolean existsByDescricao(String descricao);

}
