package br.com.octopus.undergroundFiber.controller;

import br.com.octopus.undergroundFiber.service.AgentService;
import br.com.octopus.undergroundFiber.suport.dtos.AgentDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/agents")
@Tag(name = "Agent Controller", description = "Endpoints for managing agents")
public class AgentController {

    @Autowired
    private AgentService agentService;

    @GetMapping("/{id}")
    @Operation(summary = "Get a agent by ID", description = "Retrieve a agent by its unique ID")
    public ResponseEntity<AgentDTO> getClientById(@PathVariable Long id) {
        return ResponseEntity.ok(agentService.getClientById(id));
    }

    @GetMapping
    @Operation(summary = "Get all agents", description = "Retrieve a list of all agents")
    public ResponseEntity<List<AgentDTO>> getAllClients() {
        return ResponseEntity.ok(agentService.findAll());
    }

    @PostMapping
    @Operation(summary = "Create a new agent", description = "Create a new agent with the provided details")
    public ResponseEntity<AgentDTO> createClient(@RequestBody AgentDTO agentDto) {
        return ResponseEntity.ok(agentService.create(agentDto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a agent", description = "Update an existing agent by its ID")
    public ResponseEntity<AgentDTO> updateClient(@PathVariable Long id, @RequestBody AgentDTO agentDto) {
        return ResponseEntity.ok(agentService.update(id, agentDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a agent", description = "Delete a agent by its ID")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        agentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
