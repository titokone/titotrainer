package fi.helsinki.cs.titotrainer.framework.stream;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContext;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class ServletContextResourceOpenerTest {
    
    private ServletContext scMock;
    
    @Before
    public void setUp() {
        this.scMock = Mockito.mock(ServletContext.class);
    }
    
    @Test
    public void shouldNotSearchForNullResource() throws IOException {
        try {
            new ServletContextResourceOpener(scMock).open(null);
        } catch (FileNotFoundException e) {
        }
        Mockito.verifyZeroInteractions(scMock);
    }
    
    @Test(expected = FileNotFoundException.class)
    public void shouldNeverFindNullResource() throws FileNotFoundException, IOException {
        new ServletContextResourceOpener(scMock).open(null);
    }
    
    @Test
    public void shouldGetResourceAsStreamFromServletContext() throws FileNotFoundException, IOException {
        Mockito.stub(scMock.getResourceAsStream("foo")).toReturn(Mockito.mock(InputStream.class));
        new ServletContextResourceOpener(scMock).open("foo");
        Mockito.verify(scMock).getResourceAsStream("foo");
        Mockito.verifyNoMoreInteractions(scMock);
    }
    
    @Test(expected = FileNotFoundException.class)
    public void shouldThrowFileNotFoundExceptionIfResourceNotFound() throws FileNotFoundException, IOException {
        Mockito.stub(scMock.getResourceAsStream("foo")).toReturn(null);
        new ServletContextResourceOpener(scMock).open("foo");
    }
}
