package fi.helsinki.cs.titotrainer.testsupport;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.varia.NullAppender;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import fi.helsinki.cs.titotrainer.app.TitoRequestContext;
import fi.helsinki.cs.titotrainer.app.access.TitoBaseRole;
import fi.helsinki.cs.titotrainer.app.i18n.TitoTranslation;
import fi.helsinki.cs.titotrainer.app.model.User;
import fi.helsinki.cs.titotrainer.app.model.access.CurrentCredentials;
import fi.helsinki.cs.titotrainer.app.model.access.ModelAccessController;
import fi.helsinki.cs.titotrainer.app.request.TitoRequestAttribs;
import fi.helsinki.cs.titotrainer.app.session.Messenger;
import fi.helsinki.cs.titotrainer.app.session.TitoUserSession;
import fi.helsinki.cs.titotrainer.framework.RequestContext;
import fi.helsinki.cs.titotrainer.framework.config.ConfigLoader;
import fi.helsinki.cs.titotrainer.framework.config.PropertyStreamConfigLoader;
import fi.helsinki.cs.titotrainer.framework.model.handler.ModelAccessChecker;
import fi.helsinki.cs.titotrainer.framework.request.Request;
import fi.helsinki.cs.titotrainer.framework.view.template.TemplateEngine;
import fi.helsinki.cs.titotrainer.framework.view.template.velocity.VelocityTemplateEngine;
import fi.helsinki.cs.titotrainer.testsupport.app.controller.ControllerTestCase;
import fi.helsinki.cs.titotrainer.testsupport.app.view.ViewTestCase;
import fi.helsinki.cs.titotrainer.testsupport.framework.config.TestConfigOpener;
import fi.helsinki.cs.titotrainer.testsupport.framework.model.TestDb;
import fi.helsinki.cs.titotrainer.testsupport.framework.view.template.TestViewTemplates;

/**
 * <p>A convenience superclass for our test cases.</p>
 * 
 * @see TitoTestRunner
 */
@RunWith(TitoTestRunner.class)
public class TitoTestCase {
    
    public static final Locale ENGLISH = Locale.ENGLISH;
    public static final Locale GERMAN  = Locale.GERMAN;
    public static final Locale FINNISH = new Locale("fi");
    
    /**
     * The current session factory if one has been initialized.
     * 
     * @see #getSessionFactory()
     * @see #getNewSessionFactory()
     */
    protected SessionFactory sessionFactory;
    private List<Session> openedSessions;
    private List<Session> autocloseSessions;
    
    // If false, the model access checker is disabled no matter what isModelAccessCheckerEnabled() says.
    private boolean inModelAccessCheckerPhase;
    
    /**
     * <p>Initializes a session factory to a fresh test database
     * and returns it.</p>
     * 
     * <p>Calling this causes all subsequent calls to
     * {@link #getSessionFactory()} to return the newest
     * factory.</p>
     * 
     * @return A SessionFactory to a blank test database.
     */
    protected SessionFactory getNewSessionFactory() {
        this.sessionFactory = TestDb.createSessionFactory();
        return this.sessionFactory;
    }
    
    /**
     * <p>Returns a session factory - initializing one for
     * a blank database if no session factory has been
     * created for the current test yet.</p>
     * 
     * <p>By default this calls {@link #getNewSessionFactory()} and
     * caches the result, so for subclasses that need to override this,
     * it usually suffices to override {@link #getNewSessionFactory()} only.</p>
     * 
     * @return The SessionFactory for this test.
     */
    protected SessionFactory getSessionFactory() {
        if (this.sessionFactory == null)
            this.sessionFactory = this.getNewSessionFactory();
        return this.sessionFactory;
    }
    
    private void maybeEnableModelAccessCheckerForSession(Session s) {
        if (this.inModelAccessCheckerPhase && this.isModelAccessCheckerEnabled())
            enableModelAccessCheckerForSession(s);
    }
    
