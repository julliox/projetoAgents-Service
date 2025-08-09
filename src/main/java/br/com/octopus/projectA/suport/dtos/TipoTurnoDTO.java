package br.com.octopus.projectA.suport.dtos;

import java.math.BigDecimal;

public record TipoTurnoDTO(
        Long id,
        String descricao,
        String cod,
        BigDecimal valorJunior,
        BigDecimal valorSenior
) {}