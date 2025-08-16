package br.com.octopus.projectA.controller;

import br.com.octopus.projectA.security.UserDetailsImpl;
import br.com.octopus.projectA.service.PunchService;
import br.com.octopus.projectA.suport.dtos.PunchDtos;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Instant;

@RestController
@RequestMapping("/ponto")
public class PunchController {

    @Autowired
    private PunchService punchService;

    // Mantido caso precise no futuro para obter usuário corrente com mais contexto
    // @Autowired
    // private AuthenticationCurrentUserService authCurrentUser;

    private boolean isAdmin(Authentication auth) {
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRATOR"));
    }

    private Long resolveAgentId(Authentication auth, Long maybeAgentId) {
        if (isAdmin(auth)) {
            if (maybeAgentId == null) throw new IllegalArgumentException("agentId é obrigatório para ADMIN");
            return maybeAgentId;
        } else {
            UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();
            if (maybeAgentId != null && !maybeAgentId.equals(user.getId())) {
                throw new AccessDeniedException("AGENT não pode operar para outro usuário");
            }
            return user.getId(); // agentId = userId
        }
    }

    @PostMapping("/punch")
    @PreAuthorize("hasAnyRole('AGENT','ADMINISTRATOR')")
    @Operation(summary = "Registrar ponto (ENTRADA/SAIDA)")
    public ResponseEntity<PunchDtos.PunchResponse> punch(
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody PunchDtos.PunchRequest request,
            Authentication authentication
    ) {
        Long agentId = resolveAgentId(authentication, request.getAgentId());

        // Idempotência
        if (idempotencyKey != null) {
            var existing = punchService.findByIdempotencyKey(idempotencyKey);
            if (existing.isPresent()) {
                return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(existing.get(), true));
            }
        }

        var saved = punchService.registerPunch(agentId, request, idempotencyKey);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved, false));
    }

    @GetMapping("/state")
    @PreAuthorize("hasAnyRole('AGENT','ADMINISTRATOR')")
    @Operation(summary = "Estado atual do ponto")
    public ResponseEntity<PunchDtos.StateResponse> state(
            @RequestParam(value = "agentId", required = false) Long agentId,
            Authentication authentication
    ) {
        Long resolved = resolveAgentId(authentication, agentId);
        return ResponseEntity.ok(punchService.getState(resolved));
    }

    @GetMapping("/history")
    @PreAuthorize("hasAnyRole('AGENT','ADMINISTRATOR')")
    @Operation(summary = "Histórico de pontos (paginação e período)")
    public ResponseEntity<PunchDtos.HistoryResponse> history(
            @RequestParam(value = "agentId", required = false) Long agentId,
            @RequestParam(value = "from", required = false) Instant from,
            @RequestParam(value = "to", required = false) Instant to,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "sort", defaultValue = "timestampServer,desc") String sort,
            Authentication authentication
    ) {
        Long resolved = resolveAgentId(authentication, agentId);
        Sort springSort = parseSort(sort);
        return ResponseEntity.ok(punchService.getHistory(resolved, from, to, page, size, springSort));
    }

    @GetMapping("/last-status")
    @PreAuthorize("hasAnyRole('AGENT','ADMINISTRATOR')")
    @Operation(summary = "Último status (ENTRADA/SAIDA) pelo agentId")
    public ResponseEntity<PunchDtos.LastStatusResponse> lastStatus(
            @RequestParam(value = "agentId", required = false) Long agentId,
            Authentication authentication
    ) {
        Long resolved = resolveAgentId(authentication, agentId);
        return ResponseEntity.ok(punchService.getLastStatus(resolved));
    }

    private Sort parseSort(String sortParam) {
        try {
            String[] parts = sortParam.split(",");
            String prop = parts[0];
            Sort.Direction dir = parts.length > 1 && parts[1].equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
            return Sort.by(dir, prop);
        } catch (Exception e) {
            return Sort.by(Sort.Direction.DESC, "timestampServer");
        }
    }

    private PunchDtos.PunchResponse toResponse(br.com.octopus.projectA.entity.PunchEntity e, boolean reused) {
        PunchDtos.PunchResponse resp = new PunchDtos.PunchResponse();
        resp.setId(e.getId());
        resp.setAgentId(e.getAgentId());
        resp.setType(e.getType());
        resp.setTimestampServer(e.getTimestampServer());
        resp.setTimestampEffective(e.getTimestampServer());
        resp.setStatus("RECORDED");

        var state = punchService.getState(e.getAgentId());
        resp.setClockedIn(state.isClockedIn());
        if (state.getActiveSession() != null) {
            resp.setSession(state.getActiveSession());
        }
        resp.setServerTime(Instant.now());
        return resp;
    }
}


