package br.com.octopus.undergroundFiber.suport.dtos;

import br.com.octopus.undergroundFiber.entity.enuns.StatusEnum;

public record UserDTO(
        Long id,
        String name,
        String email,
        String profile,
        StatusEnum status
) {
}
