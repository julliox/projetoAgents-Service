package br.com.octopus.projectA.service;

import br.com.octopus.projectA.Util.ShiftAlreadyExistsException;
import br.com.octopus.projectA.entity.AgentEntity;
import br.com.octopus.projectA.entity.TurnEntity;
import br.com.octopus.projectA.entity.TipoTurnoEntity;
import br.com.octopus.projectA.repository.AgentRepository;
import br.com.octopus.projectA.repository.TurnRepository;
import br.com.octopus.projectA.repository.TipoTurnoRepository;
import br.com.octopus.projectA.suport.dtos.TipoTurnoDTO;
import br.com.octopus.projectA.suport.dtos.TurnCreateDTO;
import br.com.octopus.projectA.suport.dtos.TurnDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TurnService {

    @Autowired
    private TurnRepository turnRepository;

    @Autowired
    private AgentService agentService;

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private TipoTurnoRepository tipoTurnoRepository;

    /**
     * Busca um Turno por ID.
     * @param id ID do Turno
     * @return TurnEntity
     */
    public TurnEntity findById(Long id) {
        return turnRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Turn not found with id " + id));
    }

    /**
     * Lista todos os turnos de um agente específico.
     * @param id ID do Agente
     * @return Lista de TurnDTO
     */
    public List<TurnDTO> findAllTurnsByAgentId(Long id) {
        return turnRepository.findAllByAgentId(id).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Obtém um turno por ID.
     * @param id ID do Turno
     * @return TurnDTO
     */
    public TurnDTO getProjectById(Long id) {
        return toDto(findById(id));
    }

    /**
     * Lista todos os turnos.
     * @return Lista de TurnDTO
     */
    public List<TurnDTO> findAll() {
        return turnRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Cria novos turnos para um agente nas datas fornecidas.
     * @param dto Dados para criação dos turnos
     * @return TurnDTO (opcional: retorna o primeiro turno criado)
     */
    @Transactional
    public TurnDTO create(TurnCreateDTO dto) {
        // Buscar o agente pelo ID
        AgentEntity agent = agentService.findById(dto.agentId());

        // Buscar o TipoTurno pelo ID
        TipoTurnoEntity tipoTurno = tipoTurnoRepository.findById(dto.tipoTurnoId())
                .orElseThrow(() -> new EntityNotFoundException("TipoTurno não encontrado com ID " + dto.tipoTurnoId()));

        // Verificar se já existe algum turno para o agente nas datas fornecidas
        List<TurnEntity> existingTurns = turnRepository.findByAgentAndDataTurnoIn(agent, dto.dataTurno());

        if (!existingTurns.isEmpty()) {
            // Extrair as datas conflitantes
            List<LocalDate> conflictingDates = existingTurns.stream()
                    .map(TurnEntity::getDataTurno)
                    .collect(Collectors.toList());

            // Formatar a mensagem de erro com as datas conflitantes
            StringBuilder errorMessage = new StringBuilder("O agente já possui um turno nas seguintes datas: ");
            conflictingDates.forEach(date -> errorMessage.append(date.toString()).append(", "));

            // Remover a última vírgula e espaço
            if (errorMessage.length() > 0) {
                errorMessage.setLength(errorMessage.length() - 2);
            }

            throw new ShiftAlreadyExistsException(errorMessage.toString());
        }

        // Criar e salvar os turnos para cada data
        TurnDTO createdTurnDto = null;
        for (LocalDate date : dto.dataTurno()) {
            TurnEntity entity = TurnEntity.builder()
                    .tipoTurno(tipoTurno)
                    .dataTurno(date)
                    .agent(agent)
                    .build();
            TurnEntity savedEntity = turnRepository.save(entity);
            // Opcional: Mapear o último turno criado para retorno
            createdTurnDto = toDto(savedEntity);
        }

        // Retornar o último turno criado (ou você pode ajustar conforme a necessidade)
        return createdTurnDto;
    }

    /**
     * Atualiza um turno existente.
     * @param id ID do Turno a ser atualizado
     * @param dto Dados atualizados
     * @return TurnDTO atualizado
     */
    public TurnDTO update(Long id, TurnDTO dto) {
        TurnEntity entity = findById(id);

        // Buscar o TipoTurno pelo ID
        TipoTurnoEntity tipoTurno = tipoTurnoRepository.findById(dto.tipoTurno().id())
                .orElseThrow(() -> new EntityNotFoundException("TipoTurno não encontrado com ID " + dto.tipoTurno().id()));

        // Atualizar os campos
        entity.setTipoTurno(tipoTurno);
        entity.setDataTurno(dto.dataTurno());

        // Salvar a entidade atualizada
        TurnEntity updatedEntity = turnRepository.save(entity);
        return toDto(updatedEntity);
    }

    /**
     * Deleta um turno por ID.
     * @param id ID do Turno a ser deletado
     */
    public void delete(Long id) {
        TurnEntity entity = findById(id);
        turnRepository.delete(entity);
    }

    /**
     * Converte uma entidade TurnEntity para TurnDTO.
     * @param entity TurnEntity
     * @return TurnDTO
     */
    private TurnDTO toDto(TurnEntity entity) {
        // Obter o nome do agente
        String nomeAgente = entity.getAgent().getName(); // Supondo que AgentEntity tenha o método getName()

        // Converter TipoTurnoEntity para TipoTurnoDTO
        TipoTurnoDTO tipoTurnoDTO = new TipoTurnoDTO(
                entity.getTipoTurno().getId(),
                entity.getTipoTurno().getDescricao(),
                entity.getTipoTurno().getCod(),
                entity.getTipoTurno().getValorJunior(),
                entity.getTipoTurno().getValorSenior()
        );

        return new TurnDTO(
                entity.getId(),
                tipoTurnoDTO,
                nomeAgente,
                entity.getDataTurno(),
                entity.getAgent().getId()
        );
    }

    /**
     * Cria novos turnos para um agente com base em uma lista de TurnCreateDTO.
     * @param dtos Lista de dados para criação dos turnos.
     * @return Lista de TurnDTOs representando os turnos criados.
     */
    @Transactional
    public List<TurnDTO> createInLote(List<TurnCreateDTO> dtos) {
        List<TurnDTO> createdTurnDtos = new ArrayList<>();

        for (TurnCreateDTO dto : dtos) {
            // Buscar o agente pelo ID
            AgentEntity agent = agentService.findById(dto.agentId());

            // Buscar o TipoTurno pelo ID
            TipoTurnoEntity tipoTurno = tipoTurnoRepository.findById(dto.tipoTurnoId())
                    .orElseThrow(() -> new EntityNotFoundException("TipoTurno não encontrado com ID " + dto.tipoTurnoId()));

            // Verificar se já existe algum turno para o agente nas datas fornecidas
            List<TurnEntity> existingTurns = turnRepository.findByAgentAndDataTurnoIn(agent, dto.dataTurno());

            if (!existingTurns.isEmpty()) {
                // Extrair as datas conflitantes
                List<LocalDate> conflictingDates = existingTurns.stream()
                        .map(TurnEntity::getDataTurno)
                        .collect(Collectors.toList());

                // Formatar a mensagem de erro com as datas conflitantes
                StringBuilder errorMessage = new StringBuilder("O agente já possui um turno nas seguintes datas: ");
                conflictingDates.forEach(date -> errorMessage.append(date.toString()).append(", "));

                // Remover a última vírgula e espaço
                if (errorMessage.length() > 0) {
                    errorMessage.setLength(errorMessage.length() - 2);
                }

                throw new ShiftAlreadyExistsException(errorMessage.toString());
            }

            // Criar e salvar os turnos para cada data
            for (LocalDate date : dto.dataTurno()) {
                TurnEntity entity = TurnEntity.builder()
                        .tipoTurno(tipoTurno)
                        .dataTurno(date)
                        .agent(agent)
                        .build();
                TurnEntity savedEntity = turnRepository.save(entity);
                createdTurnDtos.add(toDto(savedEntity));
            }
        }

        // Retornar todos os turnos criados
        return createdTurnDtos;
    }
}