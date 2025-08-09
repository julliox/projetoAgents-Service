package br.com.octopus.projectA.suport.dtos;

import lombok.Data;

import java.time.YearMonth;

@Data
public class RequestFolhaPagamentoDTO {

    private Long idAgente;
    private String nomeAgente;
    private YearMonth mesPagamento;

}
