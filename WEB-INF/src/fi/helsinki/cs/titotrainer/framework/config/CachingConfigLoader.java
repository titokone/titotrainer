package fi.helsinki.cs.titotrainer.framework.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>A configuration loader wrapper that caches the configuration loaded.</p>
 * 
 * <p>This class is thread-safe.</p>
 */
public class CachingConfigLoader implements ConfigLoader {
    
    protected final ConfigLoader subloader;
    protected Map<String, Config> cache;

    public CachingConfigLoader(ConfigLoader subloader) {
        this.subloader = subloader;
        this.clearCache();
    }
    
    /**
     * Clears all cached configurations so they can be reloaded
     * by subsequent calls to load().
     */
    public synchronized void clearCache() {
        this.cache = new HashMap<String, Config>();
    }
    
    /**
     * Clears one cached configurations so it can be reloaded
     * by a subsequent call to load().
     */
    public synchronized void clearCache(String name) {
        this.cache.remove(name);
    }
    
    @Override
    public synchronized Config load(String name) throws FileNotFoundException, IOException {
        Config config = this.cache.get(name);
        if (config == null) {
            config = subloader.load(name);
            assert(config != null);
            this.cache.put(name, config);
        }
        return config;
    }
}
