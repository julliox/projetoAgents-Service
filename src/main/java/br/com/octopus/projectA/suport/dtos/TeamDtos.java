package br.com.octopus.projectA.suport.dtos;

import br.com.octopus.projectA.entity.enuns.TeamStatus;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

public class TeamDtos {

    @Data
    public static class CreateTeamRequest {
        @NotBlank(message = "Nome da equipe é obrigatório")
        @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
        private String name;

        @NotBlank(message = "Horário de início é obrigatório")
        @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "Formato de horário deve ser HH:mm")
        private String workStartTime;

        @NotBlank(message = "Horário de fim é obrigatório")
        @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "Formato de horário deve ser HH:mm")
        private String workEndTime;

        private List<Long> agentIds;
    }

    @Data
    public static class UpdateTeamRequest {
        @NotNull(message = "ID da equipe é obrigatório")
        private Long id;

        @NotBlank(message = "Nome da equipe é obrigatório")
        @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
        private String name;

        @NotBlank(message = "Horário de início é obrigatório")
        @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "Formato de horário deve ser HH:mm")
        private String workStartTime;

        @NotBlank(message = "Horário de fim é obrigatório")
        @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "Formato de horário deve ser HH:mm")
        private String workEndTime;

        @NotNull(message = "Status é obrigatório")
        private TeamStatus status;

        private List<Long> agentIds;
    }

    @Data
    public static class AgentSummary {
        private Long id;
        private String name;
        private String email;
        private String phoneNumber;
    }

    @Data
    public static class TeamResponse {
        private Long id;
        private String name;
        private String workStartTime;
        private String workEndTime;
        private TeamStatus status;
        private List<AgentSummary> agents;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String workTimeFormatted;
        private Double durationHours;
        private Integer agentsCount;
    }

    @Data
    public static class TeamStatusRequest {
        @NotNull(message = "Status é obrigatório")
        private TeamStatus status;
    }

    @Data
    public static class TeamStatistics {
        private long totalTeams;
        private long activeTeams;
        private long inactiveTeams;
        private long totalAgents;
        private Double averageAgentsPerTeam;
    }
}

