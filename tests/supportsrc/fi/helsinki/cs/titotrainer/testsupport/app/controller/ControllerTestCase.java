package fi.helsinki.cs.titotrainer.testsupport.app.controller;

import org.junit.Before;
import org.mockito.Mockito;

import fi.helsinki.cs.titotrainer.app.access.TitoBaseRole;
import fi.helsinki.cs.titotrainer.app.model.User;
import fi.helsinki.cs.titotrainer.app.session.TitoUserSession;
import fi.helsinki.cs.titotrainer.framework.controller.Controller;
import fi.helsinki.cs.titotrainer.framework.request.Request;
import fi.helsinki.cs.titotrainer.testsupport.TitoTestCase;

public abstract class ControllerTestCase<RequestType extends Request, ControllerType extends Controller<RequestType>> extends TitoTestCase {
    
    /**
     * The controller being tested.
     */
    protected ControllerType controller;
    
    /**
     * <p>A request for the controller.</p>
     * 
     * <p>The request will have {@link #sessionMock} set
     * as its user session.</p>
     */
    protected RequestType request;
    
    /**
     * <p>A mock of a {@link TitoUserSession} set in {@link #createRequest()}.</p>
     * 
     * <p>By default, {@link TitoUserSession#getRole()} is
     * stubbed to return {@link TitoBaseRole#GUEST}.</p>
     */
    protected TitoUserSession sessionMock;
    
    /**
     * Sets up the controller under test and creates a request for it.
     * 
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    @Before
    public void setUpController() throws InstantiationException, IllegalAccessException {
        this.controller = this.getControllerType().newInstance();
        this.request = this.createRequest();
    }
    
    protected abstract Class<ControllerType> getControllerType();
    
    
    /**
     * Creates the request for the controller being tested.
     * This is called by {@link #setUpController()}.
     * 
     * @see #createRequest(Class, String)
     * @return A request object.
     */
    protected RequestType createRequest() {
        this.request = super.createRequest(this.controller.getRequestType(), "");
        this.sessionMock = (TitoUserSession)this.request.getUserSession();
        return this.request;
    }
    
    @Override
    protected boolean isModelAccessCheckerEnabled() {
        return true;
    }
    
    /**
     * Creates the request for the controller being tested
     * on behalf of the given user.
     * 
     * @see #createRequest()
     * @param authUser The authenticated user. May be null.
     * @return The request.
     */
    protected RequestType createRequest(User authUser) {
        this.createRequest(authUser, "");
        return this.request;
    }
    
    /**
     * Creates the request for the controller being tested
     * on behalf of the given user.
     * 
     * @see #createRequest()
     * @param authUser The authenticated user. May be null.
     * @param localPath The local path. Not null.
     * @return The request.
     */
    protected RequestType createRequest(User authUser, String localPath) {
        this.request = this.createRequest(this.controller.getRequestType(), localPath);
        this.sessionMock = (TitoUserSession)this.request.getUserSession();
        if (authUser != null) {
            Mockito.stub(this.sessionMock.getAuthenticatedUser()).toReturn(authUser);
            Mockito.stub(this.sessionMock.getRole()).toReturn(authUser.getParentRole());
        }
        return this.request;
    }
}
