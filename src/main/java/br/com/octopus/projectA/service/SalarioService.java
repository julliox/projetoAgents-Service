package br.com.octopus.projectA.service;

import br.com.octopus.projectA.entity.AdicaoSalarioEntity;
import br.com.octopus.projectA.entity.AgentEntity;
import br.com.octopus.projectA.entity.TurnEntity;
import br.com.octopus.projectA.repository.AdicaoSalarioRepository;
import br.com.octopus.projectA.repository.AgentRepository;
import br.com.octopus.projectA.repository.TipoAdicaoRepository;
import br.com.octopus.projectA.repository.TurnRepository;
import br.com.octopus.projectA.suport.dtos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
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

    @Autowired
    private AdicaoSalarioRepository adicaoSalarioRepository;

    @Autowired
    private TipoAdicaoRepository tipoAdicaoRepository;

    /**
     * Calcula o salário de todos os agentes com base nos turnos do mês atual.
     *
     * @return Lista de SalarioDTO contendo o salário de cada agente.
     */
    public List<SalarioDTO> calcularSalariosDoMesAtual(DataEscolhidaDTO dataEscolhidaDTO) {
        // Obter a lista completa de agentes na base
        List<AgentEntity> listaAgentes = agenteRepository.findAll();

        // Lista para armazenar o salário calculado de cada agente
        List<SalarioDTO> listaSalarios = new ArrayList<>();

        int anoEscolhido = dataEscolhidaDTO.getAnoEscolhido();
        int mesEscolhido = dataEscolhidaDTO.getMesEscolhido();

        YearMonth selectedYearMonth = obterYearMonth(anoEscolhido, mesEscolhido);
        LocalDate startOfMonth = selectedYearMonth.atDay(1);
        LocalDate endOfMonth = selectedYearMonth.atEndOfMonth();

        for (AgentEntity agente : listaAgentes) {
            // Buscar todos os turnos do agente no mês anterior
            List<TurnEntity> turnosDoAgente = turnoRepository.findByAgentIdAndDataTurnoBetween(
                    agente.getId(), startOfMonth, endOfMonth
            );

            // Calcular a diferença em anos entre a data de admissão e a data atual
            long anosNaEmpresa = ChronoUnit.YEARS.between(agente.getAdmissionDate(), LocalDate.now());

            // Determinar se o agente é Senior ou Junior
            boolean isSenior = anosNaEmpresa >= 1;

            // Calcular o salário usando BigDecimal
            BigDecimal salarioBase = BigDecimal.ZERO;
            BigDecimal salarioExtra = BigDecimal.ZERO;
            BigDecimal salarioSubtotal = BigDecimal.ZERO;
            BigDecimal salarioLiquido = BigDecimal.ZERO;
            BigDecimal salarioCincoPorcento = BigDecimal.ZERO;

            for (TurnEntity turno : turnosDoAgente) {
                if (!turno.getTipoTurno().getDescricao().startsWith("EXTRA") && !turno.getTipoTurno().getDescricao().startsWith("FERIADO")) {
                    if (isSenior) {
                        salarioBase = salarioBase.add(turno.getTipoTurno().getValorSenior());
                    } else {
                        salarioBase = salarioBase.add(turno.getTipoTurno().getValorJunior());
                    }
                } else {
                    if (isSenior) {
                        salarioExtra = salarioExtra.add(turno.getTipoTurno().getValorSenior());
                    } else {
                        salarioExtra = salarioExtra.add(turno.getTipoTurno().getValorJunior());
                    }
                }
            }

            // Calcular subtotal e valores finais
            salarioSubtotal = salarioBase.add(salarioExtra);
            salarioCincoPorcento = salarioSubtotal.multiply(new BigDecimal("0.05")).setScale(2, RoundingMode.HALF_UP);
            salarioLiquido = salarioSubtotal.multiply(new BigDecimal("1.05")).setScale(2, RoundingMode.HALF_UP);

            // Adicionar ao DTO
            SalarioDTO salarioDTO = new SalarioDTO(
                    agente.getId(),
                    agente.getName(),
                    salarioBase,
                    salarioExtra,
                    salarioSubtotal,
                    salarioCincoPorcento,
                    salarioLiquido
            );

            listaSalarios.add(salarioDTO);
        }

        return listaSalarios;
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
        //TODO ARRUMAR AQUI
        YearMonth currentYearMonth = YearMonth.now().minusMonths(1);
        LocalDate startOfMonth = currentYearMonth.atDay(1);
        LocalDate endOfMonth = currentYearMonth.atEndOfMonth();

        // Buscar todos os turnos do agente no mês atual
        List<TurnEntity> turnosDoAgente = turnoRepository.findByAgentIdAndDataTurnoBetween(agenteId, startOfMonth, endOfMonth);

        // Calcular a diferença em anos entre a data de admissão e a data atual
        long anosNaEmpresa = ChronoUnit.YEARS.between(agente.getAdmissionDate(), LocalDate.now());

        // Determinar se o agente é Senior ou Junior
        boolean isSenior = anosNaEmpresa >= 1;

        // Calcular o salário usando BigDecimal
        BigDecimal salarioBase = BigDecimal.ZERO;
        BigDecimal salarioExtra = BigDecimal.ZERO;
        BigDecimal salarioSubtotal = BigDecimal.ZERO;
        BigDecimal salarioLiquido = BigDecimal.ZERO;
        BigDecimal salarioCincoPorcento = BigDecimal.ZERO;
        for (TurnEntity turno : turnosDoAgente) {
            if (!turno.getTipoTurno().getDescricao().startsWith("EXTRA") && !turno.getTipoTurno().getDescricao().startsWith("FERIADO")) {
                if (isSenior) {
                    salarioBase = salarioBase.add(turno.getTipoTurno().getValorSenior());
                } else {
                    salarioBase = salarioBase.add(turno.getTipoTurno().getValorJunior());
                }
            } else if(turno.getTipoTurno().getDescricao().startsWith("FERIADO")){
                if (isSenior) {
                    salarioBase = salarioBase.add(turno.getTipoTurno().getValorSenior());
                    salarioExtra = salarioExtra.add(turno.getTipoTurno().getValorSenior());
                } else {
                    salarioBase = salarioBase.add(turno.getTipoTurno().getValorJunior());
                    salarioExtra = salarioExtra.add(turno.getTipoTurno().getValorJunior());
                }
            }else {
                if (isSenior) {
                    salarioExtra = salarioExtra.add(turno.getTipoTurno().getValorSenior());
                } else {
                    salarioExtra = salarioExtra.add(turno.getTipoTurno().getValorJunior());
                }
            }
            salarioSubtotal = salarioBase.add(salarioExtra);
            salarioCincoPorcento = salarioSubtotal.multiply(new BigDecimal("0.05")).setScale(2, RoundingMode.HALF_UP);
            salarioLiquido = salarioSubtotal.multiply(new BigDecimal("1.05")).setScale(2, RoundingMode.HALF_UP);
        }

        // Adicionar ao DTO
        SalarioDTO salarioDTO = new SalarioDTO(
                agente.getId(),
                agente.getName(),
                salarioBase,
                salarioExtra,
                salarioSubtotal,
                salarioCincoPorcento,
                salarioLiquido
        );

        return salarioDTO;
    }

    private YearMonth obterYearMonth(int ano, int mes) {
        return YearMonth.of(ano, mes);
    }


    public AdicaoSalarioDTO createAdicionalsalario(AdicaoSalarioCreateDTO adicaoSalarioDTO) {

        // Verifica se já existe uma adição de salário para o agente, tipo e mês especificados
        Optional<AdicaoSalarioEntity> existingAdicaoSalario = adicaoSalarioRepository.findByAgentIdAndTipoAdicaoIdAndMesAdicao(
                adicaoSalarioDTO.getAgentId(),
                adicaoSalarioDTO.getTipoAdicaoId(),
                adicaoSalarioDTO.getMesAdicao()
        );

        // Se já existe, lança uma exceção ou retorna uma mensagem de erro
        if (existingAdicaoSalario.isPresent()) {
            throw new IllegalArgumentException("O agente já possui uma adição de salário para este tipo e mês.");
        }

        // Cria a nova entidade AdicaoSalarioEntity
        AdicaoSalarioEntity adicaoSalarioEntity = AdicaoSalarioEntity.builder()
                .mesAdicao(adicaoSalarioDTO.getMesAdicao())
                .qtyAdicao(adicaoSalarioDTO.getQtyAdicao())
                .tipoAdicao(tipoAdicaoRepository.findById(adicaoSalarioDTO.getTipoAdicaoId()).orElseThrow(
                        () -> new IllegalArgumentException("Tipo de adição não encontrado.")
                ))
                .agent(agenteRepository.findById(adicaoSalarioDTO.getAgentId()).orElseThrow(
                        () -> new IllegalArgumentException("Agente não encontrado.")
                ))
                .build();

        // Salva a nova entidade no repositório
        adicaoSalarioRepository.save(adicaoSalarioEntity);

        // Retorna o DTO correspondente
        return adicaoSalarioToDTO(adicaoSalarioEntity);
    }

    private AdicaoSalarioDTO adicaoSalarioToDTO(AdicaoSalarioEntity entity) {
        if (entity == null) {
            return null;
        }

        TipoAdicaoDTO tipoAdicaoDTO = new TipoAdicaoDTO(
                entity.getTipoAdicao().getId(),
                entity.getTipoAdicao().getDesTipoAdicao()
        );

        AgentDTO agentDTO = new AgentDTO(
                entity.getAgent().getId(),
                entity.getAgent().getName(),
                entity.getAgent().getEmail(),
                entity.getAgent().getPhoneNumber(),
                entity.getAgent().getDesInfo(),
                entity.getAgent().getAdmissionDate(),
                entity.getAgent().getStatus()
        );

        return new AdicaoSalarioDTO(
                entity.getId(),
                tipoAdicaoDTO,
                entity.getQtyAdicao(),
                entity.getMesAdicao(),
                agentDTO
        );
    }

}
