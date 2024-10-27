package br.com.octopus.undergroundFiber.suport.dtos;

import java.time.LocalDate;
import java.util.List;

public record TurnCreateDTO(
        Long tipoTurnoId,    // Referência ao TipoTurno
        Long agentId,
        List<LocalDate> dataTurno
) {}