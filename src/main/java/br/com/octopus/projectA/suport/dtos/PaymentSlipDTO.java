package br.com.octopus.projectA.suport.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSlipDTO {

    private Long agentId;
    private String agentName;
    private String email;
    private String phoneNumber;
    private String desInfo;
    private LocalDate admissionDate;
    private String status;

    private Long turnId;
    private LocalDate turnDate;
    private String turnDescription;
    private BigDecimal turnValue;

}
