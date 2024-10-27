package br.com.octopus.undergroundFiber.suport.dtos;

import br.com.octopus.undergroundFiber.entity.enuns.StatusEnum;

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


