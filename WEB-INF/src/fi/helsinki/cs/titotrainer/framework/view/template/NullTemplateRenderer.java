package fi.helsinki.cs.titotrainer.framework.view.template;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;

/**
 * A template renderer that renders nothing.
 */
public class NullTemplateRenderer extends AbstractTemplateRenderer {
    
    private HashMap<String, Object> vars = new HashMap<String, Object>();
    
    @Override
    public void put(String name, Object value) {
        this.vars.put(name, value);
    }
    
    @Override
    public Object get(String name) {
        return this.vars.get(name);
    }
    
    /**
     * Does nothing.
     */
    @Override
    public void render(Writer output) throws IOException {
    }
}
