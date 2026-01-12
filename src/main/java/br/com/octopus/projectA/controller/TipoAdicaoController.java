package br.com.octopus.projectA.controller;

import br.com.octopus.projectA.service.TipoAdicaoService;
import br.com.octopus.projectA.suport.dtos.TipoAdicaoDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/tipo-adicao")
@Tag(name = "Tipo Adição Controller", description = "Endpoints para gerenciar tipos de adição de salário")
public class TipoAdicaoController {

    @Autowired
    private TipoAdicaoService tipoAdicaoService;

    @GetMapping
    @Operation(summary = "Listar todos os tipos de adição", description = "Retorna uma lista com todos os tipos de adição de salário cadastrados")
    public ResponseEntity<List<TipoAdicaoDTO>> getAllTiposAdicao() {
        List<TipoAdicaoDTO> tipos = tipoAdicaoService.getAllTiposAdicao();
        return ResponseEntity.ok(tipos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar tipo de adição por ID", description = "Retorna um tipo de adição específico pelo seu ID")
    public ResponseEntity<TipoAdicaoDTO> getTipoAdicaoById(@PathVariable Long id) {
        try {
            TipoAdicaoDTO tipo = tipoAdicaoService.getTipoAdicaoById(id);
            return ResponseEntity.ok(tipo);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping
    @Operation(summary = "Criar novo tipo de adição", description = "Cria um novo tipo de adição de salário")
    public ResponseEntity<?> createTipoAdicao(@RequestBody TipoAdicaoDTO tipoAdicaoDTO) {
        try {
            TipoAdicaoDTO createdTipo = tipoAdicaoService.createTipoAdicao(tipoAdicaoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTipo);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar tipo de adição", description = "Atualiza um tipo de adição existente pelo seu ID")
    public ResponseEntity<?> updateTipoAdicao(@PathVariable Long id, @RequestBody TipoAdicaoDTO tipoAdicaoDTO) {
        try {
            TipoAdicaoDTO updatedTipo = tipoAdicaoService.updateTipoAdicao(id, tipoAdicaoDTO);
            return ResponseEntity.ok(updatedTipo);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar tipo de adição", description = "Deleta um tipo de adição pelo seu ID")
    public ResponseEntity<?> deleteTipoAdicao(@PathVariable Long id) {
        try {
            tipoAdicaoService.deleteTipoAdicao(id);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }
}
