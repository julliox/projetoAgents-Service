package br.com.octopus.projectA.service;

import br.com.octopus.projectA.entity.AdicaoSalarioEntity;
import br.com.octopus.projectA.entity.EmployeeEntity;
import br.com.octopus.projectA.entity.TipoAdicaoEntity;
import br.com.octopus.projectA.entity.TurnEntity;
import br.com.octopus.projectA.repository.AdicaoSalarioRepository;
import br.com.octopus.projectA.repository.EmployeeRepository;
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
import java.util.stream.Collectors;

@Service
public class SalarioService {

    @Autowired
    private EmployeeRepository employeeRepository;

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
        // Obter a lista completa de colaboradores na base
        List<EmployeeEntity> listaColaboradores = employeeRepository.findAll();

        // Lista para armazenar o salário calculado de cada colaborador
        List<SalarioDTO> listaSalarios = new ArrayList<>();

        int anoEscolhido = dataEscolhidaDTO.getAnoEscolhido();
        int mesEscolhido = dataEscolhidaDTO.getMesEscolhido();

        YearMonth selectedYearMonth = obterYearMonth(anoEscolhido, mesEscolhido);
        LocalDate startOfMonth = selectedYearMonth.atDay(1);
        LocalDate endOfMonth = selectedYearMonth.atEndOfMonth();

        for (EmployeeEntity colaborador : listaColaboradores) {
            // Buscar todos os turnos do colaborador no mês
            List<TurnEntity> turnosDoColaborador = turnoRepository.findByEmployeeIdAndDataTurnoBetween(
                    colaborador.getId(), startOfMonth, endOfMonth
            );

            // Calcular a diferença em anos entre a data de admissão e a data atual
            long anosNaEmpresa = ChronoUnit.YEARS.between(colaborador.getAdmissionDate(), LocalDate.now());

            // Determinar se o colaborador é Senior ou Junior
            boolean isSenior = anosNaEmpresa >= 1;

            // Calcular o salário usando BigDecimal
            BigDecimal salarioBase = BigDecimal.ZERO;
            BigDecimal salarioExtra = BigDecimal.ZERO;
            BigDecimal salarioSubtotal = BigDecimal.ZERO;
            BigDecimal salarioLiquido = BigDecimal.ZERO;
            BigDecimal salarioCincoPorcento = BigDecimal.ZERO;

            for (TurnEntity turno : turnosDoColaborador) {
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
                    colaborador.getId(),
                    colaborador.getName(),
                    salarioBase,
                    salarioExtra,
                    salarioSubtotal,
                    salarioCincoPorcento,
                    salarioLiquido,
                    null
            );

            listaSalarios.add(salarioDTO);
        }

