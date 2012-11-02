package fi.helsinki.cs.titotrainer.framework.config;

import java.util.Collection;
import java.util.Collections;


/**
 * A configuration that contains no keys.
 */
public class EmptyConfig implements Config {
    
    @Override
    public String get(String key) {
        return null;
    }
    
    @Override
    public String get(String key, String defaultValue) {
        return defaultValue;
    }
    
    @Override
    public Collection<String> getAllKeys() {
        return Collections.emptyList();
    }
    
}
