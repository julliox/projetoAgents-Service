package br.com.octopus.undergroundFiber.controller;

import br.com.octopus.undergroundFiber.service.TurnService;
import br.com.octopus.undergroundFiber.suport.dtos.TurnCreateDTO;
import br.com.octopus.undergroundFiber.suport.dtos.TurnDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/turno")
@Tag(name = "Turno Controller", description = "Endpoints for managing turno")
public class TurnController {

    @Autowired
    private TurnService turnService;

    @GetMapping("/{id}")
    @Operation(summary = "Get a turno by ID", description = "Retrieve a turno by its unique ID")
    public ResponseEntity<TurnDTO> getProjectById(@PathVariable Long id) {
        return ResponseEntity.ok(turnService.getProjectById(id));
    }

    @GetMapping
    @Operation(summary = "Get all turns", description = "Retrieve a list of all turns")
    public ResponseEntity<List<TurnDTO>> getAllProjects() {
        return ResponseEntity.ok(turnService.findAll());
    }

    @GetMapping("/agente/{id}")
    @Operation(summary = "Get all turns", description = "Retrieve a list of all turns")
    public ResponseEntity<List<TurnDTO>> getAllTurnsByAgentID(@PathVariable Long id) {
        return ResponseEntity.ok(turnService.findAllTurnsByAgentId(id));
    }

    @PostMapping
    @Operation(summary = "Create a new turn", description = "Create a new turn with the provided details")
    public ResponseEntity<TurnDTO> createProject(@RequestBody TurnCreateDTO turnDto) {
        return ResponseEntity.ok(turnService.create(turnDto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Edit a turn", description = "Edit a turn by its ID")
    public ResponseEntity<TurnDTO> updateProject(@PathVariable Long id, @RequestBody TurnDTO turnDto) {
        return ResponseEntity.ok(turnService.update(id, turnDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a Turn", description = "Delete a turn by its ID")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        turnService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
