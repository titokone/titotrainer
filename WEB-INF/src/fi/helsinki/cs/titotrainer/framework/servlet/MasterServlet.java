package fi.helsinki.cs.titotrainer.framework.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.hibernate.Session;
import org.hibernate.Transaction;

import fi.helsinki.cs.titotrainer.framework.RequestContext;
import fi.helsinki.cs.titotrainer.framework.RequestHandler;
import fi.helsinki.cs.titotrainer.framework.config.ConfigLoader;
import fi.helsinki.cs.titotrainer.framework.config.ConfigUtils;
import fi.helsinki.cs.titotrainer.framework.controller.Controller;
import fi.helsinki.cs.titotrainer.framework.model.HibernateInstance;
import fi.helsinki.cs.titotrainer.framework.request.Request;
import fi.helsinki.cs.titotrainer.framework.request.RequestAttribs;
import fi.helsinki.cs.titotrainer.framework.request.RequestBuilder;
import fi.helsinki.cs.titotrainer.framework.response.ErrorResponse;
import fi.helsinki.cs.titotrainer.framework.response.RedirectResponse;
import fi.helsinki.cs.titotrainer.framework.response.Response;
import fi.helsinki.cs.titotrainer.framework.response.ResponseBodyWriter;
import fi.helsinki.cs.titotrainer.framework.response.ViewRenderingResponse;
import fi.helsinki.cs.titotrainer.framework.session.UserSession;
import fi.helsinki.cs.titotrainer.framework.view.template.TemplateEngine;
import fi.helsinki.cs.titotrainer.framework.view.template.TemplateRenderer;

/**
 * <p>Dispatches servlet requests into the application and converts
 * results back into servlet responses.</p>
 * 
 * <p>This is an abstract class that should be overridden by the
 * real application's entry point.</p>
 * 
 * <p>This class only overrides the {@link #service(HttpServletRequest, HttpServletResponse)}
 * method of the default servlet methods.</p>
 * 
 * <p>TODO: Refactor this monolith where possible.</p>
 * <p>TODO: The container might instantiate many instances of this!
 *          Make _very_ sure that cannot be a problem.</p>
 */
public abstract class MasterServlet extends HttpServlet {
    
    private HibernateInstance hibernateInstance;
    private Logger logger;
    private RequestContext requestContext;
    
    /**
     * Returns the controller to which all request shall be passed.
     * 
     * @return The front controller. Never null.
     */
    protected abstract Controller<?> getFrontController();
    
    /**
     * <p>Creates the configuration loader to be attached to the request context.</p>
     * 
     * <p>This is called during {@link #init()}.</p>
     * 
     * @return A {@link ConfigLoader}. Never null.
     */
    protected abstract ConfigLoader createConfigLoader();
    
    /**
     * <p>Creates the hibernate instance of this app.</p>
     * 
     * <p>This is called during {@link #init()}.</p>
     * 
     * @param configLoader The ConfigLoader returned by {@link #createConfigLoader()}.
     * @return A {@link HibernateInstance}. Never null.
     * @throws ServletException If something goes wrong.
     */
    protected abstract HibernateInstance createHibernateInstance(ConfigLoader configLoader) throws ServletException;
    
    /**
     * Creates the default template engine to be placed in the {@link RequestContext}.
     *  
     * @param cl The ConfigLoader returned by {@link #createConfigLoader()}.
     * @return A {@link TemplateEngine}. Never null.
     * @throws Exception If something goes wrong.
     */
    protected abstract TemplateEngine createDefaultTemplateEngine(ConfigLoader cl) throws Exception;

    protected RequestContext createRequestContext(ConfigLoader cl, TemplateEngine te, Logger logger) {
        return new RequestContext(cl, te, logger);
    }
    
    /**
     * Returns the request context to attach to all new {@link Request} objects.
     * 
     * @return A {@link RequestContext}.
     */
    protected RequestContext getRequestContext() {
        return this.requestContext;
    }
    
    /**
     * <p>Returns the path prefix to strip from request paths when
     * transforming to application paths.</p>
     * 
     * <p>By default this returns the request's context path.</p>
     * 
     * @param req The servlet request.
     * @return The path prefix.
     * @see #getAppPath(HttpServletRequest)
     */
    protected String getAppPathPrefix(HttpServletRequest req) {
        return req.getContextPath();
    }
    
    /**
     * <p>Returns the request path with the prefix stripped off.</p>
     * 
     * @param req The servlet request.
     * @return The module path i.e. the request path with
     *         the path prefix stipped off.
     * @see #getAppPathPrefix(HttpServletRequest)
     */
    protected final String getAppPath(HttpServletRequest req) {
        String prefix = getAppPathPrefix(req);
        String path = req.getRequestURI();
        if (path.startsWith(prefix))
            return path.substring(prefix.length());
        else
            return null;
    }
    
    
    protected Logger createLog4jLogger(ConfigLoader cl) throws Exception {
        PropertyConfigurator.configure(ConfigUtils.toProperties(cl.load("log4j")));
        Logger log = Logger.getLogger(MasterServlet.class);
        return log;
    }
    
