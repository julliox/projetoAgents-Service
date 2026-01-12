package br.com.octopus.projectA.suport.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.time.YearMonth;

@Data
public class AdicaoSalarioUpdateDTO {

    private Long tipoAdicaoId;
    private BigDecimal qtyAdicao;
    private YearMonth mesAdicao;
}
