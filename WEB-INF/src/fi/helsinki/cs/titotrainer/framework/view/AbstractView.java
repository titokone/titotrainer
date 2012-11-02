package fi.helsinki.cs.titotrainer.framework.view;

import java.io.IOException;

import org.hibernate.Session;

import fi.helsinki.cs.titotrainer.framework.RequestContext;
import fi.helsinki.cs.titotrainer.framework.misc.None;
import fi.helsinki.cs.titotrainer.framework.model.TransactionalTask;
import fi.helsinki.cs.titotrainer.framework.request.Request;
import fi.helsinki.cs.titotrainer.framework.response.ErrorResponseException;
import fi.helsinki.cs.titotrainer.framework.response.ResponseBodyWriter;
import fi.helsinki.cs.titotrainer.framework.response.ViewRenderingResponse;
import fi.helsinki.cs.titotrainer.framework.response.ViewResponse;
import fi.helsinki.cs.titotrainer.framework.view.template.NullTemplateRenderer;
import fi.helsinki.cs.titotrainer.framework.view.template.TemplateEngine;
import fi.helsinki.cs.titotrainer.framework.view.template.TemplateRenderer;

/**
 * <p>A convenience class to make writing View implementations easier.</p>
 * 
 * @param <RequestType> The type of the request class.
 */
public abstract class AbstractView<RequestType extends Request> implements View<RequestType> {
    
    /**
     * Returns the name of the template to use.
     * 
     * @return The template name.
     */
    protected String getTemplateName() {
        return null;
    }
    
    /**
     * Returns the template engine this view is to use.
     * 
     * @return A TemplateEngine. Never null.
     */
    protected TemplateEngine getTemplateEngine(RequestContext rc) {
        return rc.getDefaultTemplateEngine();
    }
    
    /**
     * <p>Creates a template renderer.</p>
     * 
     * <p>By default, if <code>getTemplateName()</code>
     * and <code>templateEngine</code> is set then
     * a {@link TemplateRenderer} is created.
     * Otherwise a {@link NullTemplateRenderer} is created.</p>
     * 
     * @param req The request.
     * @return A template renderer.
     * @throws IOException If the template could not be loaded.
     * @throws IllegalStateException templateEngine is null and
     *                               <code>getTemplateName()</code> doesn't
     *                               return null.
     */
    protected TemplateRenderer createTemplateRenderer(RequestType req) throws IOException {
        String templateName = getTemplateName();
        if (templateName != null) {
            TemplateEngine engine = this.getTemplateEngine(req.getContext());
            assert(engine != null);
            return engine.createRenderer(templateName);
        }
        
        return new NullTemplateRenderer();
    }
    
    /**
     * <p>Creates a view response.</p>
     * 
     * <p>The default implementation returns a default {@link DefaultViewResponse}.</p>
     * 
     * @param rw A response writer (such as a TemplateRenderer).
     * @return A view response.
     */
    protected ViewRenderingResponse createResponse(ResponseBodyWriter rw) {
        return new DefaultViewResponse(rw);
    }
    
    /**
     * <p>A more convenient handle() method.</p>
     * 
     * <p>This method is automatically called in a database transcation,
     * which is committed automatically by the caller
     * (or rolled back if an exception was thrown).</p>
     * 
     * @param req The request object.
     * @param hs A Hibernate session.
     * @param tr The template renderer as created by <code>createTemplateRenderer()</code>.
     * @param resp The response object as created by <code>createResponse()</code>.
     * @throws ErrorResponseException if the view cannot be displayed at all for some reason (often a 404).
     */
    protected void handle(RequestType req, Session hs, TemplateRenderer tr, ViewResponse resp) throws ErrorResponseException {
    }
    
    @Override
    public ViewResponse handle(final RequestType req) throws Exception {
        final TemplateRenderer tr = createTemplateRenderer(req);
        final ViewRenderingResponse resp = createResponse(tr);
        
        try {
            new TransactionalTask<None<?>>() {
                @Override
                protected None<?> run(Session s) throws Exception {
                    handle(req, s, tr, resp);
                    return null;
                }
            }.invoke(req.getAttribs().getHibernateSession());
        } catch (ErrorResponseException er) {
            return er.getErrorResponse();
        }
        
        return resp;
    }
    
    
    /*
     * If you feel like doing some arcane wizardry with Java's generics,
     * try to implement getRequestType() here in a generic way.
     * Remember the case where the subclass hierarchy is deeper than 1.
     * 
     * A starting point:
     * http://www.artima.com/weblogs/viewpost.jsp?thread=208860
     * An easier approach could be to reflect the parameter of an overridden handle() method.
     */
    
}
