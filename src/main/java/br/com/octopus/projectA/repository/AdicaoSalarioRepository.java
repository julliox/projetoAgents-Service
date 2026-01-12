package br.com.octopus.projectA.repository;

import br.com.octopus.projectA.entity.AdicaoSalarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public interface AdicaoSalarioRepository extends JpaRepository<AdicaoSalarioEntity, Long> {

    Optional<AdicaoSalarioEntity> findByEmployeeIdAndTipoAdicaoIdAndMesAdicao(Long employeeId, Long tipoAdicaoId, YearMonth mesAdicao);

    List<AdicaoSalarioEntity> findAllByEmployeeIdAndMesAdicao(Long employeeId, YearMonth mesAdicao);

    List<AdicaoSalarioEntity> findAllByEmployeeId(Long employeeId);
}
