package br.com.octopus.projectA.controller;


import br.com.octopus.projectA.service.GeradorPdfService;
import br.com.octopus.projectA.suport.dtos.DadosFolhaPagementoDTO;
import br.com.octopus.projectA.suport.dtos.RequestFolhaPagamentoDTO;
import com.itextpdf.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/testeFolha")
public class GeradorPdfController {
    @Autowired
    GeradorPdfService geradorPdfService;

    private final String TEMPLATE_FOLHA_PAGAMENTO = "templatesHTML/folhaPagamento";

    @PostMapping("/gerar-pdf-folha-de-pagamento")
    public ResponseEntity<byte[]> gerarPdfFolhaDePagamento(@RequestBody RequestFolhaPagamentoDTO requestFolhaPagamentoDTO) throws DocumentException, IOException {
        try {
            byte[] pdfBytes = geradorPdfService.gerarFolhaPagemnto(TEMPLATE_FOLHA_PAGAMENTO, requestFolhaPagamentoDTO);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF); // MediaType.APPLICATION_PDF é "application/pdf"
            String filename = "folha_pagamento_" + requestFolhaPagamentoDTO.getIdAgente() + ".pdf";
            headers.setContentDispositionFormData(filename, filename);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }

    @PostMapping("/teste")
    public ResponseEntity<DadosFolhaPagementoDTO> teste(@RequestBody RequestFolhaPagamentoDTO requestFolhaPagamentoDTO) throws DocumentException, IOException {
        return ResponseEntity.ok(geradorPdfService.geraDadosFolhaPagemento(requestFolhaPagamentoDTO.getIdAgente(), requestFolhaPagamentoDTO.getMesPagamento()));
    }
}
