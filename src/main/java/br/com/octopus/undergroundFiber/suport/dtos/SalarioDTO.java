package br.com.octopus.undergroundFiber.suport.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalarioDTO {
    private Long agenteId;
    private String agenteNome;
    private BigDecimal salario;
}
