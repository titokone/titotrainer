package fi.helsinki.cs.titotrainer.framework.model;

import static org.junit.Assert.*;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class TransactionalTaskTest {
    
    private static class TestTask extends TransactionalTask<Integer> {
        
        public boolean runCalled = false;
        public Exception exceptionToThrow = null;
        
        @Override
        protected Integer run(Session hibernateSession) throws Exception {
            runCalled = true;
            if (exceptionToThrow != null)
                throw exceptionToThrow;
            return 0;
        }
    };
    
    private Session mockSession;
    private Transaction mockTransaction;
    
    @Before
    public void setUp() {
        mockSession = Mockito.mock(Session.class);
        mockTransaction = Mockito.mock(Transaction.class);
        
        Mockito.stub(mockSession.beginTransaction()).toReturn(mockTransaction);
        
        Mockito.stub(mockTransaction.isActive()).toReturn(true);
    }
    
    @Test
    public void shouldCallRunMethodInTransaction() throws Exception {
        TestTask tt = new TestTask();
        tt.invoke(mockSession);
        Mockito.verify(mockSession).beginTransaction();
        assertTrue(tt.runCalled);
    }
    
    @Test
    public void shouldCommitTransactionOnSuccess() throws Exception {
        new TestTask().invoke(mockSession);
        Mockito.verify(mockTransaction).commit();
        Mockito.verify(mockTransaction, Mockito.never()).rollback();
    }
    
    @Test
    public void shouldRollbackTransactionOnException() throws Exception {
        TestTask tt = new TestTask();
        tt.exceptionToThrow = new Exception();
        try {
            tt.invoke(mockSession);
        } catch (Exception e) {
        }
        Mockito.verify(mockTransaction).rollback();
        Mockito.verify(mockTransaction, Mockito.never()).commit();
    }
    
    @Test
    public void shouldPropagateException() throws Exception {
        TestTask tt = new TestTask();
        tt.exceptionToThrow = new Exception();
        Exception propagated = null;
        try {
            tt.invoke(mockSession);
        } catch (Exception e) {
            propagated = e;
        }
        
        assertSame(tt.exceptionToThrow, propagated);
    }
    
    @Test
    public void shouldIgnoreExceptionOnRollback() throws Exception {
        TestTask tt = new TestTask();
        tt.exceptionToThrow = new Exception();
        Exception rollbackException = new HibernateException("fake rollback exception");
        Exception propagated = null;
        
        Mockito.doThrow(rollbackException).when(mockTransaction).rollback();
        
        try {
            tt.invoke(mockSession);
        } catch (Exception e) {
            propagated = e;
        }
        
        assertSame(tt.exceptionToThrow, propagated);
    }
    
}
