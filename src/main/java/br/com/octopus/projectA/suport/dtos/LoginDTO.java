package br.com.octopus.projectA.suport.dtos;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LoginDTO {

    @NotBlank
//    @CPF(message = "Deve ser informado um CPF válido")
    private String email;
    @NotBlank
    private String senha;

}
