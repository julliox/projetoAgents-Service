package br.com.octopus.projectA.controller;

import br.com.octopus.projectA.service.TurnService;
import br.com.octopus.projectA.suport.dtos.TurnCreateDTO;
import br.com.octopus.projectA.suport.dtos.TurnDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
        TurnDTO turn = turnService.getProjectById(id);
        return ResponseEntity.ok(turn);
    }

    @GetMapping
    @Operation(summary = "Get all turns", description = "Retrieve a list of all turns")
    public ResponseEntity<List<TurnDTO>> getAllProjects() {
        List<TurnDTO> turns = turnService.findAll();
        return ResponseEntity.ok(turns);
    }

    @GetMapping("/agente/{id}")
    @Operation(summary = "Get all turns by Agent ID", description = "Retrieve a list of all turns for a specific agent")
    public ResponseEntity<List<TurnDTO>> getAllTurnsByAgentID(@PathVariable Long id) {
        List<TurnDTO> turns = turnService.findAllTurnsByAgentId(id);
        return ResponseEntity.ok(turns);
    }

    @PostMapping
    @Operation(summary = "Create a new turn", description = "Create a new turn with the provided details")
    public ResponseEntity<TurnDTO> createProject(@RequestBody TurnCreateDTO turnDto) {
        TurnDTO createdTurn = turnService.create(turnDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTurn);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Edit a turn", description = "Edit a turn by its ID")
    public ResponseEntity<TurnDTO> updateProject(@PathVariable Long id, @RequestBody TurnDTO turnDto) {
        TurnDTO updatedTurn = turnService.update(id, turnDto);
        return ResponseEntity.ok(updatedTurn);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a Turn", description = "Delete a turn by its ID")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        turnService.delete(id);
        return ResponseEntity.noContent().build();
    }
}