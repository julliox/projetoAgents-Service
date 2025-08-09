package br.com.octopus.projectA.suport.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class DadosFolhaPagementoDTO {

    private Long idAgente;
    private String nomeAgente;
    private String dataAdmissao;
    private String mesAtual;
    private List<ItemsSalarioDTO> itemsFolhaPagamento;
    private BigDecimal extras;
    private BigDecimal adicionas;
    private BigDecimal salarioCincoPorcento;
    private BigDecimal salarioLiquido;
    private BigDecimal salarioSubtotalWithDesc;
    private BigDecimal salarioSubtotal;
    private BigDecimal salarioBase;
    private BigDecimal descontos;
}
