package fi.helsinki.cs.titotrainer.app.view;

import java.io.IOException;

import fi.helsinki.cs.titotrainer.app.model.User;
import fi.helsinki.cs.titotrainer.app.request.TitoRequest;
import fi.helsinki.cs.titotrainer.app.session.Messenger;
import fi.helsinki.cs.titotrainer.app.view.template.Can;
import fi.helsinki.cs.titotrainer.app.view.template.TemplateUtils;
import fi.helsinki.cs.titotrainer.framework.RequestContext;
import fi.helsinki.cs.titotrainer.framework.i18n.Translator;
import fi.helsinki.cs.titotrainer.framework.request.Request;
import fi.helsinki.cs.titotrainer.framework.view.template.ConcatenatingTemplateRenderer;
import fi.helsinki.cs.titotrainer.framework.view.template.TemplateEngine;
import fi.helsinki.cs.titotrainer.framework.view.template.TemplateRenderer;

/**
 * <p>TitoTrainer-specific HTML view superclass.</p>
 * 
 * <p>
 * This overrides {@link #createTemplateRenderer(Request)}
 * to create a template renderer that puts the response in the
 * TitoTrainer page layout. All variables are also forwared to
 * the layout.
 * </p>
 * 
 * <p>
 * The following template variables are registered:
 * <ul>
 * <li><code>rc</code> - the current {@link RequestContext}.</li>
 * <li><code>req</code> - the current {@link Request}.</li>
 * <li><code>module</code> - either <code>"student"</code>, <code>"admin"</code> or null.</li>
 * <li><code>user</code> - the current {@link User} of the session, or null if not logged in.</li>
 * <li><code>tr</code> - the {@link Translator} for this view.</li>
 * <li><code>messenger</code> - the {@link Messenger} for this view.</li>
 * <li><code>utils</code> - a {@link TemplateUtils} instance.</li>
 * <li><code>can</code> - a {@link Can} instance.</li>
 * </ul>
 * </p>
 */
public abstract class TitoPageView<RequestType extends TitoRequest> extends TitoPageViewCommon<RequestType> {
    
    protected String getHeaderTemplateName() {
        return "layout-header.vm";
    }
    
    protected String getFooterTemplateName() {
        return "layout-footer.vm";
    }
    
    /**
     * <p>Returns the translation for the header template.</p>
     * 
     * <p>By default this is a translator private to {@link TitoPageView}.</p>
     * 
     * @param req The request.
     * @return The header translator. Not null.
     */
    protected Translator getHeaderTranslator(RequestType req) {
        return req.getContext().getTitoTranslation().getClassTranslator(req, TitoPageView.class);
    }
    
    /**
     * See {@link #getHeaderTranslator(TitoRequest)}.
     * 
     * @param req The request.
     * @return The footer translator. Not null.
     */
    protected Translator getFooterTranslator(RequestType req) {
        return req.getContext().getTitoTranslation().getClassTranslator(req, TitoPageView.class);
    }
    
    @Override
    protected TemplateRenderer createTemplateRenderer(RequestType req) throws IOException {
        TemplateEngine te = this.getTemplateEngine(req.getContext());
        TemplateRenderer header = te.createRenderer(this.getHeaderTemplateName());
        TemplateRenderer main = super.createTemplateRenderer(req);
        TemplateRenderer footer = te.createRenderer(this.getFooterTemplateName());
        
        TemplateRenderer composite = new ConcatenatingTemplateRenderer(header, main, footer);
        
        this.addStandardTemplateVariables(req, composite);
        
        // The header and footer may have different translators
        header.put("tr", this.getHeaderTranslator(req));
        footer.put("tr", this.getFooterTranslator(req));
        
        return composite;
    }
    
}
