package br.com.octopus.projectA.suport.dtos;

import java.math.BigDecimal;
import java.time.YearMonth;

public record AdicaoSalarioDTO(
        Long id,
        TipoAdicaoDTO tipoAdicao,
        BigDecimal qtyAdicao,
        YearMonth mesAdicao,
        Long agenteId,
        String nomeAgente
) {}
