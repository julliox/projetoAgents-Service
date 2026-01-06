package br.com.octopus.projectA.service;

import br.com.octopus.projectA.entity.EmployeeEntity;
import br.com.octopus.projectA.entity.ProfileEntity;
import br.com.octopus.projectA.entity.TurnEntity;
import br.com.octopus.projectA.entity.UserEntity;
import br.com.octopus.projectA.entity.enuns.StatusEnum;
import br.com.octopus.projectA.repository.EmployeeRepository;
import br.com.octopus.projectA.repository.TurnRepository;
import br.com.octopus.projectA.repository.UserRepository;
import br.com.octopus.projectA.service.ProfileService;
import br.com.octopus.projectA.suport.dtos.AgentDTO;
import br.com.octopus.projectA.suport.dtos.AgentProfileDTO;
import br.com.octopus.projectA.suport.dtos.SalarioDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AgentService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TurnRepository turnRepository;

    @Autowired
    private SalarioService salarioService;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public EmployeeEntity findById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with id " + id));
    }

    public EmployeeEntity findByUserId(Long userId) {
        return employeeRepository.findByUser_Id(userId)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with userId " + userId));
    }

    public AgentDTO getAgentById(Long id) {
        return employeeToDto(findById(id));
    }

    public AgentProfileDTO getProfileAgent(Long id) {
        AgentDTO agent = getAgentById(id);
        SalarioDTO salarioDTO = salarioService.calcularSalarioPorAgente(id);
        return new AgentProfileDTO(agent.id(), agent.name(), agent.email(), agent.phoneNumber(), agent.desInfo(), agent.admissionDate(), salarioDTO.getSalarioBase(), agent.status());
    }

    public List<AgentDTO> findAll() {
        return employeeRepository.findAll().stream()
                .map(this::employeeToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public AgentDTO create(AgentDTO dto) {
        Optional<UserEntity> userOpt = userRepository.findByEmail(dto.email());
        
        UserEntity user;
        if (userOpt.isPresent()) {
            user = userOpt.get();
            if (employeeRepository.findByUser_Id(user.getId()).isPresent()) {
                throw new IllegalArgumentException("Email já está em uso por outro colaborador");
            }
        } else {
            ProfileEntity agentProfile;
            try {
                agentProfile = profileService.findByName("AGENT");
            } catch (EntityNotFoundException e) {
                throw new IllegalArgumentException("Profile 'AGENT' não encontrado. É necessário criar o profile antes de criar colaboradores.");
            }
            
            String temporaryPassword = passwordEncoder.encode("123456"); // Senha temporária = 123456
            user = UserEntity.builder()
                    .name(dto.name())
                    .email(dto.email())
                    .password(temporaryPassword)
                    .profile(agentProfile)
                    .status(StatusEnum.ACTIVE)
                    .build();
            user = userRepository.save(user);
        }

        // Criar o employee associado ao usuário
        EmployeeEntity entity = EmployeeEntity.builder()
                .user(user)
                .phoneNumber(dto.phoneNumber())
                .desInfo(dto.desInfo())
                .admissionDate(dto.admissionDate())
                .status(dto.status() != null ? dto.status() : StatusEnum.ACTIVE)
                .build();

        EmployeeEntity savedEntity = employeeRepository.save(entity);
        return employeeToDto(savedEntity);
    }

    @Transactional
    public AgentDTO update(Long id, AgentDTO dto) {
        EmployeeEntity entity = findById(id);
        
        UserEntity user = entity.getUser();
        if (!user.getName().equals(dto.name())) {
            user.setName(dto.name());
            userRepository.save(user);
        }
        if (!user.getEmail().equals(dto.email())) {
            if (userRepository.findByEmail(dto.email()).isPresent()) {
                throw new IllegalArgumentException("Email já está em uso");
            }
            user.setEmail(dto.email());
            userRepository.save(user);
        }

        entity.setPhoneNumber(dto.phoneNumber());
        entity.setDesInfo(dto.desInfo());
        entity.setAdmissionDate(dto.admissionDate());
        entity.setStatus(dto.status() != null ? dto.status() : entity.getStatus());

        EmployeeEntity updatedEntity = employeeRepository.save(entity);
        return employeeToDto(updatedEntity);
    }

    @Transactional
    public void delete(Long id) {
        EmployeeEntity entity = findById(id);
        List<TurnEntity> turnos = turnRepository.findAllByEmployeeId(id);
        turnRepository.deleteAll(turnos);
        employeeRepository.delete(entity);
    }

    private AgentDTO employeeToDto(EmployeeEntity entity) {
        return new AgentDTO(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getPhoneNumber(),
                entity.getDesInfo(),
                entity.getAdmissionDate(),
                entity.getStatus()
        );
    }

    private AgentProfileDTO employeeProfileToDto(EmployeeEntity entity, BigDecimal salarioBase) {
        return new AgentProfileDTO(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getPhoneNumber(),
                entity.getDesInfo(),
                entity.getAdmissionDate(),
                salarioBase,
                entity.getStatus()
        );
    }

}
