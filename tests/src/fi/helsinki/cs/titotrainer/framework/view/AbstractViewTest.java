package fi.helsinki.cs.titotrainer.framework.view;

import static org.junit.Assert.*;

import org.junit.Test;
import org.mockito.Mockito;

import fi.helsinki.cs.titotrainer.framework.RequestContext;
import fi.helsinki.cs.titotrainer.framework.request.DefaultRequest;
import fi.helsinki.cs.titotrainer.framework.view.template.TemplateEngine;

public class AbstractViewTest {
    
    private static class EmptyView extends AbstractView<DefaultRequest> {
        
        public String templateName;
        
        @Override
        protected String getTemplateName() {
            if (this.templateName != null)
                return templateName;
            else
                return super.getTemplateName();
        }
        
        @Override
        public Class<DefaultRequest> getRequestType() {
            return DefaultRequest.class;
        }
    }
    
    @Test
    public void defaultTemplateEngineShouldBeUsedByDefault() throws Exception {
        TemplateEngine mockEngine = Mockito.mock(TemplateEngine.class);
        RequestContext mockRc = Mockito.mock(RequestContext.class);
        Mockito.stub(mockRc.getDefaultTemplateEngine()).toReturn(mockEngine);
        assertSame(mockEngine, new EmptyView().getTemplateEngine(mockRc));
    }
    
    //TODO: improve coverage
}
