package br.com.octopus.undergroundFiber.service;

import br.com.octopus.undergroundFiber.Util.ShiftAlreadyExistsException;
import br.com.octopus.undergroundFiber.entity.AgentEntity;
import br.com.octopus.undergroundFiber.entity.TurnEntity;
import br.com.octopus.undergroundFiber.repository.AgentRepository;
import br.com.octopus.undergroundFiber.repository.TurnRepository;
import br.com.octopus.undergroundFiber.suport.dtos.TurnCreateDTO;
import br.com.octopus.undergroundFiber.suport.dtos.TurnDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TurnService {

    @Autowired
    private TurnRepository turnRepository;

    @Autowired
    private AgentService agentService;
    @Autowired
    private AgentRepository agentRepository;

    public TurnEntity findById(Long id) {
        return turnRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Turn not found with id " + id));
    }

    public List<TurnDTO> findAllTurnsByAgentId(Long id) {
        return turnRepository.findAllByClientId(id).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public TurnDTO getProjectById(Long id) {
        return toDto(findById(id));
    }

    public List<TurnDTO> findAll() {
        return turnRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public TurnDTO create(TurnCreateDTO dto) {
        // Buscar o agente pelo ID
        AgentEntity agent = agentService.findById(dto.clientId());

        // Verificar se já existe algum turno para o agente nas datas fornecidas
        List<TurnEntity> existingTurns = turnRepository.findByClientAndDataTurnoIn(agent, dto.dataTurno());

        if (!existingTurns.isEmpty()) {
            // Extrair as datas conflitantes
            List<LocalDate> conflictingDates = existingTurns.stream()
                    .map(TurnEntity::getDataTurno)
                    .toList();

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
                    .typeTurno(dto.typeTurn())
                    .dataTurno(date)
                    .client(agent)
                    .build();
            turnRepository.save(entity);
        }

        // Opcional: Retornar uma representação consolidada ou o último turno criado
        // Aqui, retornaremos uma mensagem de sucesso ou você pode adaptar conforme necessário
        return new TurnDTO(null, dto.typeTurn(), dto.nomeAgente(), dto.dataTurno().get(0), dto.clientId());
    }


    public TurnDTO update(Long id, TurnDTO dto) {
        TurnEntity entity = findById(id);
        entity = new TurnEntity(id, dto.typeTurn(), dto.dataTurno(), agentService.findById(dto.clientId()));
        TurnEntity updatedEntity = turnRepository.save(entity);
        return toDto(updatedEntity);
    }

    public void delete(Long id) {
        TurnEntity entity = findById(id);
        turnRepository.delete(entity);
    }

    private TurnDTO toDto(TurnEntity entity) {
        Optional<AgentEntity> agente = agentRepository.findById(entity.getClient().getId());
        return new TurnDTO(entity.getId(), entity.getTypeTurno(),agente.get().getName(), entity.getDataTurno(), entity.getClient().getId());
    }

}
