package fi.helsinki.cs.titotrainer.framework.stream;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Invokes another {@link InputStreamOpener} but adds a prefix to the key.
 */
public class PrefixOpener implements InputStreamOpener {

    protected InputStreamOpener subopener;
    protected String prefix;
    
    public PrefixOpener(InputStreamOpener subopener, String prefix) {
        if (subopener == null)
            throw new NullPointerException("subopener may not be null");
        if (prefix == null)
            throw new NullPointerException("prefix may not be null");
        this.subopener = subopener;
        this.prefix = prefix;
    }
    
    @Override
    public InputStream open(String key) throws IOException, FileNotFoundException {
        if (key == null)
            key = "";
        return this.subopener.open(this.prefix + key);
    }
    
}
