package br.com.octopus.undergroundFiber.service;

import br.com.octopus.undergroundFiber.entity.AgentEntity;
import br.com.octopus.undergroundFiber.entity.TurnEntity;
import br.com.octopus.undergroundFiber.repository.AgentRepository;
import br.com.octopus.undergroundFiber.repository.TurnRepository;
import br.com.octopus.undergroundFiber.suport.dtos.SalarioDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SalarioService {

    @Autowired
    private AgentRepository agenteRepository;

    @Autowired
    private TurnRepository turnoRepository;

    /**
     * Calcula o salário de todos os agentes com base nos turnos do mês atual.
     *
     * @return Lista de SalarioDTO contendo o salário de cada agente.
     */
    public List<SalarioDTO> calcularSalariosDoMesAtual() {
        List<SalarioDTO> salarios = new ArrayList<>();

        // Determinar o primeiro e o último dia do mês atual
        YearMonth currentYearMonth = YearMonth.now();
        LocalDateTime startOfMonth = currentYearMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = currentYearMonth.atEndOfMonth().atTime(23, 59, 59);

        // Buscar todos os turnos do mês atual
        List<TurnEntity> turnosDoMes = turnoRepository.findByDataTurnoBetween(startOfMonth, endOfMonth);

        // Buscar todos os agentes
        List<AgentEntity> agentes = agenteRepository.findAll();

        for (AgentEntity agente : agentes) {
            // Calcular a diferença em anos entre a data de admissão e a data atual
            long anosNaEmpresa = ChronoUnit.YEARS.between(agente.getAdmissionDate(), LocalDate.now());

            // Determinar se o agente é Senior ou Junior
            boolean isSenior = anosNaEmpresa >= 1;

            // Filtrar os turnos do agente no mês atual
            List<TurnEntity> turnosDoAgente = new ArrayList<>();
            for (TurnEntity turno : turnosDoMes) {
                if (turno.getAgent().getId().equals(agente.getId())) {
                    turnosDoAgente.add(turno);
                }
            }

            // Calcular o salário
            BigDecimal salarioTotal = BigDecimal.ZERO;
            for (TurnEntity turno : turnosDoAgente) {
                if (isSenior) {
                    salarioTotal = salarioTotal.add(turno.getTipoTurno().getValorSenior());
                } else {
                    salarioTotal = salarioTotal.add(turno.getTipoTurno().getValorJunior());
                }
            }

            // Adicionar ao DTO
            SalarioDTO salarioDTO = new SalarioDTO(
                    agente.getId(),
                    agente.getName(),
                    salarioTotal
            );

            salarios.add(salarioDTO);
        }

        return salarios;
    }

    /**
     * Calcula o salário de um único agente com base nos turnos do mês atual.
     *
     * @param agenteId ID do agente.
     * @return SalarioDTO contendo o salário do agente.
     * @throws IllegalArgumentException se o agente não for encontrado.
     */
    public SalarioDTO calcularSalarioPorAgente(Long agenteId) {
        // Buscar o agente pelo ID
        Optional<AgentEntity> agenteOpt = agenteRepository.findById(agenteId);
        if (!agenteOpt.isPresent()) {
            throw new IllegalArgumentException("Agente com ID " + agenteId + " não encontrado.");
        }
        AgentEntity agente = agenteOpt.get();

        // Determinar o primeiro e o último dia do mês atual
        YearMonth currentYearMonth = YearMonth.now();
        LocalDate startOfMonth = currentYearMonth.atDay(1);
        LocalDate endOfMonth = currentYearMonth.atEndOfMonth();

        // Buscar todos os turnos do agente no mês atual
        List<TurnEntity> turnosDoAgente = turnoRepository.findByAgentIdAndDataTurnoBetween(agenteId, startOfMonth, endOfMonth);

        // Calcular a diferença em anos entre a data de admissão e a data atual
        long anosNaEmpresa = ChronoUnit.YEARS.between(agente.getAdmissionDate(), LocalDate.now());

        // Determinar se o agente é Senior ou Junior
        boolean isSenior = anosNaEmpresa >= 1;

        // Calcular o salário usando BigDecimal
        BigDecimal salarioTotal = BigDecimal.ZERO;
        for (TurnEntity turno : turnosDoAgente) {
            if (isSenior) {
                salarioTotal = salarioTotal.add(turno.getTipoTurno().getValorSenior());
            } else {
                salarioTotal = salarioTotal.add(turno.getTipoTurno().getValorJunior());
            }
        }

        // Adicionar ao DTO
        SalarioDTO salarioDTO = new SalarioDTO(
                agente.getId(),
                agente.getName(),
                salarioTotal
        );

        return salarioDTO;
    }

}
