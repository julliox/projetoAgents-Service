package br.com.octopus.projectA.suport.dtos;

import br.com.octopus.projectA.entity.enuns.StatusEnum;

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
