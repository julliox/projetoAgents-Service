package br.com.octopus.projectA.suport.dtos;

import java.time.LocalDate;
import java.util.List;

public record TurnCreateDTO(
        Long tipoTurnoId,    // Referência ao TipoTurno
        Long agentId,
        List<LocalDate> dataTurno
) {}