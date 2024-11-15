package br.com.octopus.projectA.controller;

import br.com.octopus.projectA.service.SalarioService;
import br.com.octopus.projectA.suport.dtos.DataEscolhidaDTO;
import br.com.octopus.projectA.suport.dtos.SalarioDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/salarios")
public class SalarioController {

    @Autowired
    private SalarioService salarioService;

    /**
     * Endpoint para obter os salários dos agentes do mês atual.
     *
     * @return Lista de SalarioDTO com os salários calculados.
     */
    @PostMapping("/mes")
    public ResponseEntity<List<SalarioDTO>> getSalariosByMes(@RequestBody DataEscolhidaDTO dataEscolhidaDTO) {
        List<SalarioDTO> salarios = salarioService.calcularSalariosDoMesAtual(dataEscolhidaDTO);
        return ResponseEntity.ok(salarios);
    }

    /**
     * Endpoint para obter o salário de um único agente do mês atual.
     *
     * @param agenteId ID do agente.
     * @return SalarioDTO com o salário calculado.
     */
    @GetMapping("/agente/{agenteId}")
    public ResponseEntity<SalarioDTO> getSalarioPorAgente(@PathVariable Long agenteId) {
        try {
            SalarioDTO salario = salarioService.calcularSalarioPorAgente(agenteId);
            return ResponseEntity.ok(salario);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}