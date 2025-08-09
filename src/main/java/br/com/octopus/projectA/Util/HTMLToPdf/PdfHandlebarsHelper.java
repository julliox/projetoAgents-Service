package br.com.octopus.projectA.Util.HTMLToPdf;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;

public class PdfHandlebarsHelper extends PdfHelper{

    public PdfHandlebarsHelper() {
    }

    private Template getTemplate(String caminhoTemplate) throws IOException {
        ClassPathTemplateLoader classPathTemplateLoader = new ClassPathTemplateLoader(this.retornarAtributoOuValorDefault(TipoAtributo.PREFIX), this.retornarAtributoOuValorDefault(TipoAtributo.SUFIXO_RELATORIO));
        classPathTemplateLoader.setCharset(Charset.forName(this.retornarAtributoOuValorDefault(TipoAtributo.CHARSET)));
        TemplateLoader loader = classPathTemplateLoader;
        Handlebars handlebars = new Handlebars(loader);
        handlebars.registerHelpers(new HandlebarsHelpers());
        return handlebars.compile(caminhoTemplate);
    }

    public String preencherTemplate(String caminhoTemplate, Object data) throws IOException {
        Template template = this.getTemplate(caminhoTemplate);
        return this.convertToXhtml(template.apply(this.retornaContextParaObjeto(data)));
    }

    protected Map<String, Object> retornaContextParaObjeto(Object data) {
        Map<String, Object> context = new HashMap();
        context.put("objeto", data);
        return context;
    }
}
