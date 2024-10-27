package br.com.octopus.undergroundFiber.suport.dtos;

import java.time.LocalDate;

public record TurnDTO(
        Long id,
        TipoTurnoDTO tipoTurno,  // Inclusão do TipoTurnoDTO
        String nomeAgente,
        LocalDate dataTurno,
        Long agentId
) {}