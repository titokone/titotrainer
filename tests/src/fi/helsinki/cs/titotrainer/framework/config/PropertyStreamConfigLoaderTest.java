package fi.helsinki.cs.titotrainer.framework.config;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import fi.helsinki.cs.titotrainer.testsupport.framework.config.TestConfigOpener;

public class PropertyStreamConfigLoaderTest {
    
    PropertyStreamConfigLoader loader;
    
    @Before
    public void setUp() {
        this.loader = new PropertyStreamConfigLoader(new TestConfigOpener());
    }
    
    @Test
    public void shouldLoadPropertyFiles() throws FileNotFoundException, IOException {
        Config cfg = this.loader.load("example.properties");
        assertEquals("bar", cfg.get("foo"));
        assertEquals("baz", cfg.get("bar"));
    }
    
    @Test
    public void shouldAppendPropertiesFilenameSuffixAutomatically() throws FileNotFoundException, IOException {
        Config cfg = this.loader.load("example");
        assertEquals("bar", cfg.get("foo"));
        assertEquals("baz", cfg.get("bar"));
    }
    
    @Test(expected = FileNotFoundException.class)
    public void shouldThrowFileNotFoundExceptionIfFileNotFound() throws FileNotFoundException, IOException {
        this.loader.load("nonexistent");
    }
    
}
