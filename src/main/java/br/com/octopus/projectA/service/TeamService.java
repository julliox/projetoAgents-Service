package br.com.octopus.projectA.service;

import br.com.octopus.projectA.entity.EmployeeEntity;
import br.com.octopus.projectA.entity.TeamEntity;
import br.com.octopus.projectA.entity.enuns.TeamStatus;
import br.com.octopus.projectA.repository.EmployeeRepository;
import br.com.octopus.projectA.repository.TeamRepository;
import br.com.octopus.projectA.suport.dtos.TeamDtos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    /**
     * Lista todas as equipes com paginação e filtros
     */
    public Page<TeamDtos.TeamResponse> getTeams(String search, TeamStatus status, int page, int size, String sort, String direction) {
        Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sortObj = Sort.by(sortDirection, sort != null ? sort : "name");
        Pageable pageable = PageRequest.of(page, size, sortObj);

        Page<TeamEntity> teamsPage = teamRepository.findByFilters(search, status, pageable);
        
        return teamsPage.map(this::toTeamResponse);
    }

    /**
     * Obtém uma equipe por ID
     */
    @Transactional(readOnly = true)
    public TeamDtos.TeamResponse getTeamById(Long id) {
        TeamEntity team = findById(id);
        return toTeamResponse(team);
    }

    /**
     * Cria uma nova equipe
     */
    @Transactional
    public TeamDtos.TeamResponse createTeam(TeamDtos.CreateTeamRequest request) {
        // Verifica se já existe uma equipe com o mesmo nome
        if (teamRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Já existe uma equipe com o nome: " + request.getName());
        }

        // Valida os colaboradores
        Set<EmployeeEntity> employees = new HashSet<>();
        if (request.getAgentIds() != null && !request.getAgentIds().isEmpty()) {
            List<EmployeeEntity> foundEmployees = employeeRepository.findAllById(request.getAgentIds());
            if (foundEmployees.size() != request.getAgentIds().size()) {
                throw new EntityNotFoundException("Um ou mais colaboradores não foram encontrados");
            }
            employees = new HashSet<>(foundEmployees);
        }

        TeamEntity team = TeamEntity.builder()
                .name(request.getName())
                .workStartTime(request.getWorkStartTime())
                .workEndTime(request.getWorkEndTime())
                .status(TeamStatus.ACTIVE)
                .employees(employees)
                .build();

        TeamEntity savedTeam = teamRepository.save(team);
        return toTeamResponse(savedTeam);
    }

    /**
     * Atualiza uma equipe existente
     */
    @Transactional
    public TeamDtos.TeamResponse updateTeam(TeamDtos.UpdateTeamRequest request) {
        TeamEntity team = findById(request.getId());

        // Verifica se o nome está sendo alterado e se já existe outra equipe com esse nome
        if (!team.getName().equals(request.getName()) && teamRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Já existe uma equipe com o nome: " + request.getName());
        }

        // Atualiza os colaboradores
        Set<EmployeeEntity> employees = new HashSet<>();
        if (request.getAgentIds() != null && !request.getAgentIds().isEmpty()) {
            List<EmployeeEntity> foundEmployees = employeeRepository.findAllById(request.getAgentIds());
            if (foundEmployees.size() != request.getAgentIds().size()) {
                throw new EntityNotFoundException("Um ou mais colaboradores não foram encontrados");
            }
            employees = new HashSet<>(foundEmployees);
        }

        team.setName(request.getName());
        team.setWorkStartTime(request.getWorkStartTime());
        team.setWorkEndTime(request.getWorkEndTime());
        team.setStatus(request.getStatus());
        team.setEmployees(employees);
        team.setUpdatedAt(LocalDateTime.now());

        TeamEntity savedTeam = teamRepository.save(team);
        return toTeamResponse(savedTeam);
    }

    /**
     * Remove uma equipe
     */
    @Transactional
    public void deleteTeam(Long id) {
        TeamEntity team = findById(id);
        teamRepository.delete(team);
    }

    /**
     * Atualiza apenas o status de uma equipe
     */
    @Transactional
    public TeamDtos.TeamResponse updateTeamStatus(Long id, TeamStatus status) {
        TeamEntity team = findById(id);
        team.setStatus(status);
        team.setUpdatedAt(LocalDateTime.now());
        TeamEntity savedTeam = teamRepository.save(team);
        return toTeamResponse(savedTeam);
    }

    /**
     * Obtém estatísticas das equipes
     */
    @Transactional(readOnly = true)
    public TeamDtos.TeamStatistics getTeamStatistics() {
        long totalTeams = teamRepository.count();
        long activeTeams = teamRepository.countByStatus(TeamStatus.ACTIVE);
        long inactiveTeams = teamRepository.countByStatus(TeamStatus.INACTIVE);
        
        long totalAgents = teamRepository.countAllDistinctAgents();
        
        Double averageAgentsPerTeam = activeTeams > 0 
            ? (double) totalAgents / activeTeams 
            : 0.0;

        TeamDtos.TeamStatistics statistics = new TeamDtos.TeamStatistics();
        statistics.setTotalTeams(totalTeams);
        statistics.setActiveTeams(activeTeams);
        statistics.setInactiveTeams(inactiveTeams);
        statistics.setTotalAgents(totalAgents);
        statistics.setAverageAgentsPerTeam(averageAgentsPerTeam);

        return statistics;
    }

    /**
     * Busca uma equipe por ID (método auxiliar)
     */
    public TeamEntity findById(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Equipe não encontrada com id: " + id));
    }

    /**
     * Converte TeamEntity para TeamResponse
     */
    private TeamDtos.TeamResponse toTeamResponse(TeamEntity entity) {
        TeamDtos.TeamResponse response = new TeamDtos.TeamResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setWorkStartTime(entity.getWorkStartTime());
        response.setWorkEndTime(entity.getWorkEndTime());
        response.setStatus(entity.getStatus());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        
        // Converte colaboradores para AgentSummary
        List<TeamDtos.AgentSummary> agentSummaries = entity.getEmployees().stream()
                .map(this::toAgentSummary)
                .collect(Collectors.toList());
        response.setAgents(agentSummaries);
        
        // Calcula campos adicionais
        response.setWorkTimeFormatted(formatWorkTime(entity.getWorkStartTime(), entity.getWorkEndTime()));
        response.setDurationHours(calculateShiftDuration(entity.getWorkStartTime(), entity.getWorkEndTime()));
        response.setAgentsCount(entity.getEmployees().size());

        return response;
    }

    /**
     * Converte EmployeeEntity para AgentSummary
     */
    private TeamDtos.AgentSummary toAgentSummary(EmployeeEntity employee) {
        TeamDtos.AgentSummary summary = new TeamDtos.AgentSummary();
        summary.setId(employee.getId());
        summary.setName(employee.getName());
        summary.setEmail(employee.getEmail());
        summary.setPhoneNumber(employee.getPhoneNumber());
        return summary;
    }

    /**
     * Formata horário para exibição
     */
    private String formatWorkTime(String startTime, String endTime) {
        return startTime + " - " + endTime;
    }

    /**
     * Calcula duração do turno em horas
     */
    private Double calculateShiftDuration(String startTime, String endTime) {
        int startMinutes = parseTime(startTime);
        int endMinutes = parseTime(endTime);

        if (startMinutes == -1 || endMinutes == -1) {
            return 0.0;
        }

        int duration = endMinutes - startMinutes;
        
        // Se o horário termina no dia seguinte
        if (duration < 0) {
            duration += 24 * 60; // Adiciona 24 horas
        }

        // Converte para horas e arredonda para 1 casa decimal
        return Math.round((duration / 60.0) * 10.0) / 10.0;
    }

    /**
     * Converte string de tempo para minutos desde meia-noite
     */
    private int parseTime(String timeString) {
        if (timeString == null || !timeString.matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]$")) {
            return -1;
        }

        String[] parts = timeString.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);

        if (hours < 0 || hours > 23 || minutes < 0 || minutes > 59) {
            return -1;
        }

        return hours * 60 + minutes;
    }
}

