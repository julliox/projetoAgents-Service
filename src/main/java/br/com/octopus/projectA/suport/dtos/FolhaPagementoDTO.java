package br.com.octopus.projectA.suport.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FolhaPagementoDTO {

    private Long agentId;
    private String agentName;
    private String email;
    private String phoneNumber;
    private String desInfo;
    private LocalDate admissionDate;
    private String status;
    private BigDecimal salarioBase;

    private int totalTurns;

    private List<TurnoFolhaPagamentoDTO> turnos;

}
