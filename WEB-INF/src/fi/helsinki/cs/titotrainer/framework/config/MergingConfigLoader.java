package fi.helsinki.cs.titotrainer.framework.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * <p>Given a number of {@link ConfigLoader} objects, attempts to load
 * configurations in the order given and merges all successfully
 * loaded configurations into one. Keys from configurations loaded first
 * can be overridden by configurations loaded later.</p>
 * 
 * <p>{@link FileNotFoundException} are thrown by subloaders are ignored,
 * but {@link IOException} are forwarded.</p>
 */
public class MergingConfigLoader implements ConfigLoader {
    
    protected LinkedList<ConfigLoader> loaders;
    
    /**
     * Default constructor.
     */
    public MergingConfigLoader() {
        this.loaders = new LinkedList<ConfigLoader>();
    }
    
    /**
     * Convenience constructor with one initial ConfigLoader.
     * @param first The first config loader to add.
     */
    public MergingConfigLoader(ConfigLoader first) {
        this();
        this.appendLoader(first);
    }
    
    /**
     * Convenience constructor with two initial ConfigLoaders.
     * @param first The first config loader to add.
     * @param second The second config loader to add.
     */
    public MergingConfigLoader(ConfigLoader first, ConfigLoader second) {
        this(first);
        this.appendLoader(second);
    }
    
    /**
     * Adds a loader to be invoked after all other loaders.
     * 
     * @param loader The loader to add.
     */
    public synchronized void appendLoader(ConfigLoader loader) {
        this.loaders.add(loader);
    }
    
    /**
     * Adds a loader to be invoked before all other loaders.
     * 
     * @param loader The loader to add.
     */
    public synchronized void prependLoader(ConfigLoader loader) {
        this.loaders.push(loader);
    }
    
    /**
     * <p>Returns a list of the subloaders.</p>
     * 
     * @return An unmodifiable list of subloaders.
     */
    public List<ConfigLoader> getLoaders() {
        return Collections.unmodifiableList(loaders);
    }
    
    @Override
    public synchronized Config load(String name) throws FileNotFoundException, IOException {
        Properties props = new Properties();
        
        for (ConfigLoader loader : loaders) {
            Config config;
            try {
                config = loader.load(name);
            } catch (FileNotFoundException e) {
                continue;
            }
            for (String key : config.getAllKeys())
                props.setProperty(key, config.get(key));
        }
        
        return new PropertyConfig(props);
    }
}
