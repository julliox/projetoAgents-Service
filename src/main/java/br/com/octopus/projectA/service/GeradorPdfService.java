package br.com.octopus.projectA.service;

import br.com.octopus.projectA.Util.FormatadorUtil;
import br.com.octopus.projectA.Util.HTMLToPdf.HtmlToPdfUtil;
import br.com.octopus.projectA.Util.HTMLToPdf.TipoRenderer;
import br.com.octopus.projectA.entity.AdicaoSalarioEntity;
import br.com.octopus.projectA.entity.AgentEntity;
import br.com.octopus.projectA.entity.TurnEntity;
import br.com.octopus.projectA.repository.AdicaoSalarioRepository;
import br.com.octopus.projectA.repository.AgentRepository;
import br.com.octopus.projectA.repository.TurnRepository;
import br.com.octopus.projectA.suport.dtos.*;
import com.itextpdf.text.Document;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GeradorPdfService {
    @Autowired
    private SalarioService salarioService;

    @Autowired
    private TurnRepository turnRepository;

    @Autowired
    private AgentRepository agentRepository;
    @Autowired
    private AdicaoSalarioRepository adicaoSalarioRepository;


    public byte[] gerarFolhaPagemnto(String caminhotemplate, RequestFolhaPagamentoDTO request) throws IOException, DocumentException {
        DadosFolhaPagementoDTO dadosFolha = geraDadosFolhaPagemento(request.getIdAgente(), request.getMesPagamento());

        byte[] pdfSimples = HtmlToPdfUtil.gerarPdfCaminhoDeTemplateEDados(caminhotemplate, dadosFolha, TipoRenderer.HTML_HANDLEBARS);


        byte[] pdfComMargem = adicionarMargemAoPdf(pdfSimples, 20, 20, 20, 0);

        return pdfComMargem;
    }


    public static byte[] adicionarMargemAoPdf(byte[] pdfBytes, float margemEsquerda, float margemDireita, float margemTopo, float margemBase) throws IOException, com.itextpdf.text.DocumentException {
        // Leitura do PDF original em byte[]
        PdfReader reader = new PdfReader(new ByteArrayInputStream(pdfBytes));

        // Criar o novo documento com as margens desejadas
        Document document = new Document();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);

        document.open();
        PdfContentByte contentByte = writer.getDirectContent();

        // Número total de páginas no PDF original
        int totalPaginas = reader.getNumberOfPages();

        // Tamanho da página original
        Rectangle tamanhoOriginal = reader.getPageSize(1);

        // Ajustar as margens para o novo documento
        float larguraNova = tamanhoOriginal.getWidth() - (margemEsquerda + margemDireita);
        float alturaNova = tamanhoOriginal.getHeight() - (margemTopo + margemBase);

        // Iterar sobre todas as páginas do PDF original
        for (int i = 1; i <= totalPaginas; i++) {
            // Criar uma nova página no documento com o mesmo tamanho da original
            document.newPage();

            // Importar a página original
            PdfImportedPage paginaImportada = writer.getImportedPage(reader, i);

            // Calcular a escala para ajustar o conteúdo ao novo tamanho da página com margens
            float escalaX = larguraNova / tamanhoOriginal.getWidth();
            float escalaY = alturaNova / tamanhoOriginal.getHeight();

            // Posicionar o conteúdo original dentro das margens
            contentByte.addTemplate(paginaImportada, escalaX, 0, 0, escalaY, margemEsquerda, margemBase);
        }

        // Fechar o documento e os leitores
        document.close();
        reader.close();

        // Retornar o novo PDF como byte[]
        return outputStream.toByteArray();
    }

    public DadosFolhaPagementoDTO geraDadosFolhaPagemento (Long idAgente , YearMonth mesPagamento) {
        BigDecimal adicionais = BigDecimal.ZERO;
        DadosFolhaPagementoDTO dadosFolha = new DadosFolhaPagementoDTO();
        AgentEntity agentEntity = agentRepository.findById(idAgente).get();
        SalarioDTO salarioDTO = salarioService.calcularSalarioPorAgente(idAgente);

        dadosFolha.setSalarioSubtotal(salarioDTO.getSalarioSubTotal());
        dadosFolha.setSalarioLiquido(salarioDTO.getSalarioLiquido());
        dadosFolha.setSalarioCincoPorcento(salarioDTO.getSalarioCincoPorcento());
        dadosFolha.setExtras(salarioDTO.getSalarioExtra());
        dadosFolha.setSalarioSubtotalWithDesc(salarioDTO.getSalarioLiquido());
        dadosFolha.setSalarioBase(salarioDTO.getSalarioBase());

        dadosFolha.setDescontos(BigDecimal.valueOf(0));

        dadosFolha.setIdAgente(idAgente);
        dadosFolha.setNomeAgente(agentEntity.getName());
        dadosFolha.setDataAdmissao(agentEntity.getAdmissionDate().toString());

        dadosFolha.setMesAtual(FormatadorUtil.formatarYearMonth(mesPagamento));


        // Calcular a diferença em anos entre a data de admissão e a data atual
        long anosNaEmpresa = ChronoUnit.YEARS.between(agentEntity.getAdmissionDate(), LocalDate.now());

        // Determinar se o agente é Senior ou Junior
        boolean isSenior = anosNaEmpresa >= 1;

        List<AdicaoSalarioDTO> novaLista = salarioDTO.getAdicionais();
        for (AdicaoSalarioDTO adicaoSalarioDTO : novaLista) {
            adicionais = adicionais.add(adicaoSalarioDTO.qtyAdicao());
        }

        dadosFolha.setAdicionas(adicionais);

        LocalDate startOfMonth = mesPagamento.atDay(1);
        LocalDate endOfMonth = mesPagamento.atEndOfMonth();

        // Buscar todos os turnos do agente no mês atual
        List<TurnDTO> turnosDoAgente = turnRepository.findByAgentIdAndDataTurnoBetween(idAgente, startOfMonth, endOfMonth).stream()
                .map(this::turnToDto)
                .collect(Collectors.toList());

        // Buscar todas as adições de salário do agente no mês
        List<AdicaoSalarioDTO> adicaoSalarioDTOS = adicaoSalarioRepository.findAllByAgentIdAndMesAdicao(idAgente, mesPagamento).stream()
                .map(this::adicaoSalarioToDTO)
                .collect(Collectors.toList());

        // Agrupar os turnos por tipo
        Map<TipoTurnoDTO, List<TurnDTO>> turnosAgrupados = turnosDoAgente.stream()
                .collect(Collectors.groupingBy(TurnDTO::tipoTurno));

        // Popular itemsFolhaPagamento com os turnos
        List<ItemsSalarioDTO> itemsFolhaPagamento = turnosAgrupados.entrySet().stream()
                .map(entry -> criarItemFolhaPagamento(entry.getKey(), entry.getValue(), isSenior))
                .collect(Collectors.toList());

        // Adicionar as adições de salário à folha de pagamento
        List<ItemsSalarioDTO> itensAdicoes = adicaoSalarioDTOS.stream()
                .map(this::criarItemAdicaoSalario)
                .collect(Collectors.toList());

        // Combinar turnos e adições na folha de pagamento
        itemsFolhaPagamento.addAll(itensAdicoes);

        dadosFolha.setItemsFolhaPagamento(itemsFolhaPagamento);

        return dadosFolha;
    }


    private AdicaoSalarioDTO adicaoSalarioToDTO(AdicaoSalarioEntity entity) {
        if (entity == null) {
            return null;
        }

        TipoAdicaoDTO tipoAdicaoDTO = new TipoAdicaoDTO(
                entity.getTipoAdicao().getId(),
                entity.getTipoAdicao().getDesTipoAdicao()
        );

        return new AdicaoSalarioDTO(
                entity.getId(),
                tipoAdicaoDTO,
                entity.getQtyAdicao(),
                entity.getMesAdicao(),
                entity.getAgent().getId(),
                entity.getAgent().getName()
        );
    }

    private TurnDTO turnToDto(TurnEntity entity) {
        // Obter o nome do agente
        String nomeAgente = entity.getAgent().getName(); // Supondo que AgentEntity tenha o método getName()

        // Converter TipoTurnoEntity para TipoTurnoDTO
        TipoTurnoDTO tipoTurnoDTO = new TipoTurnoDTO(
                entity.getTipoTurno().getId(),
                entity.getTipoTurno().getDescricao(),
                entity.getTipoTurno().getCod(),
                entity.getTipoTurno().getValorJunior(),
                entity.getTipoTurno().getValorSenior()
        );

        return new TurnDTO(
                entity.getId(),
                tipoTurnoDTO,
                nomeAgente,
                entity.getDataTurno(),
                entity.getAgent().getId()
        );
    }

    // Função auxiliar para criar um item de folha de pagamento
    private ItemsSalarioDTO criarItemFolhaPagamento(TipoTurnoDTO tipoTurno, List<TurnDTO> turnos, boolean isSenior) {
        // Quantidade de turnos do mesmo tipo
        int quantidadeTurnos = turnos.size();

        // Valor unitário (Senior ou Junior)
        BigDecimal valorUnitario = isSenior ? tipoTurno.valorSenior() : tipoTurno.valorJunior();

        // Valor total para esse tipo de turno
        BigDecimal valorTotal = valorUnitario.multiply(BigDecimal.valueOf(quantidadeTurnos));

        // Verificar se o tipo de turno começa com "FERIADO" e dobrar os valores se necessário
        if (tipoTurno.descricao().toUpperCase().startsWith("FERIADO")) {
            valorUnitario = valorUnitario.multiply(BigDecimal.valueOf(2));
            valorTotal = valorTotal.multiply(BigDecimal.valueOf(2));
        }

        // Criar o item de folha de pagamento
        ItemsSalarioDTO item = new ItemsSalarioDTO();
        item.setDesItemSalario(quantidadeTurnos + "x " + tipoTurno.descricao());
        item.setPriceItemSalario(valorUnitario.toString());
        item.setTotalPriceItem(valorTotal.toString());

        return item;
    }

    // Função auxiliar para criar um item de folha de pagamento para adições de salário
    private ItemsSalarioDTO criarItemAdicaoSalario(AdicaoSalarioDTO adicaoSalarioDTO) {
        ItemsSalarioDTO item = new ItemsSalarioDTO();
        item.setDesItemSalario(adicaoSalarioDTO.tipoAdicao().desTipoAdicao());
        item.setPriceItemSalario(adicaoSalarioDTO.qtyAdicao().toString());
        item.setTotalPriceItem(adicaoSalarioDTO.qtyAdicao().toString());

        return item;
    }

}
