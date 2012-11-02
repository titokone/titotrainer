package fi.helsinki.cs.titotrainer.framework.model;

import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Provides a try-run-commit-catch-rollback block around
 * a method that operates on a database transaction.
 * 
 * @param <Ret> The return type of the method.
 */
public abstract class TransactionalTask<Ret> {
    
    protected abstract Ret run(Session hibernateSession) throws Exception;
    
    /**
     * Calls {@link #run(Session)} in a try-catch block
     * that will commit on success and rollback on failure.
     * 
     * @param hibernateSession The hibernate session to start the transaction in.
     * @throws Exception Any thrown exception is propagated.
     */
    public Ret invoke(Session hibernateSession) throws Exception {
        Transaction tx = null;
        Ret ret = null;
        
        try {
            tx = hibernateSession.beginTransaction();
            
            ret = this.run(hibernateSession);
            
            if (tx.isActive())
                tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                if (tx.isActive()) {
                    try {
                        tx.rollback();
                    } catch (Exception e2) {
                        // what, if anything, should we do with this?
                    }
                }
            }
            throw e;
        }
        
        return ret;
    }
}