    protected Logger getLogger() {
        return logger;
    }
    
    
    @Override
    public void init() throws ServletException {
        super.init();
        
        ConfigLoader cl = this.createConfigLoader();
        
        try {
            this.logger = createLog4jLogger(cl);
        } catch (Exception e) {
            System.err.println("Failed to initialize log4j.");
            e.printStackTrace();
            throw new ServletException(e);
        }
        
        this.logger.debug("Logger initialized.");
        
        try {
            this.hibernateInstance = createHibernateInstance(cl);
        } catch (Exception e) {
            this.logger.fatal("Failed to initialize Hibernate", e);
            throw new ServletException(e);
        }
        
        this.logger.debug("Hibernate initialized.");
        
        TemplateEngine te;
        try {
            te = createDefaultTemplateEngine(cl);
        } catch (Exception e) {
            this.logger.fatal("Failed to initialize default template engine", e);
            throw new ServletException(e);
        }
        
        this.logger.debug("Template engine initialized.");
        
        this.requestContext = createRequestContext(cl, te, logger);
    }
    
    /**
     * Returns the hibernate instance initialized in {@link #init()}.
     * @return A hibernate instance. Never null after a successful {@link #init()}.
     */
    protected HibernateInstance getHibernateInstance() {
        return this.hibernateInstance;
    }
    
    /**
     * Returns the key for storing the {@link UserSession} in
     * an {@link HttpSession}.
     * 
     * @return A non-null string.
     */
    protected String getUserSessionKey() {
        return "UserSession";
    }
    
    /**
     * <p>Returns the UserSession to pass to requests when
     * there is no session.</p>
     * 
     * <p>The default implementation returns null.</p>
     * 
     * @return The default user session, or null.
     */
    protected UserSession getDefaultUserSession() {
        return null;
    }
    
    private HashMap<String, Object> flattenServletRequestParameterMap(Map<String, String[]> params) {
        HashMap<String, Object> flat = new HashMap<String, Object>();
        for (Map.Entry<String, String[]> e : params.entrySet()) {
            String value = null;
            if (e.getValue() != null && e.getValue().length > 0)
                value = e.getValue()[0];
            flat.put(e.getKey(), value);
        }
        return flat;
    }
    
    @SuppressWarnings("unchecked")
    private <T extends Request> T castRequest(RequestHandler<T> handler, Request req) {
        return (T)req;
    }
    
    /**
     * Creates the {@link RequestAttribs} object to
     * link to a reqest.
     */
    protected RequestAttribs createRequestAttribs(HttpServletRequest httpReq, UserSession userSession, String basePath, String localPath) throws Exception {
        return new RequestAttribs(
            this.getRequestContext(),
            this.getHibernateInstance().getSessionFactory().openSession(),
            userSession,
            basePath,
            localPath
            );
    }
    
    @SuppressWarnings("unchecked")
    private <T extends Request> T createRequestForController(Controller<T> controller, HttpServletRequest httpReq, UserSession userSession) throws Exception {
        Class<T> reqClass = controller.getRequestType();
        
        String basePath = this.getAppPathPrefix(httpReq);
        String localPath = this.getAppPath(httpReq);
        
        //FIXME: hack: remove double slashes here
        // TODO: Should do a redirect way before this if there are too many slashes
        basePath = basePath.replaceAll("//+", "/");
        localPath = localPath.replaceAll("//+", "/");
        
        RequestAttribs requestAttribs = this.createRequestAttribs(httpReq, userSession, basePath, localPath);
        
        HashMap<String, Object> params = flattenServletRequestParameterMap(httpReq.getParameterMap());
        
        if (ServletFileUpload.isMultipartContent(httpReq)) {
            FileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            List<FileItem> items = upload.parseRequest(httpReq);
            
            for (FileItem item : items) {
                if (item.isFormField()) {
                    params.put(item.getFieldName(), item.getString());
                } else {
                    params.put(item.getFieldName(), item);
                }
            }
        }
        
        T req = RequestBuilder.getDefaultInstance().build(params, reqClass);
        req.setAttribs(requestAttribs);
        
        return req;
    }
    
