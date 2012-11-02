package fi.helsinki.cs.titotrainer.framework.config;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Properties;

import org.junit.Test;
import org.mockito.Mockito;

public class ConfigUtilsTest {
    
    @Test
    public void toPropertiesMethodShouldReturnAllEntriesAsProperties() {
        Config cfg = Mockito.mock(Config.class);
        Mockito.stub(cfg.getAllKeys()).toReturn(Arrays.asList("a", "b"));
        Mockito.stub(cfg.get("a")).toReturn("A");
        Mockito.stub(cfg.get("b")).toReturn("B");
        
        Properties props = ConfigUtils.toProperties(cfg);
        assertEquals("A", props.get("a"));
        assertEquals("B", props.get("b"));
    }
    
    @Test
    public void toPropertiesMethodShouldReturnNullIfParameterWasNull() {
        assertNull(ConfigUtils.toProperties(null));
    }
}