    /**
     * <p>Manually enables the model access checker for a session.</p>
     * 
     * <p>This may be overridden to e.g. disable it for a specific session.</p>
     */
    protected void enableModelAccessCheckerForSession(Session s) {
        User user = CurrentCredentials.getCurrentUser();
        if (user == null)
            user = new User("testUser", getModelAccessCheckerRole());
        CurrentCredentials.setCurrentUser(user);
        ModelAccessChecker.enableForSession(s, ModelAccessController.getInstance(), user.getParentRole());
    }
    
    /**
     * <p>Manually disables the model access checker for a session.</p>
     * 
     * <p>Useful for fixture setup in some tests.
     * Note the model access checker is disabled by default in <code>@Before </code> setup methods.</p>
     */
    protected void disableModelAccessCheckerForSession(Session s) {
        ModelAccessChecker.disableForSession(s);
    }
    
    /**
     * <p>Opens a new session to the current session factory.</p>
     * 
     * <p>By obtaining sessions using this method you'll
     * have the superclass also automatically verify that you
     * don't forget to close any connections.</p>
     * 
     * @return As if {@link #openSession()} was called on the result of {@link #getSessionFactory()}.
     */
    protected Session openSession() {
        Session s = this.getSessionFactory().openSession();
        maybeEnableModelAccessCheckerForSession(s);
        if (this.openedSessions == null)
            this.openedSessions = new LinkedList<Session>();
        this.openedSessions.add(s);
        return s;
    }
    
    /**
     * <p>Opens a new session to the current session factory.</p>
     * 
     * <p>The session will be automatically closed at the end of the test.</p>
     * 
     * @return As if {@link #openSession()} was called on the result of {@link #getSessionFactory()}.
     */
    protected Session openAutoclosedSession() {
        Session s = this.getSessionFactory().openSession();
        maybeEnableModelAccessCheckerForSession(s);
        if (this.autocloseSessions == null)
            this.autocloseSessions = new LinkedList<Session>();
        this.autocloseSessions.add(s);
        return s;
    }
    
    /**
     * <p>Whether the {@link ModelAccessChecker} is enabled for this test.</p>
     * 
     * <p>The model access checker is only enabled just before the actual test method is invoked.</p>
     * 
     * <p>Defaults to false (but in {@link ControllerTestCase} and {@link ViewTestCase} defaults to true).</p>
     */
    protected boolean isModelAccessCheckerEnabled() {
        return false;
    }
    
    /**
     * <p>Returns the role to be given to the {@link ModelAccessChecker}.</p>
     * 
     * <p>Defaults to either Guest, Student or Administrative
     * depending on the package name of the subclass.</p>
     * 
     * @see ModelAccessChecker
     * @see ModelAccessController
     */
    protected TitoBaseRole getModelAccessCheckerRole() {
        String pkgName = this.getClass().getPackage().getName();
        if (pkgName.contains(".student.")) {
            return TitoBaseRole.STUDENT;
        } else if (pkgName.contains(".admin.")) {
            return TitoBaseRole.ADMINISTRATIVE;
        } else {
            return TitoBaseRole.GUEST;
        }
    }
    
    /**
     * Clears {@link CurrentCredentials} for the current thread.
     */
    @Before
    @BeforePriority(-200)
    public void clearCurrentCredentials() {
        CurrentCredentials.clear();
    }
    
    /**
     * Prepares TitoTestCase's internal database bookkeeping.
     */
    @Before
    @BeforePriority(-100)
    public void databasePrep() {
        this.sessionFactory = null;
        this.openedSessions = null;
        this.autocloseSessions = null;
        this.inModelAccessCheckerPhase = false;
    }
    
    /**
     * Enables the model access checker after other setup code is done.
     */
    @Before
    @BeforePriority(999)
    public void enableModelAccessChecker() {
        this.inModelAccessCheckerPhase = true;
        
        if (this.openedSessions != null) {
            for (Session s : this.openedSessions) {
                maybeEnableModelAccessCheckerForSession(s);
            }
        }
        if (this.autocloseSessions != null) {
            for (Session s : this.autocloseSessions) {
                maybeEnableModelAccessCheckerForSession(s);
            }
        }
    }
    
