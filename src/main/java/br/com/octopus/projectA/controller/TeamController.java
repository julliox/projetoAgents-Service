package br.com.octopus.projectA.controller;

import br.com.octopus.projectA.entity.enuns.TeamStatus;
import br.com.octopus.projectA.service.TeamService;
import br.com.octopus.projectA.suport.dtos.TeamDtos;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/teams")
@Tag(name = "Team Controller", description = "Endpoints for managing teams")
public class TeamController {

    @Autowired
    private TeamService teamService;

    /**
     * Lista todas as equipes com paginação e filtros
     */
    @GetMapping
    @Operation(summary = "List all teams", description = "Retrieve a paginated list of teams with optional filters")
    public ResponseEntity<Page<TeamDtos.TeamResponse>> getTeams(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "sort", defaultValue = "name") String sort,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        TeamStatus teamStatus = null;
        if (status != null && !status.isEmpty()) {
            try {
                teamStatus = TeamStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Se o status não for válido, ignora o filtro
            }
        }

        Page<TeamDtos.TeamResponse> teams = teamService.getTeams(search, teamStatus, page, size, sort, direction);
        return ResponseEntity.ok(teams);
    }

    /**
     * Obtém uma equipe por ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get a team by ID", description = "Retrieve a team by its unique ID")
    public ResponseEntity<TeamDtos.TeamResponse> getTeamById(@PathVariable Long id) {
        TeamDtos.TeamResponse team = teamService.getTeamById(id);
        return ResponseEntity.ok(team);
    }

    /**
     * Cria uma nova equipe
     */
    @PostMapping
    @Operation(summary = "Create a new team", description = "Create a new team with the provided details")
    public ResponseEntity<TeamDtos.TeamResponse> createTeam(@Valid @RequestBody TeamDtos.CreateTeamRequest request) {
        TeamDtos.TeamResponse createdTeam = teamService.createTeam(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTeam);
    }

    /**
     * Atualiza uma equipe existente
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update a team", description = "Update an existing team by its ID")
    public ResponseEntity<TeamDtos.TeamResponse> updateTeam(
            @PathVariable Long id,
            @Valid @RequestBody TeamDtos.UpdateTeamRequest request
    ) {
        // Garante que o ID do path corresponde ao ID do body
        if (!id.equals(request.getId())) {
            return ResponseEntity.badRequest().build();
        }
        TeamDtos.TeamResponse updatedTeam = teamService.updateTeam(request);
        return ResponseEntity.ok(updatedTeam);
    }

    /**
     * Remove uma equipe
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a team", description = "Delete a team by its ID")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Atualiza apenas o status de uma equipe
     */
    @PatchMapping("/{id}/status")
    @Operation(summary = "Update team status", description = "Update only the status of a team")
    public ResponseEntity<TeamDtos.TeamResponse> updateTeamStatus(
            @PathVariable Long id,
            @Valid @RequestBody TeamDtos.TeamStatusRequest request
    ) {
        TeamDtos.TeamResponse updatedTeam = teamService.updateTeamStatus(id, request.getStatus());
        return ResponseEntity.ok(updatedTeam);
    }

    /**
     * Obtém estatísticas das equipes
     */
    @GetMapping("/statistics")
    @Operation(summary = "Get team statistics", description = "Retrieve statistics about teams")
    public ResponseEntity<TeamDtos.TeamStatistics> getTeamStatistics() {
        TeamDtos.TeamStatistics statistics = teamService.getTeamStatistics();
        return ResponseEntity.ok(statistics);
    }
}

