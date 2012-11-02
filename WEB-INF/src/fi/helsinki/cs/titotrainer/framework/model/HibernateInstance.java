package fi.helsinki.cs.titotrainer.framework.model;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;

/**
 * <p>Bundles together a Hibernate {@link Configuration} and
 * {@link SessionFactory} i.e. everything there is to know
 * about an initialized Hibernate instance.</p>
 */
public interface HibernateInstance {
    /**
     * <p>Returns the configuration used by the session factory.</p>
     * 
     * <p>NOTE: This configuration should not be changed!</p>
     * 
     * @return The configuration used by this hibernate instance.
     */
    public AnnotationConfiguration getConfiguration();
    
    /**
     * Returns the session factory for this hibernate instance.
     * 
     * @return A hibernate session factory.
     */
    public SessionFactory getSessionFactory();
}
