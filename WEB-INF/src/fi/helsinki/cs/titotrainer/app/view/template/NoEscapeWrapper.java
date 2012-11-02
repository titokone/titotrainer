package fi.helsinki.cs.titotrainer.app.view.template;

/**
 * <p>The wrapper object inserted by <code>$utils.noescape()</code>.</p>
 * 
 * <p>{@link #toString()} returns the value of {@link Object#toString()} of
 * the wrapped object.</p> 
 */
public class NoEscapeWrapper {
    private Object obj;
    
    public NoEscapeWrapper(Object obj) {
        this.obj = obj;
    }
    
    public Object getObject() {
        return this.obj;
    }
    
    @Override
    public String toString() {
        return this.obj.toString();
    }
}