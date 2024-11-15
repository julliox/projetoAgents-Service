package br.com.octopus.projectA.suport.dtos;

import br.com.octopus.projectA.entity.enuns.StatusEnum;

public record UserDTO(
        Long id,
        String name,
        String email,
        String profile,
        StatusEnum status
) {
}
