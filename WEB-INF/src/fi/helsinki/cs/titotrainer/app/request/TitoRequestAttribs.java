package fi.helsinki.cs.titotrainer.app.request;

import org.hibernate.Session;

import fi.helsinki.cs.titotrainer.app.controller.ModuleFrontController;
import fi.helsinki.cs.titotrainer.app.session.TitoUserSession;
import fi.helsinki.cs.titotrainer.framework.RequestContext;
import fi.helsinki.cs.titotrainer.framework.request.RequestAttribs;

/**
 * All {@link TitoRequest} objects' attribute objects
 * are of this type.
 */
public class TitoRequestAttribs extends RequestAttribs {

    private String queryString;
    
    private ModuleFrontController moduleFrontController;
    
    public TitoRequestAttribs(RequestContext context,
                              Session hibernateSession,
                              TitoUserSession userSession,
                              String basePath,
                              String localPath,
                              String queryString
                              ) {
        super(context, hibernateSession, userSession, basePath, localPath);
        
        if (queryString == null)
            this.queryString = "";
        this.queryString = queryString;
    }
    
    @Override
    public TitoUserSession getUserSession() {
        return (TitoUserSession)super.getUserSession();
    }
    
    /**
     * <p>Returns the raw query string i.e. the part of the URL after the "?".</p>
     * 
     * <p>This is never null but may be empty.</p>
     */
    public String getQueryString() {
        return this.queryString;
    }
    
    /**
     * Returns the full path + "?" + queryString.
     */
    public String getQueryPath() {
        String qs = this.getQueryString();
        if (qs.isEmpty())
            return this.getFullPath();
        else
            return this.getFullPath() + "?" + qs;
    }

    /**
     * Returns the router that was the front controller for the module.
     */
    public ModuleFrontController getModuleFrontController() {
        return moduleFrontController;
    }
    
    public void setModuleFrontController(ModuleFrontController moduleFrontController) {
        this.moduleFrontController = moduleFrontController;
    }
    
}
