package fi.helsinki.cs.titotrainer.framework.view.template;

import java.io.IOException;
import java.io.Writer;

import fi.helsinki.cs.titotrainer.framework.response.ResponseBodyWriter;

public interface TemplateRenderer extends ResponseBodyWriter {
    
    /**
     * Assigns a variable to the template.
     * 
     * @param name The name of the variable.
     * @param value The value of the variable.
     * @throws NullPointerException if <code>name</code> is null.
     */
    public void put(String name, Object value);
    
    /**
     * Returns a previously assigned variable.
     * 
     * @param name The name of the variable.
     * @return The value of the variable.
     * @throws NullPointerException if <code>name</code> is null.
     */
    public Object get(String name);
    
    public void render(Writer output) throws IOException;
    
}