package fi.helsinki.cs.titotrainer.app.model.criteria;

import static org.junit.Assert.*;

import java.util.Locale;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.junit.Before;
import org.junit.Test;

import fi.helsinki.cs.titotrainer.app.model.Criterion;
import fi.helsinki.cs.titotrainer.app.model.Task;
import fi.helsinki.cs.titotrainer.testsupport.TitoTestCase;
import fi.helsinki.cs.titotrainer.testsupport.app.model.SimpleCriterion;
import fi.helsinki.cs.titotrainer.testsupport.app.model.sample.SampleTasks;
import fi.helsinki.cs.titotrainer.testsupport.framework.model.TestDb;

public class CriterionPersistenceTest extends TitoTestCase {
    
    private Session hs;
    private Task task;
    
    @Override
    protected SessionFactory getNewSessionFactory() {
        AnnotationConfiguration conf = TestDb.createHibernateConfig();
        conf.addAnnotatedClass(SimpleCriterion.class);
        this.sessionFactory = TestDb.createSessionFactory(conf);
        return this.sessionFactory;
    }
    
    @Before
    public void setUp() {
        this.hs = this.openAutoclosedSession();
        this.task = new SampleTasks(this.hs).minimalTask;
    }
    
    private void test(Class<? extends Criterion> cls, String params) {
        try {
            test(cls.newInstance(), params);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        
        
    }
    
    private void test(Criterion c, String params) {
        c.setParameters(params);
        c.setTask(task);
        c.setAcceptMessage(Locale.ENGLISH, "yay!");
        c.setRejectMessage(Locale.ENGLISH, "nay!");
        c.setQualityCriterion(true);
        task.getCriteria().add(c);
        hs.save(c);
        
        hs.flush();
        hs.close();
        hs = this.openAutoclosedSession();
        
        c = (Criterion)this.hs.createQuery("FROM Criterion WHERE id = ?").setLong(0, c.getId()).uniqueResult();
        assertEquals(params, c.getParameters());
        assertEquals(c.getTask().getId(), task.getId());
        assertEquals("yay!", c.getAcceptMessage(Locale.ENGLISH));
        assertEquals("nay!", c.getRejectMessage(Locale.ENGLISH));
        assertEquals(true, c.isQualityCriterion());
    }
    
    @Test
    public void simpleCriterionShouldBePersistable() {
        test(SimpleCriterion.class, "hello");
    }
    
    @Test
    public void registerCriterionShouldBePersistable() {
        test(RegisterCriterion.class, "R3 = 33");
    }
    
    @Test
    public void modelRegisterCriterionShouldBePersistable() {
        test(ModelRegisterCriterion.class, "R3 =");
    }
    
    @Test
    public void symbolCriterionShouldBePersistable() {
        test(SymbolCriterion.class, "x = 33");
    }
    
    @Test
    public void modelSymbolCriterionShouldBePersistable() {
        test(ModelSymbolCriterion.class, "x =");
    }
    
    @Test
    public void screenOutputCriterionShouldBePersistable() {
        test(ScreenOutputCriterion.class, "1,2,3");
    }
    
    @Test
    public void modelScreenOutputCriterionShouldBePersistable() {
        test(ModelScreenOutputCriterion.class, "");
    }
    
    @Test
    public void requiredInstructionsCriterionShouldBePersistable() {
        test(RequiredInstructionsCriterion.class, "SUB,STORE,LOAD");
    }
    
    @Test
    public void forbiddenInstructionsCriterionShouldBePersistable() {
        test(ForbiddenInstructionsCriterion.class, "SUB,STORE,LOAD");
    }
    
    @Test
    public void codeSizeCriterionShouldBePersistable() {
        test(CodeSizeCriterion.class, " < 10");
    }
    
    @Test
    public void dataSizeCriterionShouldBePersistable() {
        test(DataSizeCriterion.class, " < 10");
    }
    
    @Test
    public void maxStackSizeCriterionShouldBePersistable() {
        test(MaxStackSizeCriterion.class, " < 10");
    }
    
    @Test
    public void executedInstructionsCriterionShouldBePersistable() {
        test(ExecutedInstructionsCriterion.class, " < 10");
    }
    
    @Test
    public void dataReferencesCriterionShouldBePersistable() {
        test(DataReferencesCriterion.class, " < 10");
    }
    
    @Test
    public void memoryReferencesCriterionShouldBePersistable() {
        test(MemoryReferencesCriterion.class, " < 10");
    }
    
}
