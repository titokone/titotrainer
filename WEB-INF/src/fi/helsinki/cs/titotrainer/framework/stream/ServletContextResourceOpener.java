package fi.helsinki.cs.titotrainer.framework.stream;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContext;

/**
 * Opens a servlet path given a resource path.
 */
public class ServletContextResourceOpener implements InputStreamOpener {
    protected ServletContext servletContext;
    
    public ServletContextResourceOpener(ServletContext servletContext) {
        if (servletContext == null)
            throw new IllegalArgumentException("servletContext may not be null");
        this.servletContext = servletContext;
    }

    /**
     * Opens the resource given a path to an existing resource.
     */
    @Override
    public InputStream open(String key) throws IOException, FileNotFoundException {
        if (key == null)
            throw new FileNotFoundException(this.getClass().getSimpleName() + " expects a resource path");
        
        InputStream stream = this.servletContext.getResourceAsStream(key);
        if (stream == null)
            throw new FileNotFoundException(key + " not found in servlet context");
        
        return stream;
    }
    
    
    
}
