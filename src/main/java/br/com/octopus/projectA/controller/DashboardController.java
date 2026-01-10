package br.com.octopus.projectA.controller;

import br.com.octopus.projectA.service.DashboardService;
import br.com.octopus.projectA.suport.dtos.DashboardDtos;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.format.DateTimeParseException;

@RestController
@RequestMapping("/dashboard")
@Tag(name = "Dashboard Controller", description = "Endpoints para o Dashboard Administrativo")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    /**
     * Endpoint 1: Contagem de Agentes Online
     * GET /api/project_a/dashboard/agents/online/count
     */
    @GetMapping("/agents/online/count")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @Operation(summary = "Contagem de agentes online", 
               description = "Retorna a quantidade total de agentes que estão ONLINE no momento atual")
    public ResponseEntity<?> getOnlineAgentsCount() {
        try {
            DashboardDtos.OnlineAgentsCountResponse response = dashboardService.getOnlineAgentsCount();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            DashboardDtos.ErrorResponse errorResponse = new DashboardDtos.ErrorResponse();
            errorResponse.setError("Erro ao buscar contagem de agentes online");
            errorResponse.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Endpoint 2: Histórico de Status dos Agentes
     * GET /api/project_a/dashboard/agents/status/history
     */
    @GetMapping("/agents/status/history")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @Operation(summary = "Histórico de status dos agentes", 
               description = "Retorna o histórico de mudanças de status (ONLINE/OFFLINE) dos agentes, " +
                           "ordenado por data mais recente primeiro, com suporte a paginação e filtros")
    public ResponseEntity<?> getAgentStatusHistory(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "agentId", required = false) Long agentId,
            @RequestParam(value = "startDate", required = false) String startDateStr,
            @RequestParam(value = "endDate", required = false) String endDateStr
    ) {
        try {
            // Parse das datas ISO 8601
            Instant startDate = null;
            Instant endDate = null;

            if (startDateStr != null && !startDateStr.isEmpty()) {
                try {
                    startDate = Instant.parse(startDateStr);
                } catch (DateTimeParseException e) {
                    DashboardDtos.ErrorResponse errorResponse = new DashboardDtos.ErrorResponse();
                    errorResponse.setError("Formato de data inválido");
                    errorResponse.setMessage("startDate deve estar no formato ISO 8601 (ex: 2024-01-15T00:00:00Z)");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
                }
            }

            if (endDateStr != null && !endDateStr.isEmpty()) {
                try {
                    endDate = Instant.parse(endDateStr);
                } catch (DateTimeParseException e) {
                    DashboardDtos.ErrorResponse errorResponse = new DashboardDtos.ErrorResponse();
                    errorResponse.setError("Formato de data inválido");
                    errorResponse.setMessage("endDate deve estar no formato ISO 8601 (ex: 2024-01-15T23:59:59Z)");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
                }
            }

            DashboardDtos.AgentStatusHistoryPage response = dashboardService.getAgentStatusHistory(
                    page,
                    size,
                    status,
                    agentId,
                    startDate,
                    endDate
            );

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            DashboardDtos.ErrorResponse errorResponse = new DashboardDtos.ErrorResponse();
            errorResponse.setError("Parâmetro inválido");
            errorResponse.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            DashboardDtos.ErrorResponse errorResponse = new DashboardDtos.ErrorResponse();
            errorResponse.setError("Erro ao buscar histórico de status");
            errorResponse.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
