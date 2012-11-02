package fi.helsinki.cs.titotrainer.framework.config;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Interface to a configuration loader.
 */
public interface ConfigLoader {
    
    /**
     * <p>Loads a configuration given a name.</p>
     * 
     * <p>This method must be thread-safe.</p>
     * 
     * @param name
     * @return The configuration.
     * @throws FileNotFoundException if the configuration could not be loaded.
     * @throws IOException if loading the configuration file failed for some reason.
     */
    public Config load(String name) throws FileNotFoundException, IOException;
}
