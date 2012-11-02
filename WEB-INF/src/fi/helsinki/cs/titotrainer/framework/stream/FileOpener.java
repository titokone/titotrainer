package fi.helsinki.cs.titotrainer.framework.stream;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Opens a named file.
 */
public class FileOpener implements InputStreamOpener {
    
    @Override
    public InputStream open(String key) throws IOException, FileNotFoundException {
        if (key == null)
            throw new FileNotFoundException();
        
        return new FileInputStream(key);
    }
    
}
