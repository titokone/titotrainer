package fi.helsinki.cs.titotrainer.framework.view.template;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

/**
 * A convenience superclass for implementing the {@link TemplateRenderer}
 * interface.
 */
public abstract class AbstractTemplateRenderer implements TemplateRenderer {
    
    /**
     * The default implementation in {@link AbstractTemplateRenderer}
     * uses {@link #render(java.io.Writer)} through an
     * {@link OutputStreamWriter}.
     */
    @Override
    public void writeResponse(OutputStream os, Charset charset) throws IOException {
        OutputStreamWriter osw = new OutputStreamWriter(os, charset);
        this.render(osw);
        osw.flush();
    }
}
