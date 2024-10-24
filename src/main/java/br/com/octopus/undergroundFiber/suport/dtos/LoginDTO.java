package br.com.octopus.undergroundFiber.suport.dtos;

import lombok.Data;
import org.hibernate.validator.constraints.br.CPF;

import javax.validation.constraints.NotBlank;

@Data
public class LoginDTO {

    @NotBlank
//    @CPF(message = "Deve ser informado um CPF válido")
    private String email;
    @NotBlank
    private String senha;

}
