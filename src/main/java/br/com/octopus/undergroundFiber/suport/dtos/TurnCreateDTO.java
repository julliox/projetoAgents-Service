package br.com.octopus.undergroundFiber.suport.dtos;

import java.time.LocalDate;
import java.util.List;

public record TurnCreateDTO(
        Long id,
        String typeTurn,
        String nomeAgente,
        List<LocalDate> dataTurno,
        Long clientId
) {}
