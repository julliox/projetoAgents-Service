package br.com.octopus.undergroundFiber.service;

import br.com.octopus.undergroundFiber.entity.AgentEntity;
import br.com.octopus.undergroundFiber.entity.TurnEntity;
import br.com.octopus.undergroundFiber.repository.AgentRepository;
import br.com.octopus.undergroundFiber.repository.TurnRepository;
import br.com.octopus.undergroundFiber.suport.dtos.AgentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AgentService {

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private TurnRepository turnRepository;

    public AgentEntity findById(Long id) {
        return agentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client not found with id " + id));
    }

    public AgentDTO getClientById(Long id) {
        return toDto(findById(id));
    }

    public List<AgentDTO> findAll() {
        return agentRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public AgentDTO create(AgentDTO dto) {
        AgentEntity entity = new AgentEntity(null, dto.name(), dto.email(), dto.phoneNumber(), dto.desInfo(), dto.admissionDate(), dto.status());
        AgentEntity savedEntity = agentRepository.save(entity);
        return toDto(savedEntity);
    }

    public AgentDTO update(Long id, AgentDTO dto) {
        AgentEntity entity = findById(id);
        entity = new AgentEntity(id, dto.name(), dto.email(), dto.phoneNumber(), dto.desInfo(), dto.admissionDate(), dto.status());
        AgentEntity updatedEntity = agentRepository.save(entity);
        return toDto(updatedEntity);
    }

    public void delete(Long id) {
        AgentEntity entity = findById(id);
        List<TurnEntity> turnos = turnRepository.findAllByClientId(id);
        turnRepository.deleteAll(turnos);
        agentRepository.delete(entity);
    }

    private AgentDTO toDto(AgentEntity entity) {
        return new AgentDTO(entity.getId(), entity.getName(), entity.getEmail(), entity.getPhoneNumber(), entity.getDesInfo(), entity.getAdmissionDate(), entity.getStatus());
    }

}
