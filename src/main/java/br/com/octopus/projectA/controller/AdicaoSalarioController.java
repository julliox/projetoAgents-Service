package br.com.octopus.projectA.controller;


import br.com.octopus.projectA.service.SalarioService;
import br.com.octopus.projectA.suport.dtos.AdicaoSalarioCreateDTO;
import br.com.octopus.projectA.suport.dtos.AdicaoSalarioDTO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/add-salario")
public class AdicaoSalarioController {
    @Autowired
    SalarioService salarioService;

    @PostMapping
    @Operation(summary = "", description = "")
    public ResponseEntity<AdicaoSalarioDTO> createAdicionalSalario(@RequestBody AdicaoSalarioCreateDTO adicaoSalarioDTO) {
        AdicaoSalarioDTO createdTurn = salarioService.createAdicionalsalario(adicaoSalarioDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTurn);
    }

}
