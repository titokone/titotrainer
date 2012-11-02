package fi.helsinki.cs.titotrainer.framework.config;

import java.util.Properties;

/**
 * Helper methods for working with {@link Config} and
 * {@link ConfigLoader} objects.
 */
public class ConfigUtils {
    
    /**
     * Converts a {@link Config} object to a {@link Properties} object.
     * 
     * @param config The config object.
     * @return The properties object. Returns null iff the parameter was null.
     */
    public static Properties toProperties(Config config) {
        if (config == null)
            return null;
        
        Properties props = new Properties();
        for (String key : config.getAllKeys()) {
            props.put(key, config.get(key));
        }
        return props;
    }
    
    protected ConfigUtils() {
        // Prevent direct instantiation.
    }
}
