package fi.helsinki.cs.titotrainer.framework.controller;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.LinkedList;

import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import fi.helsinki.cs.titotrainer.app.request.TitoRequest;
import fi.helsinki.cs.titotrainer.app.request.TitoRequestAttribs;
import fi.helsinki.cs.titotrainer.framework.request.RequestInvalidity;
import fi.helsinki.cs.titotrainer.framework.response.Response;

public class AbstractControllerTest {
    
    public static class TestRequest extends TitoRequest {
        private final boolean valid;
        public TestRequest(boolean valid) {
            this.valid = valid;
        }
        
        @Override
        public Collection<RequestInvalidity> validate() {
            Collection<RequestInvalidity> ret = new LinkedList<RequestInvalidity>();
            if (!valid)
                ret.add(new RequestInvalidity("Testing invalid request"));
            return ret;
        }
        
        @Override
        public TitoRequestAttribs getAttribs() {
            return Mockito.mock(TitoRequestAttribs.class);
        }
        
    }
    
    private static class TestController extends AbstractController<TestRequest> {
        
        public int handleValidCalls = 0;
        public int handleInvalidCalls = 0;
        
        @Override
        protected Response handleValid(TestRequest req, Session hs) throws Exception {
            ++this.handleValidCalls;
            return new Response() {
                @Override
                public int getStatusCode() {
                    return 200;
                }
            };
        }
        
        @Override
        protected Response handleInvalid(TestRequest req, Session hs, Collection<RequestInvalidity> invalidities) throws Exception {
            ++this.handleInvalidCalls;
            return super.handleInvalid(req, hs, invalidities);
        }
        
        @Override
        protected boolean useTransaction() {
            return false;
        }
        
        @Override
        public Class<TestRequest> getRequestType() {
            return TestRequest.class;
        }
    }
    
    private TestController testController;
    
    @Before
    public void setUp() {
        this.testController = new TestController();
    }
    
    @Test
    public void shouldCallHandleValidIfRequestIsValid() throws Exception {
        TestRequest tr = new TestRequest(true);
        this.testController.handle(tr);
        assertEquals(1, this.testController.handleValidCalls);
    }
    
    @Test
    public void shouldCallHandleInvalidIfRequestIsNotValid() throws Exception {
        TestRequest tr = new TestRequest(false);
        this.testController.handle(tr);
        assertEquals(1, this.testController.handleInvalidCalls);
    }
    
    @Test
    public void handleInvalidShouldReturn404ByDefault() throws Exception {
        TestRequest tr = new TestRequest(false);
        assertEquals(404, this.testController.handleInvalid(tr, null, tr.validate()).getStatusCode());
    }
    
}
