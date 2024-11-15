package br.com.octopus.projectA.suport.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalarioDTO {
    private Long agenteId;
    private String agenteNome;
    private BigDecimal salarioBase;
    private BigDecimal salarioExtra;
    private BigDecimal salarioSubTotal;
    private BigDecimal salarioCincoPorcento;
    private BigDecimal salarioLiquido;
    private List<AdicaoSalarioDTO> adicionais;
}
