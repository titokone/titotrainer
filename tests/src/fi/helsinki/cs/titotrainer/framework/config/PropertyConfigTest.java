package fi.helsinki.cs.titotrainer.framework.config;

import static org.junit.Assert.*;

import java.util.Properties;

import org.junit.Test;

public class PropertyConfigTest {
    
    @Test
    public void getAllKeysMethodShouldReturnAllPropertyKeys() {
        Properties props = new Properties();
        props.put("foo", "foo");
        props.put("FOO", "bar");
        
        PropertyConfig pc = new PropertyConfig(props);
        assertTrue(pc.getAllKeys().contains("foo"));
        assertTrue(pc.getAllKeys().contains("FOO"));
    }
}
