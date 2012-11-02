package fi.helsinki.cs.titotrainer.framework.config;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Properties;

/**
 * A configuration backed by a Properties object.
 */
public class PropertyConfig implements Config {

    protected Properties props;
    
    /**
     * Creates a PropertyConfig to reflect a {@link Properties} object.
     * 
     * @param props The properties object to wrap.
     */
    public PropertyConfig(Properties props) {
        this.props = props;
    }
    
    @Override
    public String get(String key) {
        return props.getProperty(key);
    }
    
    @Override
    public String get(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }

    @Override
    public Collection<String> getAllKeys() {
        Collection<String> keys = new LinkedList<String>();
        for (Object key : props.keySet()) {
            keys.add(key.toString());
        }
        return keys;
    }
    
}
