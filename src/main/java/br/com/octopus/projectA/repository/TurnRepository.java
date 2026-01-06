package br.com.octopus.projectA.repository;

import br.com.octopus.projectA.entity.EmployeeEntity;
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

    List<TurnEntity> findAllByEmployeeId(Long employeeId);

    Optional<TurnEntity> findByEmployeeAndDataTurno(EmployeeEntity employee, LocalDate dataTurno);
    List<TurnEntity> findByEmployeeAndDataTurnoIn(EmployeeEntity employee, List<LocalDate> dataTurno);
    List<TurnEntity> findByDataTurnoBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Busca os turnos de um colaborador específico dentro de um intervalo de datas.
     *
     * @param employeeId ID do colaborador.
     * @param start   Data e hora de início.
     * @param end     Data e hora de término.
     * @return Lista de TurnEntity correspondentes.
     */
    List<TurnEntity> findByEmployeeIdAndDataTurnoBetween(Long employeeId, LocalDate start, LocalDate end);



    @Query(value = "SELECT " +
            "e.ID AS employeeId, " +
            "u.NAME AS employeeName, " +
            "u.EMAIL, " +
            "e.PHONE_NUMBER, " +
            "e.DES_INFO, " +
            "e.ADMISSION_DATE, " +
            "e.STATUS AS status, " +
            "t.ID AS turnId, " +
            "t.DATE AS turnDate, " +
            "tt.DESCRICAO AS turnDescription, " +
            "CASE " +
            "   WHEN e.ADMISSION_DATE <= :oneYearAgo THEN tt.VALOR_SENIOR " +
            "   ELSE tt.VALOR_JUNIOR " +
            "END AS turnValue " +
            "FROM TBL_EMPLOYEE e " +
            "JOIN TBL_USER u ON e.USER_ID = u.ID " +
            "JOIN TBL_TURN t ON t.EMPLOYEE_ID = e.ID " +
            "JOIN TBL_TIPO_TURNO tt ON t.TIPO_TURNO_ID = tt.ID " +
            "WHERE e.ID = :employeeId " +
            "AND t.DATE BETWEEN :startOfMonth AND :endOfMonth", nativeQuery = true)
    List<Object[]> findPaymentSlipsByEmployeeAndMonthNative(
            @Param("employeeId") Long employeeId,
            @Param("oneYearAgo") LocalDate oneYearAgo,
            @Param("startOfMonth") LocalDate startOfMonth,
            @Param("endOfMonth") LocalDate endOfMonth
    );

}