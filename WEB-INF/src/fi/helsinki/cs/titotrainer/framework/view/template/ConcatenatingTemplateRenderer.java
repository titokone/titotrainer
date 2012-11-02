package fi.helsinki.cs.titotrainer.framework.view.template;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Renders multiple templates in sequence.</p>
 * 
 * <p>
 * All variables put through this renderer will be put into
 * all subrenderers as well.
 * </p>
 */
public class ConcatenatingTemplateRenderer extends AbstractTemplateRenderer {
    
    protected TemplateRenderer[] subrenderers;
    private Map<String, Object> variables;
    
    public ConcatenatingTemplateRenderer(TemplateRenderer ... subrenderers) {
        this.subrenderers = subrenderers;
        this.variables = new HashMap<String, Object>();
    }
    
    @Override
    public void put(String name, Object value) {
        for (TemplateRenderer sr : subrenderers) {
            sr.put(name, value);
        }
        this.variables.put(name, value);
    }

    @Override
    public Object get(String name) {
        return this.variables.get(name);
    }

    @Override
    public void render(Writer output) throws IOException {
        for (TemplateRenderer tr : subrenderers) {
            tr.render(output);
        }
    }
    
}
