package br.com.octopus.projectA.suport.dtos;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TurnoFolhaPagamentoDTO {

    private LocalDate turnDate;
    private String turnDescription;
    private BigDecimal turnValue;



}
