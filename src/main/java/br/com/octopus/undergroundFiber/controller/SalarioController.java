package br.com.octopus.undergroundFiber.controller;

import br.com.octopus.undergroundFiber.service.SalarioService;
import br.com.octopus.undergroundFiber.suport.dtos.SalarioDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @GetMapping("/mes-atual")
    public ResponseEntity<List<SalarioDTO>> getSalariosDoMesAtual() {
        List<SalarioDTO> salarios = salarioService.calcularSalariosDoMesAtual();
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