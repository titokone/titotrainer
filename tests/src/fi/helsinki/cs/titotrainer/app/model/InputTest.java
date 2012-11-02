package fi.helsinki.cs.titotrainer.app.model;

import static org.junit.Assert.*;

import org.hibernate.PropertyAccessException;
import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;

import fi.helsinki.cs.titotrainer.testsupport.TitoTestCase;
import fi.helsinki.cs.titotrainer.testsupport.app.model.sample.SampleInputs;
import fi.helsinki.cs.titotrainer.testsupport.app.model.sample.SampleTasks;

public class InputTest extends TitoTestCase {

    private Session session;
    
    private SampleTasks tasks;
    
    @Before
    public void setUp() {
        this.session = this.openAutoclosedSession();
        this.tasks = new SampleTasks(this.session);
    }    
    
    /* General Tests */
    
    @Test
    public void shouldSaveValidInput() {
        Input input = new Input(this.tasks.minimalTask, "1,2,3,4", false);
        this.tasks.minimalTask.getInputs().add(input);
        this.session.save(input);
        this.session.flush();
    }
    
    //TODO: Investigate this!!!
    //@Test(expected = PropertyValueException.class) <- Doesn't work... Why???
    @Test(expected = PropertyAccessException.class)
    public void shouldNotSaveInputWithoutTask() {
        Input input = new Input(null, "1,2,3,4", false);
        this.session.save(input);
        this.session.flush();
    }
    
    /* Test setInput(...) */
    
    @Test
    public void shouldAcceptValidInput() {
        Input input = new Input();
        input.setInput("3,6,1,7");
        assertEquals("3,6,1,7", input.getInput());
    }
    
    @Test
    public void shouldStripWhitespacesFromInput() {
        Input input = new Input();
        input.setInput("   2,5,12,999     ");
        assertEquals("2,5,12,999", input.getInput());
    }
    
    @Test(expected = NullPointerException.class)
    public void shouldNotAcceptNullInput() {
        Input input = new Input();
        input.setInput(null);
    }
    
    /* Test setSecret (...) */
    
    @Test
    public void secretGetterSetterShouldBehaveAsExpected() {
        Input input = new Input();
        input.setSecret(false);
        assertEquals(false, input.isSecret());
        input.setSecret(true);
        assertEquals(true, input.isSecret());
    }
    
    /* Test setTask(...) */
    
    @Test
    public void shouldAcceptValidTask() {
        Input input = new Input();
        input.setTask(this.tasks.minimalTask);
        assertEquals(this.tasks.minimalTask, input.getTask());
    }
    
    @Test(expected = NullPointerException.class)
    public void shouldNotAcceptNullTask() {
        Input input = new Input();
        input.setTask(null);
    }
    
    /* Test deepCopy() */
    
    @Test(expected = NullPointerException.class)
    public void deepCopyShouldThrowNullPointerExceptionOnNullTask() {
        Input source = SampleInputs.createInputStandard(null);
        source.deepCopy();
    }
    
    @Test
    public void deepCopyShouldNotCopyTaskOrInput() {
        Input source = SampleInputs.createInputStandard(this.tasks.standardTask);
        Input copy = source.deepCopy();
        assertSame(source.getTask(), copy.getTask());
        assertSame(source.getInput(), copy.getInput());
    }

    @Test
    public void deepCopyShouldPreserveSecrecyStatus() {
        Input source = SampleInputs.createInputStandard(this.tasks.standardTask);
        source.setSecret(true);
        Input copyOne = source.deepCopy();
        source.setSecret(false);
        assertTrue(copyOne.isSecret());
        Input copyTwo = source.deepCopy();
        assertFalse(copyTwo.isSecret());
    }

    @Test
    public void deepCopyShouldNotUpdateCollectionsInTask() {
        Input source = SampleInputs.createInputStandard(this.tasks.standardTask);
        Input copy = source.deepCopy();
        assertFalse(source.getTask().getInputs().contains(copy));
    }

    /* Test deepCopy(Task, boolean) */
    
    @Test
    public void deepCopyShouldReturnTrueCopy() {
        Input source = SampleInputs.createInputStandard(this.tasks.standardTask);
        this.session.save(source);
        Input copy = source.deepCopy(this.tasks.minimalTask, false);
        assertNotSame(source, copy);
        assertFalse(source.equals(copy));
        assertFalse(copy.equals(source));        
    }
    
    @Test
    public void deepCopyShouldAttachCopyToTargetTask() {
        Input source = SampleInputs.createInputStandard(this.tasks.standardTask);
        Input copy = source.deepCopy(this.tasks.minimalTask, false);
        assertSame(this.tasks.minimalTask, copy.getTask());
    }
    
    @Test
    public void deepCopyShouldUpdateCollectionsInTask() {
        Input source = SampleInputs.createInputStandard(this.tasks.standardTask);
        Input copy = source.deepCopy(this.tasks.minimalTask, true);
        this.session.save(source);
        this.session.save(copy);
        assertTrue(this.tasks.standardTask.getInputs().contains(source));
        assertTrue(this.tasks.minimalTask.getInputs().contains(copy));
    }

    /* //TODO: move to a test of PartialInputCmp 
    @Test
    public void compareToMethodShouldCompareInputs() {
        Input a = new Input();
        Input b = new Input();
        
        assertEquals(0, b.compareTo(null));
        assertEquals(0, a.compareTo(b));
        b.setInput("");
        assertEquals(0, b.compareTo(null));
        
        a.setInput("a");
        b.setInput("b");
        assertTrue(a.compareTo(b) < 0);
        assertTrue(b.compareTo(a) > 0);
    }
    */
    
}