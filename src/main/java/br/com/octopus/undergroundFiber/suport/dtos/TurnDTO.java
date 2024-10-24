package br.com.octopus.undergroundFiber.suport.dtos;

import java.time.LocalDate;

public record TurnDTO(
        Long id,
        String typeTurn,
        String nomeAgente,
        LocalDate dataTurno,
        Long clientId
) {}