    // A helper method needed to get a binding for T.
    private <T extends Request> Response callFrontController(Controller<T> frontController, Request req) throws Exception {
        Session hibernateSession = null;
        try {
            hibernateSession = req.getAttribs().getHibernateSession();
            
            return frontController.handle(castRequest(frontController, req));
            
        } catch (Exception e) {
            // I'm somewhat uncertain whether all of this cleanup code is
            // necessary and/or correct.
            this.getLogger().error("Exception propagated from front controller", e);
            if (hibernateSession != null) {
                if (hibernateSession.isOpen()) {
                    Transaction tx = hibernateSession.getTransaction();
                    if (tx != null && tx.isActive()) {
                        try {
                            hibernateSession.getTransaction().rollback();
                        } catch (Exception e2) {
                            this.getLogger().warn("Failed to roll back transaction after exception.");
                        }
                    }
                }
            }
            throw e;
        } finally {
            if (hibernateSession != null) {
                if (hibernateSession.isOpen()) {
                    Transaction tx = hibernateSession.getTransaction();
                    if (tx == null || tx.isActive()) { // Don't flush if we've rolled back
                        if (hibernateSession.isDirty()) {
                            hibernateSession.flush();
                        }
                    }
                    hibernateSession.close();
                }
            }
        }
    }
    
    protected void outputResponse(Response respObj, HttpServletResponse servletResp, HttpServletRequest servletReq) throws IOException {
        servletResp.setStatus(respObj.getStatusCode());
        if (respObj instanceof ErrorResponse) {
            outputErrorResponse((ErrorResponse)respObj, servletResp);
        } else if (respObj instanceof RedirectResponse) {
            outputRelativeRedirectResponse((RedirectResponse)respObj, servletResp, servletReq);
        } else if (respObj instanceof ViewRenderingResponse) {
            outputViewResponse((ViewRenderingResponse)respObj, servletResp, servletReq);
        }
    }

    private void outputErrorResponse(ErrorResponse er, HttpServletResponse servletResp) throws IOException {
        servletResp.sendError(er.getStatusCode(), er.getMessage());
    }
    
    private void outputRelativeRedirectResponse(RedirectResponse respObj, HttpServletResponse servletResp, HttpServletRequest servletReq) throws IOException {
        String fullPath = this.getAppPathPrefix(servletReq) + respObj.getPath();
        
        String host = servletReq.getServerName();
        String port = Integer.toString(servletReq.getServerPort());
        String scheme = servletReq.getScheme();
        
        String url = scheme + "://" + host + ":" + port + fullPath;

        servletResp.sendRedirect(servletResp.encodeRedirectURL(url));
    }
    
    private void outputViewResponse(ViewRenderingResponse respObj, HttpServletResponse servletResp, HttpServletRequest servletReq) throws IOException {
        servletResp.setStatus(respObj.getStatusCode());
        servletResp.setContentType(respObj.getContentType());
        if (!respObj.getContentDisposition().equals("inline"))
            servletResp.setHeader("Content-Disposition", respObj.getContentDisposition());
        
        ResponseBodyWriter rw = respObj.getResponseWriter();
        if (rw instanceof TemplateRenderer) {
            // Take a small shortcut: use the servet's writer directly.
            ((TemplateRenderer)rw).render(servletResp.getWriter());
        } else {
            Charset cs;
            try {
                cs = Charset.forName(servletResp.getCharacterEncoding());
            } catch (Exception e) {
                this.getLogger().error("Failed to get character set " + servletResp.getCharacterEncoding(), e);
                cs = Charset.defaultCharset();
            }
            rw.writeResponse(servletResp.getOutputStream(), cs);
        }
        
    }
    
    private void logExceptionWithFullBacktrace(Exception exception, String initialMsg) {
        StringWriter msg = new StringWriter();
        PrintWriter msgStream = new PrintWriter(msg);
        msgStream.println(initialMsg + ": ");
        Throwable t = exception;
        do {
            msgStream.print("Caused by: " + t.getClass().getName() + ": " + t.getMessage());
            for (StackTraceElement element : t.getStackTrace()) {
                msgStream.println("\t" + element.toString());
            }
            t = t.getCause();
            
            // Follow non-standard cause hierarchies
            if (t != null) {
                if (t instanceof SQLException) {
                    t = ((SQLException)t).getNextException();
                }
            }
            
        } while (t != null);
        msgStream.close();
        this.getLogger().error(msg.toString());
    }
    
    @Override
    protected void service(HttpServletRequest httpReq, HttpServletResponse httpResp) throws ServletException, IOException {
        try {
            HttpSession session = httpReq.getSession(true);
            UserSession userSession = (UserSession)session.getAttribute(this.getUserSessionKey());
            if (userSession == null) {
                userSession = this.getDefaultUserSession();
            } else {
                userSession.hop();
            }
            
            Controller<? extends Request> frontController = this.getFrontController();
            Request req = this.createRequestForController(frontController, httpReq, userSession);
            Response resp = this.callFrontController(frontController, req);
            
            userSession = req.getAttribs().getUserSession(); // Might have been changed
            session.setAttribute(this.getUserSessionKey(), userSession);
            
            this.outputResponse(resp, httpResp, httpReq);
        } catch (Exception e) {
            logExceptionWithFullBacktrace(e, "Exception during service()");
            httpResp.sendError(500);
        }
    }
    
}
