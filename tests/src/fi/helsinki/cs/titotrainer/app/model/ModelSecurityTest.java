package fi.helsinki.cs.titotrainer.app.model;

import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;

import fi.helsinki.cs.titotrainer.app.model.access.ModelAccessController;
import fi.helsinki.cs.titotrainer.framework.model.handler.ModelAccessChecker;
import fi.helsinki.cs.titotrainer.framework.model.handler.ModelAccessDeniedException;
import fi.helsinki.cs.titotrainer.testsupport.TitoTestCase;
import fi.helsinki.cs.titotrainer.testsupport.app.model.sample.SampleTasks;
import fi.helsinki.cs.titotrainer.testsupport.app.model.sample.SampleUsers;

public class ModelSecurityTest extends TitoTestCase {
    
    private Session setupHs; // For test case setup
    private Session testHs; // For trying out access
    
    private SampleTasks sampleTasks;
    private SampleUsers sampleUsers;
    
    @Before
    public void setUp() {
        this.setupHs = this.openAutoclosedSession();
        this.sampleTasks = new SampleTasks(this.setupHs);
        this.sampleUsers = this.sampleTasks.users;
        
        this.testHs = this.openAutoclosedSession();
    }
    
    @Override
    protected boolean isModelAccessCheckerEnabled() {
        return false; // We'll enable it explicitly in each test
    }
    
    private void enableAccessChecks(User user) {
        ModelAccessChecker.enableForSession(this.testHs, ModelAccessController.getInstance(), user.getParentRole());
    }
    
    @Test(expected = ModelAccessDeniedException.class)
    public void studentShouldNotBeAbleToReadHiddenTasks() {
        sampleTasks.minimalTask.setHidden(true);
        this.setupHs.update(sampleTasks.minimalTask);
        this.setupHs.flush();
        
        enableAccessChecks(sampleUsers.nykanen);
        
        // ok, WTF? get() triggers our access checker event handler but load() doesn't
        this.testHs.get(Task.class, sampleTasks.minimalTask.getId());
    }
}
