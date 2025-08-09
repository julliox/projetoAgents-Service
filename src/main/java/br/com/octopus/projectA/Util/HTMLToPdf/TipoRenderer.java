package br.com.octopus.projectA.Util.HTMLToPdf;

public enum TipoRenderer {
    HTML_HANDLEBARS {
        public PdfHelper getRenderer() {
            return new PdfHandlebarsHelper();
        }
    };

    private TipoRenderer() {
    }

    public abstract PdfHelper getRenderer();
}
