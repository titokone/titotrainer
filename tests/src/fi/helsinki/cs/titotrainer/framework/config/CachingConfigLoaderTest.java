package fi.helsinki.cs.titotrainer.framework.config;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class CachingConfigLoaderTest {
    
    private Config subconfigMock;
    private ConfigLoader subloaderMock;
    
    @Before
    public void setUp() throws FileNotFoundException, IOException {
        subconfigMock = Mockito.mock(Config.class);
        subloaderMock = Mockito.mock(ConfigLoader.class);
        
        Mockito.stub(subloaderMock.load("foo")).toReturn(subconfigMock);
    }
    
    @Test
    public void shouldCacheSubloaderConfig() throws FileNotFoundException, IOException {
        
        CachingConfigLoader cacher = new CachingConfigLoader(subloaderMock);
        assertSame(subconfigMock, cacher.load("foo"));
        Mockito.verify(subloaderMock, Mockito.times(1)).load("foo");
        
        assertSame(subconfigMock, cacher.load("foo"));
        Mockito.verifyNoMoreInteractions(subloaderMock.load("foo"));
    }
    
    @Test
    public void cacheShouldBeClearable() throws FileNotFoundException, IOException {
        CachingConfigLoader cacher = new CachingConfigLoader(subloaderMock);
        cacher.load("foo");
        cacher.clearCache("foo");
        cacher.load("foo");
        cacher.clearCache();
        cacher.load("foo");
        Mockito.verify(subloaderMock, Mockito.times(3)).load("foo");
    }
    
}
