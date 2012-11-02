package fi.helsinki.cs.titotrainer.framework.config;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.junit.Test;
import org.mockito.Mockito;

public class MergingConfigLoaderTest {

    @Test
    public void shouldBeConstructableWithTwoInitialSubloaders() {
        ConfigLoader subMock1 = Mockito.mock(ConfigLoader.class);
        ConfigLoader subMock2 = Mockito.mock(ConfigLoader.class);
        
        MergingConfigLoader mcl = new MergingConfigLoader(subMock1, subMock2);
        
        List<ConfigLoader> list = mcl.getLoaders();
        
        assertEquals(2, list.size());
        assertSame(subMock1, list.get(0));
        assertSame(subMock2, list.get(1));
    }
    
    @Test
    public void shouldLoadValuesFromAllSubloaders() throws FileNotFoundException, IOException {
        ConfigLoader subMock1 = Mockito.mock(ConfigLoader.class);
        ConfigLoader subMock2 = Mockito.mock(ConfigLoader.class);
        ConfigLoader subMock3 = Mockito.mock(ConfigLoader.class);
        
        Properties config1Props = new Properties();
        Properties config2Props = new Properties();
        Properties config3Props = new Properties();
        config1Props.put("conf1", "a");
        config2Props.put("conf2", "b");
        config3Props.put("conf3", "c");
        
        PropertyConfig config1 = new PropertyConfig(config1Props);
        PropertyConfig config2 = new PropertyConfig(config2Props);
        PropertyConfig config3 = new PropertyConfig(config3Props);
        
        Mockito.stub(subMock1.load("foo")).toReturn(config1);
        Mockito.stub(subMock2.load("foo")).toReturn(config2);
        Mockito.stub(subMock3.load("foo")).toReturn(config3);
        
        MergingConfigLoader mcl = new MergingConfigLoader();
        mcl.appendLoader(subMock1);
        mcl.appendLoader(subMock2);
        mcl.appendLoader(subMock3);
        
        Config c = mcl.load("foo");
        assertEquals("a", c.get("conf1"));
        assertEquals("b", c.get("conf2"));
        assertEquals("c", c.get("conf3"));
    }
    
    
    @Test
    public void valuesLoadedLaterShouldOverrideValuesLoadedEarlier() throws FileNotFoundException, IOException {

        ConfigLoader subMock1 = Mockito.mock(ConfigLoader.class);
        ConfigLoader subMock2 = Mockito.mock(ConfigLoader.class);
        ConfigLoader subMock3 = Mockito.mock(ConfigLoader.class);
        
        Properties config1Props = new Properties();
        Properties config2Props = new Properties();
        Properties config3Props = new Properties();
        
        config1Props.put("conf1", "a");
        config2Props.put("conf1", "b");
        config3Props.put("conf1", "c");
        
        PropertyConfig config1 = new PropertyConfig(config1Props);
        PropertyConfig config2 = new PropertyConfig(config2Props);
        PropertyConfig config3 = new PropertyConfig(config3Props);
        
        Mockito.stub(subMock1.load("foo")).toReturn(config1);
        Mockito.stub(subMock2.load("foo")).toReturn(config2);
        Mockito.stub(subMock3.load("foo")).toReturn(config3);
        
        MergingConfigLoader mcl = new MergingConfigLoader();
        
        mcl.appendLoader(subMock1);        
        Config c = mcl.load("foo");
        assertEquals("a", c.get("conf1"));
        
        mcl.appendLoader(subMock2);
        c = mcl.load("foo");
        assertEquals("b", c.get("conf1"));
        
        mcl.prependLoader(subMock3);
        c = mcl.load("foo");
        assertEquals("b", c.get("conf1"));
    }
    
    @Test
    public void shouldNotPropagateFileNotFoundException() throws FileNotFoundException, IOException {
        
        ConfigLoader subMock1 = Mockito.mock(ConfigLoader.class);

        
        Mockito.stub(subMock1.load("foo")).toThrow(new FileNotFoundException());
        
        MergingConfigLoader mcl = new MergingConfigLoader();
        
        mcl.appendLoader(subMock1);
        mcl.load("foo");
    }
  
    @Test(expected = IOException.class)
    public void shouldPropagateIOException() throws FileNotFoundException, IOException {
        ConfigLoader subMock1 = Mockito.mock(ConfigLoader.class);

        
        Mockito.stub(subMock1.load("foo")).toThrow(new IOException());
        
        MergingConfigLoader mcl = new MergingConfigLoader();
        
        mcl.appendLoader(subMock1);
        mcl.load("foo");
    }

    
    @Test
    public void shouldNotPropagateIOException() {
        //TODO
    }
   
}
