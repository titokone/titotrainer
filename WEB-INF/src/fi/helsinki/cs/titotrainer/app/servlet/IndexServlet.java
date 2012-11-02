package fi.helsinki.cs.titotrainer.app.servlet;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.app.event.EventCartridge;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

import fi.helsinki.cs.titotrainer.app.TitoRequestContext;
import fi.helsinki.cs.titotrainer.app.controller.FrontController;
import fi.helsinki.cs.titotrainer.app.i18n.TitoTranslation;
import fi.helsinki.cs.titotrainer.app.init.InitCriterionTypeTable;
import fi.helsinki.cs.titotrainer.app.model.titokone.StaticSynchronizedTitokoneFacade;
import fi.helsinki.cs.titotrainer.app.model.titokone.TitokoneFacade;
import fi.helsinki.cs.titotrainer.app.request.TitoRequestAttribs;
import fi.helsinki.cs.titotrainer.app.session.TitoUserSession;
import fi.helsinki.cs.titotrainer.app.view.template.velocity.event.OverridableEscapeHtmlReference;
import fi.helsinki.cs.titotrainer.framework.config.CachingConfigLoader;
import fi.helsinki.cs.titotrainer.framework.config.Config;
import fi.helsinki.cs.titotrainer.framework.config.ConfigLoader;
import fi.helsinki.cs.titotrainer.framework.config.ConfigUtils;
import fi.helsinki.cs.titotrainer.framework.config.MergingConfigLoader;
import fi.helsinki.cs.titotrainer.framework.config.PropertyStreamConfigLoader;
import fi.helsinki.cs.titotrainer.framework.controller.Controller;
import fi.helsinki.cs.titotrainer.framework.model.HibernateInstance;
import fi.helsinki.cs.titotrainer.framework.model.TransactionalTask;
import fi.helsinki.cs.titotrainer.framework.request.RequestAttribs;
import fi.helsinki.cs.titotrainer.framework.response.ErrorResponse;
import fi.helsinki.cs.titotrainer.framework.response.RedirectResponse;
import fi.helsinki.cs.titotrainer.framework.response.Response;
import fi.helsinki.cs.titotrainer.framework.response.ViewRenderingResponse;
import fi.helsinki.cs.titotrainer.framework.servlet.MasterServlet;
import fi.helsinki.cs.titotrainer.framework.session.UserSession;
import fi.helsinki.cs.titotrainer.framework.stream.InputStreamOpener;
import fi.helsinki.cs.titotrainer.framework.stream.PrefixOpener;
import fi.helsinki.cs.titotrainer.framework.stream.ServletContextResourceOpener;
import fi.helsinki.cs.titotrainer.framework.view.template.TemplateEngine;
import fi.helsinki.cs.titotrainer.framework.view.template.velocity.VelocityTemplateEngine;

/**
 * A default servlet that redirects to the student module.
 */
public final class IndexServlet extends MasterServlet {
    
    private static final String HIBERNATE_CONFIG_FILE_PATH = "/WEB-INF/hibernate.cfg.xml";
    private FrontController frontController;
    
    @Override
    public void init() throws ServletException {
        super.init();
        
        try {
            this.getLogger().debug("Verifying the criterion type table");
            Session hs = this.getHibernateInstance().getSessionFactory().openSession();
            new TransactionalTask<Object>() {
                @Override
                protected Object run(Session hs) throws Exception {
                    new InitCriterionTypeTable().run(hs, getRequestContext().getTitoTranslation());
                    return null;
                }
            }.run(hs);
        } catch (Exception e) {
            throw new ServletException("Failed to verify criterion type table.", e);
        }
        
        this.frontController = new FrontController();
    }
    
    @Override
    protected Controller<?> getFrontController() {
        return this.frontController;
    }
    
    @Override
    protected ConfigLoader createConfigLoader() {
        InputStreamOpener overrideOpener = new PrefixOpener(new ServletContextResourceOpener(this.getServletContext()), "/WEB-INF/conf/");
        InputStreamOpener baseOpener = new PrefixOpener(overrideOpener, "default/");
        ConfigLoader overrideLoader = new PropertyStreamConfigLoader(overrideOpener);
        ConfigLoader baseLoader = new PropertyStreamConfigLoader(baseOpener);
        
        ConfigLoader merger = new MergingConfigLoader(baseLoader, overrideLoader);
        
        ConfigLoader cacher = new CachingConfigLoader(merger);
        return cacher;
    }

