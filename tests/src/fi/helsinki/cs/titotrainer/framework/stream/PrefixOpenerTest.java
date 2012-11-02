package fi.helsinki.cs.titotrainer.framework.stream;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class PrefixOpenerTest {
    
    InputStreamOpener mockOpener;
    
    @Before
    public void setUp() {
        mockOpener = Mockito.mock(InputStreamOpener.class);
    }
    
    @Test(expected = NullPointerException.class)
    public void shouldNotAcceptNullSubopener() {
        new PrefixOpener(null, "pre");
    }
    
    @Test(expected = NullPointerException.class)
    public void shouldNotAcceptNullPrefix() {
        new PrefixOpener(mockOpener, null);
    }
    
    @Test
    public void shouldPrependPrefixToPath() throws FileNotFoundException, IOException {
        PrefixOpener po = new PrefixOpener(mockOpener, "pre");
        po.open("foo");
        Mockito.verify(mockOpener).open("prefoo");
    }
    
    @Test
    public void shouldIgnoreNullKey() throws IOException {
        PrefixOpener po = new PrefixOpener(mockOpener, "pre");
        po.open(null);
        Mockito.verify(mockOpener).open("pre");
    }
}
