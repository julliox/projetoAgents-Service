package br.com.octopus.projectA.Util.HTMLToPdf;

import com.itextpdf.text.DocumentException;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public final class HtmlToPdfUtil {

    private static final TipoRenderer RENDERER_PADRAO = null;

    public static byte[] gerarPdfCaminhoDeTemplateEDados(String caminhoTemplate, Object objeto, TipoRenderer tipoTemplate) throws IOException, DocumentException {
        if (Stream.of(caminhoTemplate).anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("template");
        } else {
            return getRendererOuDefault(tipoTemplate).getRenderer().gerarPdfCaminhoDeTemplateEDados(caminhoTemplate, objeto);
        }
    }

    private static TipoRenderer getRendererOuDefault(TipoRenderer renderer) {
        return (TipoRenderer) Optional.ofNullable(renderer).orElse(RENDERER_PADRAO);
    }
}
