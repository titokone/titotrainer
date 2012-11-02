package fi.helsinki.cs.titotrainer.app.misc;

import java.io.File;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

import fi.helsinki.cs.titotrainer.framework.config.Config;
import fi.helsinki.cs.titotrainer.framework.config.ConfigLoader;
import fi.helsinki.cs.titotrainer.framework.config.ConfigUtils;
import fi.helsinki.cs.titotrainer.framework.config.MergingConfigLoader;
import fi.helsinki.cs.titotrainer.framework.config.PropertyStreamConfigLoader;
import fi.helsinki.cs.titotrainer.framework.model.HibernateInstance;
import fi.helsinki.cs.titotrainer.framework.stream.FileOpener;
import fi.helsinki.cs.titotrainer.framework.stream.InputStreamOpener;
import fi.helsinki.cs.titotrainer.framework.stream.PrefixOpener;

/**
 * <p>Loads configuration and sets up parts of the framework
 * when not running under a servlet.</p>
 */
public class PlainAppConfiguration {
    
    private static final String HIBERNATE_CONFIG_FILE_PATH = "/WEB-INF/hibernate.cfg.xml";

    private ConfigLoader configLoader;
    
    private HibernateInstance hibernateInstance;

    
    protected ConfigLoader createConfigLoader(String basePath) {
        InputStreamOpener overrideOpener = new PrefixOpener(new FileOpener(), basePath + "/WEB-INF/conf/");
        InputStreamOpener baseOpener = new PrefixOpener(overrideOpener, "default/");
        ConfigLoader overrideLoader = new PropertyStreamConfigLoader(overrideOpener);
        ConfigLoader baseLoader = new PropertyStreamConfigLoader(baseOpener);
        
        return new MergingConfigLoader(baseLoader, overrideLoader);
    }
    
    protected AnnotationConfiguration loadHibernateConfiguration(String basePath) throws Exception {
        // Create hibernate's configuration object
        AnnotationConfiguration hibConf = new AnnotationConfiguration();
        
        // Load base configuration
        hibConf.configure(new File(basePath + HIBERNATE_CONFIG_FILE_PATH));
        
        // Load the DB properties file using our configuration loader
        Config conf;
        conf = configLoader.load("db");
        
        // Add the the properties from our DB properties file
        hibConf.setProperties(ConfigUtils.toProperties(conf));
        
        return hibConf;
    }
    
    protected HibernateInstance createHibernateInstance(String basePath) throws Exception {
        // Create hibernate's configuration object
        final AnnotationConfiguration hibConf = loadHibernateConfiguration(basePath);
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
    
    /**
     * Constructor.
     * 
     * @param basePath The path to the root directory of the application.
     * @throws Exception if something goes wrong.
     */
    public PlainAppConfiguration(String basePath) throws Exception {
        this.configLoader = this.createConfigLoader(basePath);
        this.hibernateInstance = this.createHibernateInstance(basePath);
    }
    
    public ConfigLoader getConfigLoader() {
        return this.configLoader;
    }
    
    public HibernateInstance getHibernateInstance() {
        return this.hibernateInstance;
    }
}
