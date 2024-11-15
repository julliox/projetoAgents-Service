package br.com.octopus.projectA.suport.dtos;

import br.com.octopus.projectA.entity.enuns.StatusEnum;

public record ProfileDTO(
        Long id,
        String name,
        StatusEnum status
) {
}
