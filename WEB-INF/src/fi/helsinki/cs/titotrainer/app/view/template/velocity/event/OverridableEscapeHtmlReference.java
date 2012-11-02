package fi.helsinki.cs.titotrainer.app.view.template.velocity.event;

import org.apache.velocity.app.event.implement.EscapeHtmlReference;

import fi.helsinki.cs.titotrainer.app.view.template.NoEscapeWrapper;
import fi.helsinki.cs.titotrainer.app.view.template.TemplateUtils;

/**
 * <p>Works like {@link EscapeHtmlReference} but adds the option to override
 * escaping in the template by wrapping a reference in a
 * <code>$utils.noEscape()</code> call.</p>
 * 
 * <p>TitoTrainer uses this by default.</p>
 * 
 * @see TemplateUtils#noEscape(Object)
 */
public class OverridableEscapeHtmlReference extends EscapeHtmlReference {
    
    @Override
    public Object referenceInsert(String reference, Object value) {
        if (value instanceof NoEscapeWrapper) {
            return ((NoEscapeWrapper)value).getObject();
        } else {
            return super.referenceInsert(reference, value);
        }
    }
    
}