    @After
    public void databaseCleanup() {
        if (this.openedSessions != null) {
            for (Session s : this.openedSessions) {
                if (s.isOpen()) {
                    throw new AssertionError("Test case left an open Hibernate session.");
                }
            }
        }
        if (this.autocloseSessions != null) {
            for (Session s : this.autocloseSessions) {
                if (s.isOpen()) {
                    if (s.getTransaction() != null) {
                        if (s.getTransaction().isActive()) {
                            s.getTransaction().commit();
                        }
                    }
                    s.close();
                }
            }
        }
        if (this.sessionFactory != null) {
            this.sessionFactory.close();
        }
    }
    
    @BeforeClass
    public static void loggingSetup() {
        PropertyConfigurator.configure("tests/conf/log4j.properties");
    }
    
    /**
     * Creates a mock request context for tests.
     * 
     * @see #createRequest(Class, String)
     * @return A request context.
     */
    protected RequestContext createRequestContext() {
        try  {
            ConfigLoader cl = new PropertyStreamConfigLoader(new TestConfigOpener());
            TemplateEngine te = new VelocityTemplateEngine(TestViewTemplates.createVelocityEngine());
            TitoTranslation tt = new TitoTranslation(cl.load("translation"));
            Logger logger = Logger.getLogger(this.getClass());
            logger.removeAllAppenders();
            logger.addAppender(new NullAppender());
            
            TitoRequestContext rc = Mockito.mock(TitoRequestContext.class);
            Mockito.stub(rc.getConfigLoader()).toReturn(cl);
            Mockito.stub(rc.getDefaultTemplateEngine()).toReturn(te);
            Mockito.stub(rc.getLogger()).toReturn(logger);
            Mockito.stub(rc.getTitoTranslation()).toReturn(tt);
            return rc;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * <p>Creates a new request with most properties set.</p>
     * 
     * <p>
     * The {@link TitoRequestAttribs} object of the request
     * will be mocked so it can be restubbed by test cases.
     * </p>
     * 
     * <p>
     * The base path getter is stubbed to return "".
     * The local path getter is stubbed according to the parameter.
     * </p>
     * 
     * <p>
     * The hibernate session getter is stubbed to return
     * an autoclosed session with an open transaction.
     * </p>
     * 
     * <p>
     * The user session getter is stubbed to return
     * a mock {@link TitoUserSession} that has
     * {@link TitoUserSession#getRole()} stubbed to return
     * the guest role.
     * </p>
     * 
     * <p>
     * {@link TitoUserSession#getMessenger()} is stubbed
     * to return an empty {@link Messenger}.
     * </p>
     * 
     * <p>
     * The request context getter is stubbed to return
     * the context returned by {@link #createRequestContext()}.
     * </p>
     * 
     * @param <T> The type of request to create.
     * @param cls The class of T.
     * @param localPath The local path of the request.
     * @return
     */
    protected <T extends Request> T createRequest(Class<T> cls, String localPath) {
        T req;
        try {
            req = cls.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        RequestContext context = this.createRequestContext();
        
        TitoUserSession userSessionMock = Mockito.mock(TitoUserSession.class);
        Mockito.stub(userSessionMock.getRole()).toReturn(TitoBaseRole.GUEST);
        Mockito.stub(userSessionMock.getMessenger()).toReturn(new Messenger());
        
        Session session = this.openAutoclosedSession();
        session.beginTransaction();
        TitoRequestAttribs attribs = Mockito.mock(TitoRequestAttribs.class);
        Mockito.stub(attribs.getContext()).toReturn(context);
        Mockito.stub(attribs.getHibernateSession()).toReturn(session);
        Mockito.stub(attribs.getUserSession()).toReturn(userSessionMock);
        Mockito.stub(attribs.getBasePath()).toReturn("");
        Mockito.stub(attribs.getLocalPath()).toReturn(localPath);
        Mockito.stub(attribs.getFullPath()).toReturn("" + localPath);
        
        req.setAttribs(attribs);
        
        return req;
    }
    
}
