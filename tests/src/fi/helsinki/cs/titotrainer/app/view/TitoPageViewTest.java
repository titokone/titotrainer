package fi.helsinki.cs.titotrainer.app.view;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import fi.helsinki.cs.titotrainer.app.model.User;
import fi.helsinki.cs.titotrainer.app.request.TitoRequest;
import fi.helsinki.cs.titotrainer.app.session.Messenger;
import fi.helsinki.cs.titotrainer.app.view.template.TemplateUtils;
import fi.helsinki.cs.titotrainer.framework.i18n.Translator;
import fi.helsinki.cs.titotrainer.framework.view.template.TemplateRenderer;
import fi.helsinki.cs.titotrainer.testsupport.TitoTestCase;

public class TitoPageViewTest extends TitoTestCase {
    
    private TitoPageView<TitoRequest> view;
    
    @Before
    public void setUp() {
        this.view = new TitoPageView<TitoRequest>() {
            
            @Override
            protected String getHeaderTemplateName() {
                return "empty.vm";
            }
            
            @Override
            protected String getTemplateName() {
                return "empty.vm";
            }
            
            @Override
            protected String getFooterTemplateName() {
                return "empty.vm";
            }
            
            @Override
            public Class<TitoRequest> getRequestType() {
                return TitoRequest.class;
            }
        };
    }
    
    @Test
    public void shouldPutRequestAndRequestContextVariablesIntoTemplateRenderer() throws IOException {
        TitoRequest req = this.createRequest(TitoRequest.class, "");
        TemplateRenderer tr = this.view.createTemplateRenderer(req);
        
        assertSame(req, tr.get("req"));
        assertSame(req.getContext(), tr.get("rc"));
    }
    
    @Test
    public void shouldPutModuleVariableIntoTemplateRendererBasedOnRequestPath() throws IOException {
        TitoRequest req = this.createRequest(TitoRequest.class, "/student/foo");
        TemplateRenderer tr = this.view.createTemplateRenderer(req);
        assertEquals("student", tr.get("module"));
        
        req = this.createRequest(TitoRequest.class, "/admin/foo");
        tr = this.view.createTemplateRenderer(req);
        assertEquals("admin", tr.get("module"));
        
        req = this.createRequest(TitoRequest.class, "/");
        tr = this.view.createTemplateRenderer(req);
        assertEquals(null, tr.get("module"));
    }
    
    @Test
    public void shouldPutNullUserVariableIntoTemplateRendererIfNoUserIsLoggedIn() throws IOException {
        TitoRequest req = this.createRequest(TitoRequest.class, "");
        TemplateRenderer tr = this.view.createTemplateRenderer(req);
        
        assertEquals(null, tr.get("user"));
    }
    
    @Test
    public void shouldPutUserVariableIntoTemplateRendererIfUserIsLoggedIn() throws IOException {
        TitoRequest req = this.createRequest(TitoRequest.class, "");
        
        User mockUser = Mockito.mock(User.class);
        Mockito.stub(req.getUserSession().getAuthenticatedUser()).toReturn(mockUser);
        
        TemplateRenderer tr = this.view.createTemplateRenderer(req);
        
        assertEquals(mockUser, tr.get("user"));
    }
    
    @Test
    public void shouldPutTranslatorVariableIntoTemplateRenderer() throws IOException {
        TitoRequest req = this.createRequest(TitoRequest.class, "");
        TemplateRenderer tr = this.view.createTemplateRenderer(req);
        
        assertTrue(tr.get("tr") instanceof Translator);
    }
    
    @Test
    public void shouldPutMessengerVariableIntoTemplateRenderer() throws IOException {
        TitoRequest req = this.createRequest(TitoRequest.class, "");
        TemplateRenderer tr = this.view.createTemplateRenderer(req);
        
        assertTrue(tr.get("messenger") instanceof Messenger);
    }
    
    @Test
    public void shouldPutUtilsVariableIntoTemplateRenderer() throws IOException {
        TitoRequest req = this.createRequest(TitoRequest.class, "");
        TemplateRenderer tr = this.view.createTemplateRenderer(req);
        
        assertTrue(tr.get("utils") instanceof TemplateUtils);
    }
    
    //TODO: test translation loading (header & footer specific too)
    
}
