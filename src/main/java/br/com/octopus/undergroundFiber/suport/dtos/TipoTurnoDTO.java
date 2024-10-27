package br.com.octopus.undergroundFiber.suport.dtos;

import java.math.BigDecimal;

public record TipoTurnoDTO(
        Long id,
        String descricao,
        BigDecimal valorJunior,
        BigDecimal valorSenior
) {}