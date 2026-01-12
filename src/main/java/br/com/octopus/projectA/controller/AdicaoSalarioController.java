package br.com.octopus.projectA.controller;

import br.com.octopus.projectA.service.SalarioService;
import br.com.octopus.projectA.suport.dtos.AdicaoSalarioCreateDTO;
import br.com.octopus.projectA.suport.dtos.AdicaoSalarioDTO;
import br.com.octopus.projectA.suport.dtos.AdicaoSalarioUpdateDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/add-salario")
@Tag(name = "Adição Salário Controller", description = "Endpoints para gerenciar adições de salário")
public class AdicaoSalarioController {
    @Autowired
    SalarioService salarioService;

    @GetMapping
    @Operation(summary = "Listar todas as adições de salário", description = "Retorna uma lista com todas as adições de salário cadastradas")
    public ResponseEntity<List<AdicaoSalarioDTO>> getAllAdicaoSalario() {
        List<AdicaoSalarioDTO> adicoes = salarioService.getAllAdicaoSalario();
        return ResponseEntity.ok(adicoes);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar adição de salário por ID", description = "Retorna uma adição de salário específica pelo seu ID")
    public ResponseEntity<?> getAdicaoSalarioById(@PathVariable Long id) {
        try {
            AdicaoSalarioDTO adicao = salarioService.getAdicaoSalarioById(id);
            return ResponseEntity.ok(adicao);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "Listar adições de salário por colaborador", description = "Retorna uma lista de adições de salário de um colaborador específico")
    public ResponseEntity<List<AdicaoSalarioDTO>> getAdicaoSalarioByEmployeeId(@PathVariable Long employeeId) {
        List<AdicaoSalarioDTO> adicoes = salarioService.getAllAdicaoSalarioByEmployeeId(employeeId);
        return ResponseEntity.ok(adicoes);
    }

    @PostMapping
    @Operation(summary = "Criar nova adição de salário", description = "Cria uma nova adição de salário para um colaborador")
    public ResponseEntity<?> createAdicionalSalario(@RequestBody AdicaoSalarioCreateDTO adicaoSalarioDTO) {
        try {
            AdicaoSalarioDTO createdAdicao = salarioService.createAdicionalsalario(adicaoSalarioDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAdicao);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar adição de salário", description = "Atualiza uma adição de salário existente pelo seu ID")
    public ResponseEntity<?> updateAdicaoSalario(@PathVariable Long id, @RequestBody AdicaoSalarioUpdateDTO updateDTO) {
        try {
            AdicaoSalarioDTO updatedAdicao = salarioService.updateAdicaoSalario(id, updateDTO);
            return ResponseEntity.ok(updatedAdicao);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar adição de salário", description = "Deleta uma adição de salário pelo seu ID")
    public ResponseEntity<?> deleteAdicaoSalario(@PathVariable Long id) {
        try {
            salarioService.deleteAdicaoSalario(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }
}
