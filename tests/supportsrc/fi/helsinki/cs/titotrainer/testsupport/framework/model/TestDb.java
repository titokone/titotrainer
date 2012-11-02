package fi.helsinki.cs.titotrainer.testsupport.framework.model;

import java.io.File;
import java.sql.SQLException;

import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.dialect.Dialect;

import fi.helsinki.cs.titotrainer.testsupport.TitoTestCase;
import fi.helsinki.cs.titotrainer.testsupport.app.model.SimpleCriterion;

/**
 * <p>Configures the test hibernate instance.</p>
 * 
 * <p>A test database is an in-memory database with
 * the same schema as the application database, except
 * that a table of {@link TestDbEntity} objects is added.</p>
 * 
 * <p>Test cases should extend {@link TitoTestCase} to get
 * a nice interface to the test database.</p>
 * 
 * @see TitoTestCase
 */
public class TestDb {
    
    private static final String baseConnectionUrl = "jdbc:hsqldb:mem";
    private static int nextTestDbNumber = 1;
    private static AnnotationConfiguration defaultHibernateConfig = null;
    
    /**
     * <p>Creates the Hibernate configuration used by tests to access an in-memory database.</p>
     * 
     * <p>This can be passed to {@link #createSessionFactory(AnnotationConfiguration)}.
     * The connection URL used by that method cannot be changed but other parameters can.</p>
     */
    public static AnnotationConfiguration createHibernateConfig() {
        AnnotationConfiguration hibConfig = new AnnotationConfiguration();
        
        hibConfig.configure(new File("WEB-INF/hibernate.cfg.xml"));
        
        hibConfig.setProperty("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver");
        hibConfig.setProperty("hibernate.connection.username", "sa");
        hibConfig.setProperty("hibernate.connection.password", "");
        hibConfig.setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
        // connection url is set in createSessionFactory()
        
        hibConfig.addAnnotatedClass(TestDbEntity.class);
        hibConfig.addAnnotatedClass(SimpleCriterion.class);
        
        return hibConfig;
    }
    
    /**
     * Creates a session factory to an independent in-memory
     * test database using the default configuration and
     * initializes the database tables.
     * 
     * @return A new session factory using an in-memory database.
     */
    public static synchronized SessionFactory createSessionFactory() {
        if (defaultHibernateConfig == null) {
            defaultHibernateConfig = createHibernateConfig();
        }
        
        return createSessionFactory(defaultHibernateConfig);
    }
    
    /**
     * <p>Creates a session factory to an independent in-memory
     * test database using the given configuration and
     * initializes the database tables.</p>
     * 
     * <p>The URL of the configuration will be changed to point to the next
     * unused in-memory database.</p>
     * 
     * @return A new session factory using an in-memory database.
     */
    public static synchronized SessionFactory createSessionFactory(AnnotationConfiguration config) {
        String url = baseConnectionUrl + ":test" + Integer.toString(nextTestDbNumber++);
        config.setProperty("hibernate.connection.url", url);
        
        SessionFactory sf = config.buildSessionFactory();
        initDatabase(sf, config);
        return sf;
    }
    
    private static void initDatabase(SessionFactory sf, AnnotationConfiguration config) {
        StatelessSession s = sf.openStatelessSession();
        
        String[] schemaScript = config.generateSchemaCreationScript(Dialect.getDialect(config.getProperties()));
        
        for (String command : schemaScript) {
            try {
                s.connection().createStatement().executeUpdate(command);
            } catch (SQLException e) {
                throw new RuntimeException("Failed to initialize test database", e);
            }
        }
    }
    
    protected TestDb() {
        // Static methods only. No need for instances.
    }
}