    private URL getHibernateMappingUrl() {
        try {
            return this.getServletContext().getResource(HIBERNATE_CONFIG_FILE_PATH);
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Failed to form servlet context URL to " + HIBERNATE_CONFIG_FILE_PATH);
        }
    }
    
    @Override
    protected HibernateInstance createHibernateInstance(ConfigLoader configLoader) throws ServletException {
        // Load the DB properties file using our configuration loader
        Config conf;
        try {
            conf = configLoader.load("db");
        } catch (IOException e) {
            throw new ServletException(e);
        }
        
        // Create hibernate's configuration object
        final AnnotationConfiguration hibConf = new AnnotationConfiguration();
        try {
            hibConf.configure(this.getHibernateMappingUrl());
        } catch (Exception e) {
            throw new ServletException(e);
        }
        
        // Add the the properties from our DB properties file
        hibConf.setProperties(ConfigUtils.toProperties(conf));
        
        final SessionFactory sf = hibConf.buildSessionFactory();
        
        return new HibernateInstance() {
            @Override
            public AnnotationConfiguration getConfiguration() {
                return hibConf;
            }
            
            @Override
            public SessionFactory getSessionFactory() {
                return sf;
            }
        };
    }

    @Override
    protected TemplateEngine createDefaultTemplateEngine(ConfigLoader cl) throws Exception {
        Properties velocitySettings = ConfigUtils.toProperties(cl.load("velocity"));
        
        URL templateRoot = this.getServletContext().getResource("/WEB-INF/templates/");
        if (templateRoot == null)
            throw new Exception("Failed to obtain template root directory URL");
        
        velocitySettings.put("resource.loader", "url");
        velocitySettings.put("url.resource.loader.class", "org.apache.velocity.runtime.resource.loader.URLResourceLoader");
        velocitySettings.put("url.resource.loader.root", templateRoot.toString());

        VelocityEngine ve = new VelocityEngine();
        ve.init(velocitySettings);
        
        VelocityTemplateEngine vte = new VelocityTemplateEngine(ve);
        
        EventCartridge ec = new EventCartridge();
        ec.addReferenceInsertionEventHandler(new OverridableEscapeHtmlReference());
        vte.setEventCartrige(ec);
        return vte;
    }
    
    @Override
    protected UserSession getDefaultUserSession() {
        return new TitoUserSession();
    }
    
    @Override
    protected RequestAttribs createRequestAttribs(HttpServletRequest httpReq, UserSession userSession, String basePath, String localPath) throws Exception {
        assert(userSession instanceof TitoUserSession);
        
        Session hs = this.getHibernateInstance().getSessionFactory().openSession();
        
        return new TitoRequestAttribs(
            this.getRequestContext(),
            hs,
            (TitoUserSession)userSession,
            basePath,
            localPath,
            httpReq.getQueryString()
            );
    }
    
    @Override
    protected TitoRequestContext createRequestContext(ConfigLoader cl, TemplateEngine te, Logger logger) {
        try {
            TitoTranslation tt = new TitoTranslation(cl.load("translation"));
            TitokoneFacade tf = new StaticSynchronizedTitokoneFacade();
            return new TitoRequestContext(cl, te, logger, tt, tf);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    protected TitoRequestContext getRequestContext() {
        return (TitoRequestContext)super.getRequestContext();
    }

    @Override
    protected void outputResponse(Response respObj, HttpServletResponse servletResp, HttpServletRequest servletReq) throws IOException {
        if (respObj instanceof RedirectResponse) {
            this.getLogger().debug("Redirect to " + ((RedirectResponse)respObj).getPath());
        } else if (respObj instanceof ViewRenderingResponse) {
            this.getLogger().debug("View");
        } else if (respObj instanceof ErrorResponse) {
            this.getLogger().debug("Error (" + respObj.getStatusCode() + "): " + ((ErrorResponse)respObj).getMessage());
        }
        super.outputResponse(respObj, servletResp, servletReq);
    }
    
    @Override
    protected void service(HttpServletRequest httpReq, HttpServletResponse httpResp) throws ServletException, IOException {
        if (httpReq.getCharacterEncoding() == null)
            httpReq.setCharacterEncoding("UTF-8");
        super.service(httpReq, httpResp);
    }

}
