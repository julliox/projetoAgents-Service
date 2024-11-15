package br.com.octopus.projectA.suport.dtos;

import br.com.octopus.projectA.entity.enuns.StatusEnum;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AgentProfileDTO (
        Long id,
        String name,
        String email,
        String phoneNumber,
        String desInfo,
        LocalDate admissionDate,
        BigDecimal salarioBase,
        StatusEnum status
){}


