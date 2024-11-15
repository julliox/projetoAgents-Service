package br.com.octopus.projectA.service;

import br.com.octopus.projectA.entity.AgentEntity;
import br.com.octopus.projectA.entity.TurnEntity;
import br.com.octopus.projectA.repository.AgentRepository;
import br.com.octopus.projectA.repository.TurnRepository;
import br.com.octopus.projectA.suport.dtos.AgentDTO;
import br.com.octopus.projectA.suport.dtos.AgentProfileDTO;
import br.com.octopus.projectA.suport.dtos.SalarioDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AgentService {

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private TurnRepository turnRepository;

    @Autowired
    private SalarioService salarioService;


    public AgentEntity findById(Long id) {
        return agentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client not found with id " + id));
    }

    public AgentDTO getAgentById(Long id) {
        return agentToDto(findById(id));
    }

    public AgentProfileDTO getProfileAgent (Long id) {
        AgentDTO agent = getAgentById(id);
        SalarioDTO salarioDTO = salarioService.calcularSalarioPorAgente(id);
        return new AgentProfileDTO(agent.id(), agent.name(), agent.email(), agent.phoneNumber(), agent.desInfo(), agent.admissionDate(), salarioDTO.getSalarioBase(), agent.status());
    }

    public List<AgentDTO> findAll() {
        return agentRepository.findAll().stream()
                .map(this::agentToDto)
                .collect(Collectors.toList());
    }

    public AgentDTO create(AgentDTO dto) {
        AgentEntity entity = new AgentEntity(null, dto.name(), dto.email(), dto.phoneNumber(), dto.desInfo(), dto.admissionDate(), dto.status());
        AgentEntity savedEntity = agentRepository.save(entity);
        return agentToDto(savedEntity);
    }

    public AgentDTO update(Long id, AgentDTO dto) {
        AgentEntity entity = findById(id);
        entity = new AgentEntity(id, dto.name(), dto.email(), dto.phoneNumber(), dto.desInfo(), dto.admissionDate(), dto.status());
        AgentEntity updatedEntity = agentRepository.save(entity);
        return agentToDto(updatedEntity);
    }

    public void delete(Long id) {
        AgentEntity entity = findById(id);
        List<TurnEntity> turnos = turnRepository.findAllByAgentId(id);
        turnRepository.deleteAll(turnos);
        agentRepository.delete(entity);
    }

    private AgentDTO agentToDto(AgentEntity entity) {
        return new AgentDTO(entity.getId(), entity.getName(), entity.getEmail(), entity.getPhoneNumber(), entity.getDesInfo(), entity.getAdmissionDate(), entity.getStatus());
    }

    private AgentProfileDTO agentProfileToDto (AgentEntity entity, BigDecimal salarioBase) {
        return new AgentProfileDTO( entity.getId(), entity.getName(), entity.getEmail(), entity.getPhoneNumber(), entity.getDesInfo(), entity.getAdmissionDate(), salarioBase ,  entity.getStatus());
    }

}
