package fi.helsinki.cs.titotrainer.framework.stream;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * <p>Interface to any object that can open an input stream.</p>
 * 
 * <p>A factory for {@link InputStream}s.</p>
 */
public interface InputStreamOpener {
    
    /**
     * Opens a specific stream.
     * 
     * @param key A name or path. Not necessarily significant for all implementations.
     *            May be null.
     * @return An input stream. Never null. Throws FileNotFoundException instead.
     * @throws IOException If the stream could not be opened.
     * @throws FileNotFoundException If the stream could not be opened at this time.
     */
    public InputStream open(String key) throws IOException, FileNotFoundException;
}
