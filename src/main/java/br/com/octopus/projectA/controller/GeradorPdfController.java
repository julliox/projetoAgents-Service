package br.com.octopus.projectA.controller;


import br.com.octopus.projectA.service.GeradorPdfService;
import br.com.octopus.projectA.suport.dtos.DadosFolhaPagementoDTO;
import br.com.octopus.projectA.suport.dtos.RequestFolhaPagamentoDTO;
import com.itextpdf.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("/pdfDecisaoSemAssinatura")
    public ResponseEntity<byte[]> gerarPdfDecisaoSemAssinatura(@RequestBody RequestFolhaPagamentoDTO requestFolhaPagamentoDTO) throws DocumentException, IOException {
        return ResponseEntity.ok(geradorPdfService.gerarFolhaPagemnto(TEMPLATE_FOLHA_PAGAMENTO, requestFolhaPagamentoDTO));
    }

    @PostMapping("/teste")
    public ResponseEntity<DadosFolhaPagementoDTO> teste(@RequestBody RequestFolhaPagamentoDTO requestFolhaPagamentoDTO) throws DocumentException, IOException {
        return ResponseEntity.ok(geradorPdfService.geraDadosFolhaPagemento(requestFolhaPagamentoDTO.getIdAgente(), requestFolhaPagamentoDTO.getMesPagamento()));
    }
}
