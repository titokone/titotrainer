package fi.helsinki.cs.titotrainer.app.view;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;

import fi.helsinki.cs.titotrainer.app.access.TitoBaseRole;
import fi.helsinki.cs.titotrainer.app.request.TitoRequest;
import fi.helsinki.cs.titotrainer.app.view.template.Can;
import fi.helsinki.cs.titotrainer.app.view.template.TemplateUtils;
import fi.helsinki.cs.titotrainer.framework.i18n.Translator;
import fi.helsinki.cs.titotrainer.framework.response.ErrorResponse;
import fi.helsinki.cs.titotrainer.framework.response.ErrorResponseException;
import fi.helsinki.cs.titotrainer.framework.response.ViewRenderingResponse;
import fi.helsinki.cs.titotrainer.framework.response.ViewResponse;
import fi.helsinki.cs.titotrainer.framework.view.AbstractView;
import fi.helsinki.cs.titotrainer.framework.view.template.TemplateRenderer;


/**
 * The common parts of {@link TitoPageView} and {@link TitoPageFragmentView}.
 */
/*public*/ abstract class TitoPageViewCommon<RequestType extends TitoRequest> extends AbstractView<RequestType> {
    
    /**
     * <p>Returns this view's translator for the user that made the given request.</p>
     * 
     * @param req The request. Must not be null.
     * @return The translator to use. Never null.
     */
    protected Translator getTranslator(RequestType req) {
        return req.getContext().getTitoTranslation().getClassTranslator(req, this.getClass());
    }
    
    /**
     * Loads a lazy Hibernate collection into memory so it
     * can be used after the session is closed (e.g. in a template).
     * 
     * @param c The collection to load.
     */
    protected void loadLazyCollection(Collection<?> c) {
        Iterator<?> i = c.iterator();
        while (i.hasNext())
            i.next();
    }
    
    /**
     * <p>Renders a view fragment into a string buffer.</p>
     * 
     * @param view A page fragment view.
     * @param req The request to pass to the view fragment.
     * @return The output of the page fragment view.
     * @throws ErrorResponseException If the page fragment threw or returned an {@link ErrorResponse}.
     */
    protected <FragmentRequest extends TitoRequest> String renderFragment(TitoPageFragmentView<FragmentRequest> view, FragmentRequest req) throws ErrorResponseException {
        try {
            ViewResponse vr = view.handle(req);
            if (vr instanceof ViewRenderingResponse) {
                Charset cs = Charset.forName("UTF-8");
                
                ByteArrayOutputStream buf = new ByteArrayOutputStream();
                ((ViewRenderingResponse)vr).getResponseWriter().writeResponse(buf, cs);
                return buf.toString(cs.name());
                
            } else if (vr instanceof ErrorResponse) {
                throw new ErrorResponseException((ErrorResponse)vr);
            } else {
                throw new Exception("Unrecognized view response type: " + vr.getClass());
            }
        } catch (ErrorResponseException e) {
            throw e;
        } catch (Exception e) {
            Logger.getLogger(this.getClass()).error("Error rendering view fragment ", e);
            throw new ErrorResponseException(500, e);
        }
    }
    
    protected TemplateRenderer createTemplateRenderer(RequestType req) throws IOException {
        TemplateRenderer tr = super.createTemplateRenderer(req);
        addStandardTemplateVariables(req, tr);
        return tr;
    };
    
    /**
     * <p>Adds standard variables like {@code req} and {@code utils} to the template renderer.</p>
     * 
     * <p>This is called from {@code createTemplateRenderer()}.</p>
     * 
     * @param req The request being processed.
     * @param tr The template renderer to add the variables to.
     */
    protected void addStandardTemplateVariables(RequestType req, TemplateRenderer tr) {
        tr.put("rc", req.getContext());
        tr.put("req", req);
        
        if (req.getLocalPath().startsWith("/student"))
            tr.put("module", "student");
        else if (req.getLocalPath().startsWith("/admin"))
            tr.put("module", "admin");
        else
            tr.put("module", null);
        
        tr.put("user", req.getUserSession().getAuthenticatedUser());
        tr.put("TitoBaseRole", TitoBaseRole.class);
        
        tr.put("tr", this.getTranslator(req));
        
        tr.put("messenger", req.getUserSession().getMessenger());
        
        tr.put("utils", TemplateUtils.getInstance());
        
        tr.put("can", new Can(req.getAttribs().getModuleFrontController()));
    }
    
}
