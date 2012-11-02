package fi.helsinki.cs.titotrainer.framework.view;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import fi.helsinki.cs.titotrainer.framework.response.ResponseBodyWriter;

public class AbstractViewResponseTest {
    
    private ResponseBodyWriter mockResponseWriter;
    
    @Before
    public void setUp() {
        this.mockResponseWriter = Mockito.mock(ResponseBodyWriter.class);
    }
    
    @Test
    public void statusCodeShouldBeAccessibleThroughGetter() {
        DefaultViewResponse resp = new DefaultViewResponse(mockResponseWriter);
        assertEquals(DefaultViewResponse.DEFAULT_STATUS_CODE, resp.getStatusCode());
        resp.setStatusCode(500);
        assertEquals(500, resp.getStatusCode());
    }
    
    @Test
    public void contentTypeShouldBeAccessibleThroughGetter() {
        DefaultViewResponse resp = new DefaultViewResponse(mockResponseWriter);
        assertEquals(DefaultViewResponse.DEFAULT_CONTENT_TYPE, resp.getContentType());
        
        String expectedContentType = "text/plain; charset=utf-16";
        resp.setContentType(expectedContentType);
        assertEquals(expectedContentType, resp.getContentType());
    }
}
