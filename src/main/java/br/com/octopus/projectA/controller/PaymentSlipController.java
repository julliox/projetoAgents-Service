package br.com.octopus.projectA.controller;

import br.com.octopus.projectA.service.PaymentSlipService;
import br.com.octopus.projectA.suport.dtos.FolhaPagementoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/payment-slip")
public class PaymentSlipController {

    @Autowired
    private PaymentSlipService paymentSlipService;

    @GetMapping("/generate/{agentId}")
    public ResponseEntity<FolhaPagementoDTO> generatePaymentSlip(@PathVariable Long agentId) {
        return ResponseEntity.ok(paymentSlipService.geraDadosFolhaDePagamentoPorAgenteId(agentId));
    }


}
