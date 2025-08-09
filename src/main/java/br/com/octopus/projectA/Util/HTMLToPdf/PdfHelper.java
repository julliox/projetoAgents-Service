package br.com.octopus.projectA.Util.HTMLToPdf;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.itextpdf.text.DocumentException;
import org.w3c.tidy.Tidy;

import org.xhtmlrenderer.pdf.ITextRenderer;

public abstract class PdfHelper {
    private Map<TipoAtributo, String> atributosRelatorio;

    public PdfHelper() {
    }

    public abstract String preencherTemplate(String var1, Object var2) throws IOException;

    protected String retornarAtributoOuValorDefault(TipoAtributo key) {
        Map<TipoAtributo, String> mapaValoresDefault = new HashMap();
        mapaValoresDefault.put(TipoAtributo.CHARSET, StandardCharsets.UTF_8.name());
        mapaValoresDefault.put(TipoAtributo.CAMINHO_RECURSOS_EXTERNOS_RELATORIO, "src/main/resources");
        mapaValoresDefault.put(TipoAtributo.SUFIXO_RELATORIO, ".html");
        mapaValoresDefault.put(TipoAtributo.PREFIX, "/");
        return (String) Optional.ofNullable((String)this.getAtributosRelatorio().get(key)).orElse((String)mapaValoresDefault.get(key));
    }

    public Map<TipoAtributo, String> getAtributosRelatorio() {
        if (this.atributosRelatorio == null) {
            this.atributosRelatorio = new HashMap();
        }

        return this.atributosRelatorio;
    }

    public void setAtributosRelatorio(Map<TipoAtributo, String> atributosRelatorio) {
        this.atributosRelatorio = atributosRelatorio;
    }

    protected String convertToXhtml(String html) throws UnsupportedEncodingException {
        Tidy tidy = new Tidy();
        String charset = this.retornarAtributoOuValorDefault(TipoAtributo.CHARSET);
        tidy.setInputEncoding(charset);
        tidy.setOutputEncoding(charset);
        tidy.setXHTML(true);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(html.getBytes(charset));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        tidy.parseDOM(inputStream, outputStream);
        return outputStream.toString(charset);
    }

    public byte[] gerarPdfCaminhoDeTemplateEDados(String caminhoTemplate, Object data) throws IOException, DocumentException {
        String xHtml = this.preencherTemplate(caminhoTemplate, data);
        return this.gerarBytesDePdf(xHtml);
    }

    public byte[] gerarBytesDePdf(String xHtml) throws DocumentException, IOException {
        String rootResource = FileSystems.getDefault().getPath(this.retornarAtributoOuValorDefault(TipoAtributo.CAMINHO_RECURSOS_EXTERNOS_RELATORIO)).toUri().toURL().toString();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        this.createPdf(baos, rootResource, this.convertToXhtml(xHtml));
        byte[] retorno = baos.toByteArray();
        baos.close();
        return retorno;
    }

    protected void createPdf(OutputStream baos, String rootResource, String xHtml) throws DocumentException, IOException {
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(xHtml, rootResource);
        renderer.layout();
        renderer.createPDF(baos);
    }
}
