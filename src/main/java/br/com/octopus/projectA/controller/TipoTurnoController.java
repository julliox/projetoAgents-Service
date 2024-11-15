// src/main/java/br/com/octopus/undergroundFiber/controller/TipoTurnoController.java

package br.com.octopus.projectA.controller;

import br.com.octopus.projectA.suport.dtos.TipoTurnoDTO;
import br.com.octopus.projectA.service.TipoTurnoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/tiposTurno")
@CrossOrigin(origins = "*") // Ajuste conforme necessário para CORS
public class TipoTurnoController {

    @Autowired
    private TipoTurnoService tipoTurnoService;

    /**
     * Busca todos os tipos de turno.
     * @return Lista de TipoTurnoDTO
     */
    @GetMapping
    public ResponseEntity<List<TipoTurnoDTO>> getAllTiposTurno() {
        List<TipoTurnoDTO> tipos = tipoTurnoService.getAllTipoTurnos();
        return ResponseEntity.ok(tipos);
    }

    /**
     * Busca um tipo de turno por ID.
     * @param id ID do TipoTurno
     * @return TipoTurnoDTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<TipoTurnoDTO> getTipoTurnoById(@PathVariable Long id) {
        try {
            TipoTurnoDTO tipo = tipoTurnoService.getTipoTurnoById(id);
            return ResponseEntity.ok(tipo);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * Cria um novo tipo de turno.
     * @param tipoTurnoDTO Dados para criação
     * @return TipoTurnoDTO criado
     */
    @PostMapping
    public ResponseEntity<?> createTipoTurno(@RequestBody TipoTurnoDTO tipoTurnoDTO) {
        try {
            TipoTurnoDTO createdTipo = tipoTurnoService.createTipoTurno(tipoTurnoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTipo);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    /**
     * Atualiza um tipo de turno existente.
     * @param id ID do TipoTurno a ser atualizado
     * @param tipoTurnoDTO Dados atualizados
     * @return TipoTurnoDTO atualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTipoTurno(@PathVariable Long id, @RequestBody TipoTurnoDTO tipoTurnoDTO) {
        try {
            TipoTurnoDTO updatedTipo = tipoTurnoService.updateTipoTurno(id, tipoTurnoDTO);
            return ResponseEntity.ok(updatedTipo);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    /**
     * Deleta um tipo de turno por ID.
     * @param id ID do TipoTurno a ser deletado
     * @return ResponseEntity vazio com status OK
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTipoTurno(@PathVariable Long id) {
        try {
            tipoTurnoService.deleteTipoTurno(id);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }
}