        return listaSalarios;
    }

    /**
     * Calcula o salário de um único colaborador com base nos turnos do mês atual.
     *
     * @param agenteId ID do colaborador (mantido para compatibilidade).
     * @return SalarioDTO contendo o salário do colaborador.
     * @throws IllegalArgumentException se o colaborador não for encontrado.
     */
    public SalarioDTO calcularSalarioPorAgente(Long agenteId) {
        // Buscar o colaborador pelo ID
        EmployeeEntity colaborador = employeeRepository.findById(agenteId)
                .orElseThrow(() -> new IllegalArgumentException("Colaborador com ID " + agenteId + " não encontrado."));

        // Determinar o primeiro e o último dia do mês atual
        YearMonth currentYearMonth = YearMonth.now();
        LocalDate startOfMonth = currentYearMonth.atDay(1);
        LocalDate endOfMonth = currentYearMonth.atEndOfMonth();

        // Buscar todos os turnos do colaborador no mês atual
        List<TurnEntity> turnosDoColaborador = turnoRepository.findByEmployeeIdAndDataTurnoBetween(agenteId, startOfMonth, endOfMonth);

        // Calcular a diferença em anos entre a data de admissão e a data atual
        long anosNaEmpresa = ChronoUnit.YEARS.between(colaborador.getAdmissionDate(), LocalDate.now());

        // Determinar se o colaborador é Senior ou Junior
        boolean isSenior = anosNaEmpresa >= 1;

        // Calcular o salário usando BigDecimal
        BigDecimal salarioBase = BigDecimal.ZERO;
        BigDecimal salarioExtra = BigDecimal.ZERO;
        BigDecimal salarioSubtotal = BigDecimal.ZERO;
        BigDecimal salarioLiquido = BigDecimal.ZERO;
        BigDecimal salarioCincoPorcento = BigDecimal.ZERO;
        for (TurnEntity turno : turnosDoColaborador) {
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

        }

        List<AdicaoSalarioDTO> novaLista = adicaoSalarioRepository
                .findAllByEmployeeIdAndMesAdicao(agenteId, currentYearMonth)
                .stream()
                .map(this::adicaoSalarioToDTO)
                .toList();


        for (AdicaoSalarioDTO adicaoSalarioDTO : novaLista) {
            salarioSubtotal = salarioSubtotal.add(adicaoSalarioDTO.qtyAdicao());
        }

        salarioCincoPorcento = salarioSubtotal.multiply(new BigDecimal("0.05")).setScale(2, RoundingMode.HALF_UP);
        salarioLiquido = salarioSubtotal.multiply(new BigDecimal("1.05")).setScale(2, RoundingMode.HALF_UP);

        // Adicionar ao DTO
        SalarioDTO salarioDTO = new SalarioDTO(
                colaborador.getId(),
                colaborador.getName(),
                salarioBase,
                salarioExtra,
                salarioSubtotal,
                salarioCincoPorcento,
                salarioLiquido,
                novaLista
        );

        return salarioDTO;
    }

    private YearMonth obterYearMonth(int ano, int mes) {
        return YearMonth.of(ano, mes);
    }


    public AdicaoSalarioDTO createAdicionalsalario(AdicaoSalarioCreateDTO adicaoSalarioDTO) {

        // Verifica se já existe uma adição de salário para o colaborador, tipo e mês especificados
        Optional<AdicaoSalarioEntity> existingAdicaoSalario = adicaoSalarioRepository.findByEmployeeIdAndTipoAdicaoIdAndMesAdicao(
                adicaoSalarioDTO.getAgentId(),
                adicaoSalarioDTO.getTipoAdicaoId(),
                adicaoSalarioDTO.getMesAdicao()
        );

        // Se já existe, lança uma exceção ou retorna uma mensagem de erro
        if (existingAdicaoSalario.isPresent()) {
            throw new IllegalArgumentException("O colaborador já possui uma adição de salário para este tipo e mês.");
        }

        // Cria a nova entidade AdicaoSalarioEntity
        AdicaoSalarioEntity adicaoSalarioEntity = AdicaoSalarioEntity.builder()
                .mesAdicao(adicaoSalarioDTO.getMesAdicao())
                .qtyAdicao(adicaoSalarioDTO.getQtyAdicao())
                .tipoAdicao(tipoAdicaoRepository.findById(adicaoSalarioDTO.getTipoAdicaoId()).orElseThrow(
                        () -> new IllegalArgumentException("Tipo de adição não encontrado.")
                ))
                .employee(employeeRepository.findById(adicaoSalarioDTO.getAgentId()).orElseThrow(
                        () -> new IllegalArgumentException("Colaborador não encontrado.")
                ))
                .build();

        // Salva a nova entidade no repositório
        adicaoSalarioRepository.save(adicaoSalarioEntity);

        // Retorna o DTO correspondente
        return adicaoSalarioToDTO(adicaoSalarioEntity);
    }

    /**
     * Busca todas as adições de salário.
     * @return Lista de AdicaoSalarioDTO
     */
    public List<AdicaoSalarioDTO> getAllAdicaoSalario() {
        return adicaoSalarioRepository.findAll()
                .stream()
                .map(this::adicaoSalarioToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca uma adição de salário por ID.
     * @param id ID da adição de salário
     * @return AdicaoSalarioDTO
     */
    public AdicaoSalarioDTO getAdicaoSalarioById(Long id) {
        AdicaoSalarioEntity entity = adicaoSalarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Adição de salário não encontrada com ID " + id));
        return adicaoSalarioToDTO(entity);
    }

    /**
     * Busca todas as adições de salário de um colaborador.
     * @param employeeId ID do colaborador
     * @return Lista de AdicaoSalarioDTO
     */
    public List<AdicaoSalarioDTO> getAllAdicaoSalarioByEmployeeId(Long employeeId) {
        return adicaoSalarioRepository.findAllByEmployeeId(employeeId)
                .stream()
                .map(this::adicaoSalarioToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Atualiza uma adição de salário existente.
     * @param id ID da adição de salário a ser atualizada
     * @param updateDTO DTO com os novos dados
     * @return AdicaoSalarioDTO atualizado
     */
    public AdicaoSalarioDTO updateAdicaoSalario(Long id, AdicaoSalarioUpdateDTO updateDTO) {
        AdicaoSalarioEntity existingEntity = adicaoSalarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Adição de salário não encontrada com ID " + id));

        // Verifica se está tentando atualizar para um tipo/mês que já existe (exceto o próprio registro)
        if (updateDTO.getTipoAdicaoId() != null && updateDTO.getMesAdicao() != null) {
            Optional<AdicaoSalarioEntity> existing = adicaoSalarioRepository.findByEmployeeIdAndTipoAdicaoIdAndMesAdicao(
                    existingEntity.getEmployee().getId(),
                    updateDTO.getTipoAdicaoId(),
                    updateDTO.getMesAdicao()
            );
            if (existing.isPresent() && !existing.get().getId().equals(id)) {
                throw new IllegalArgumentException("O colaborador já possui uma adição de salário para este tipo e mês.");
            }
        }

        // Atualiza os campos
        if (updateDTO.getTipoAdicaoId() != null) {
            TipoAdicaoEntity tipoAdicao = tipoAdicaoRepository.findById(updateDTO.getTipoAdicaoId())
                    .orElseThrow(() -> new IllegalArgumentException("Tipo de adição não encontrado."));
            existingEntity.setTipoAdicao(tipoAdicao);
        }

        if (updateDTO.getQtyAdicao() != null) {
            existingEntity.setQtyAdicao(updateDTO.getQtyAdicao());
        }

        if (updateDTO.getMesAdicao() != null) {
            existingEntity.setMesAdicao(updateDTO.getMesAdicao());
        }

        AdicaoSalarioEntity updatedEntity = adicaoSalarioRepository.save(existingEntity);
        return adicaoSalarioToDTO(updatedEntity);
    }

    /**
     * Deleta uma adição de salário por ID.
     * @param id ID da adição de salário a ser deletada
     */
    public void deleteAdicaoSalario(Long id) {
        AdicaoSalarioEntity entity = adicaoSalarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Adição de salário não encontrada com ID " + id));
        adicaoSalarioRepository.delete(entity);
    }

    private AdicaoSalarioDTO adicaoSalarioToDTO(AdicaoSalarioEntity entity) {
        if (entity == null) {
            return null;
        }

        TipoAdicaoDTO tipoAdicaoDTO = new TipoAdicaoDTO(
                entity.getTipoAdicao().getId(),
                entity.getTipoAdicao().getDesTipoAdicao()
        );

        return new AdicaoSalarioDTO(
                entity.getId(),
                tipoAdicaoDTO,
                entity.getQtyAdicao(),
                entity.getMesAdicao(),
                entity.getEmployee().getId(),
                entity.getEmployee().getName()
        );
    }

}
