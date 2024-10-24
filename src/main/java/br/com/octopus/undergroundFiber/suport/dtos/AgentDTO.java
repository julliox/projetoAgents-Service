package br.com.octopus.undergroundFiber.suport.dtos;

import br.com.octopus.undergroundFiber.entity.enuns.StatusEnum;

import java.time.LocalDate;

public record AgentDTO(
        Long id,
        String name,
        String email,
        String phoneNumber,
        String desInfo,
        LocalDate admissionDate,
        StatusEnum status
) {}
