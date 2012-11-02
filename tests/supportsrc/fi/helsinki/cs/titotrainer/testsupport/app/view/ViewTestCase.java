package fi.helsinki.cs.titotrainer.testsupport.app.view;

import static org.junit.Assert.*;

import org.junit.Before;

import fi.helsinki.cs.titotrainer.app.access.TitoBaseRole;
import fi.helsinki.cs.titotrainer.app.session.TitoUserSession;
import fi.helsinki.cs.titotrainer.framework.request.Request;
import fi.helsinki.cs.titotrainer.framework.response.ErrorResponse;
import fi.helsinki.cs.titotrainer.framework.response.ErrorResponseException;
import fi.helsinki.cs.titotrainer.framework.response.ResponseBodyWriter;
import fi.helsinki.cs.titotrainer.framework.response.ViewRenderingResponse;
import fi.helsinki.cs.titotrainer.framework.response.ViewResponse;
import fi.helsinki.cs.titotrainer.framework.view.View;
import fi.helsinki.cs.titotrainer.framework.view.template.TemplateRenderer;
import fi.helsinki.cs.titotrainer.testsupport.TitoTestCase;

public abstract class ViewTestCase<RequestType extends Request, ViewType extends View<RequestType>> extends TitoTestCase {
    
    /**
     * The view being tested.
     */
    protected ViewType view;
    
    /**
     * <p>A request for the view.</p>
     * 
     * <p>The request will have {@link #sessionMock} set
     * as its user session.</p>
     */
    protected RequestType request;
    
    /**
     * <p>A mock of a {@link TitoUserSession}.</p>
     * 
     * <p>By default, {@link TitoUserSession#getRole()} is
     * stubbed to return {@link TitoBaseRole#GUEST}.</p>
     */
    protected TitoUserSession sessionMock;
    
    /**
     * Sets up the view under test and creates a request for it.
     * 
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    @Before
    public void setUpView() throws InstantiationException, IllegalAccessException {
        this.view = this.getViewType().newInstance();
        this.request = this.createRequest();
        this.sessionMock = (TitoUserSession)this.request.getUserSession();
    }
    
    protected abstract Class<ViewType> getViewType();
    
    @Override
    protected boolean isModelAccessCheckerEnabled() {
        return true;
    }
    
    
    /**
     * Creates the request for the view being tested.
     * This is called by {@link #setUpView()}.
     * 
     * @return A request object.
     */
    protected RequestType createRequest() {
        return this.request = super.createRequest(this.view.getRequestType(), "");
    }
    
    /**
     * Calls the view to handle {@link #request}, asserts that the result has a
     * {@link TemplateRenderer} and returns it.
     * 
     * @return The template renderer.
     * @throws Exception If the view throws an exception or an error response.
     */
    protected TemplateRenderer callTemplateBasedView() throws Exception {
        ViewResponse resp = this.view.handle(this.request);
        if (resp instanceof ViewRenderingResponse) {
            ResponseBodyWriter rw = ((ViewRenderingResponse)resp).getResponseWriter();
            assertTrue(rw instanceof TemplateRenderer);
            return ((TemplateRenderer)rw);
        } else {
            assertTrue(resp instanceof ErrorResponse);
            throw new ErrorResponseException((ErrorResponse)resp);
        }
    }
    
}
