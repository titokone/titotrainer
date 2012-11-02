package fi.helsinki.cs.titotrainer.framework.config;

import java.util.Collection;

/**
 * Interface to a simple key/value configuration.
 */
public interface Config {
    /**
     * Returns a configuration value.
     * 
     * @param key The configuration key.
     * @return The configuration value, or null if not found.
     * @throws NullPointerException if key is null.
     */
    public String get(String key);
    
    /**
     * Returns a configuration value or the default value if not found.
     * 
     * @param key The configuration key.
     * @param defaultValue The value to return if the key is not found.
     * @return The configuration value, or <code>defaultValue</code> if not found.
     * @throws NullPointerException if key is null. 
     */
    public String get(String key, String defaultValue);
    
    /**
     * Returns a map of all configuration values.
     * 
     * @return A map with configuration values. Some values may be nulls.
     */
    public Collection<String> getAllKeys();
}
