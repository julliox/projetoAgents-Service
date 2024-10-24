package br.com.octopus.undergroundFiber.suport.dtos;

import br.com.octopus.undergroundFiber.entity.enuns.StatusEnum;

public record ProfileDTO(
        Long id,
        String name,
        StatusEnum status
) {
}
