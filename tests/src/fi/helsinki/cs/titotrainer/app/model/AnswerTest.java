package fi.helsinki.cs.titotrainer.app.model;

import static org.junit.Assert.*;

import java.util.Date;

import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Before;
import org.junit.Test;

import fi.helsinki.cs.titotrainer.testsupport.TitoTestCase;
import fi.helsinki.cs.titotrainer.testsupport.app.model.sample.SampleAnswers;
import fi.helsinki.cs.titotrainer.testsupport.app.model.sample.SampleCriteria;
import fi.helsinki.cs.titotrainer.testsupport.app.model.sample.SampleTasks;
import fi.helsinki.cs.titotrainer.testsupport.app.model.sample.SampleUsers;

public class AnswerTest extends TitoTestCase {
    
    private Session session;
    
    private SampleTasks tasks;
    private SampleUsers users;
    
    @Before
    public void setUp() {        
        this.session = this.openAutoclosedSession();
        this.tasks = new SampleTasks(this.session);
        this.users = this.tasks.users;
    }
    
    @Test
    public void shouldSaveValidAnswer() {
        Answer answer = new Answer(this.users.pullman, this.tasks.minimalTask, "SVC =HALT", true);
        this.tasks.minimalTask.getAnswers().add(answer);
        this.session.save(answer);
        this.session.flush();
    }
    
    @Test(expected = ConstraintViolationException.class)
    public void shouldNotAllowTwoAnswersForSameTaskBySameUser() {
        Answer answer = new Answer(this.users.pullman, this.tasks.minimalTask, "SVC =HALT", true);
        this.tasks.minimalTask.getAnswers().add(answer);
        this.session.save(answer);
        
        Answer answer2 = new Answer(this.users.pullman, this.tasks.minimalTask, "SVC =HALT", true);
        this.tasks.minimalTask.getAnswers().add(answer2);
        this.session.save(answer2);
        this.session.flush();
    }
    
    @Test
    public void shouldBeDeletable() {
        Criterion criterion = new SampleCriteria(this.session, this.tasks).pointlessCriterion;
        Task task = criterion.getTask();
        
        Answer answer = new Answer(this.users.pullman, task, "SVC =HALT", true);
        task.getAnswers().add(answer);
        
        Validation validation = new Validation();
        validation.setAnswer(answer);
        validation.setCriterion(criterion);
        answer.getValidations().add(validation);
        
        this.session.save(answer);
        this.session.save(validation);
        this.session.flush();
        
        task.getAnswers().remove(answer);
        this.session.delete(answer);
        this.session.flush();
    }
    
    /* Test setCode(...) */
    
    @Test
    public void shouldAcceptValidCode() {
        Answer answer = new Answer();
        answer.setCode("SVC =HALT");
        assertEquals("SVC =HALT", answer.getCode());
    }
    
    @Test(expected = NullPointerException.class)
    public void shouldNotAcceptNullCode() {
        Answer answer = new Answer();
        answer.setCode(null);
    }
    
    /* Test setTask(...) */
    
    @Test
    public void shouldAcceptValidTask() {
        Answer answer = new Answer();
        answer.setTask(this.tasks.minimalTask);
        assertEquals(this.tasks.minimalTask, answer.getTask());
    }
    
    @Test(expected = NullPointerException.class)
    public void shouldNotAcceptNullTask() {
        Answer answer = new Answer();
        answer.setTask(null);
    }
    
    /* Test setTimestamp(...) */
    
    @Test
    public void shouldAcceptValidTimestamp() {
        Answer answer = new Answer();
        answer.setTimestamp(new Date());
    }
    
    @Test(expected = NullPointerException.class)
    public void shouldNotAcceptNullTimestamp() {
        Answer answer = new Answer();
        answer.setTimestamp(null);
    }
    
    /* Test setUser(...) */
    
    @Test
    public void shouldAcceptValidUser() {
        Answer answer = new Answer();
        answer.setUser(this.users.pullman);
        assertEquals(this.users.pullman, answer.getUser());
    }
    
    @Test(expected = NullPointerException.class)
    public void shouldNotAcceptNullUser() {
        Answer answer = new Answer();
        answer.setUser(null);
    }
    
    /* Test deepCopy() */
    
    @Test(expected = NullPointerException.class)
    public void deepCopyShouldThrowNullPointerExceptionIfTaskIsNotSet() {
        Answer source = SampleAnswers.createAnswerComment(null, this.users.nykanen);
        source.deepCopy();
    }
    
    @Test(expected = NullPointerException.class)
    public void deepCopyShouldThrowNullPointerExceptionIfUserIsNotSet() {
        Answer source = SampleAnswers.createAnswerComment(this.tasks.standardTask, null);
        source.deepCopy();        
    }
    
    @Test
    public void deepCopyShouldReturnTrueCopy() {
        Answer source = SampleAnswers.createAnswerComment(this.tasks.standardTask, this.users.nykanen);
        this.session.save(source);
        Answer copy = source.deepCopy();
        assertNotNull(copy);
        assertNotSame(source, copy);
        assertFalse(source.equals(copy));
        assertFalse(copy.equals(source));
    }
    
    @Test
    public void deepCopyShouldNotCopyTaskOrUserOrCode() {
        Answer source = SampleAnswers.createAnswerComment(this.tasks.standardTask, this.users.nykanen);
        Answer copy = source.deepCopy();
        assertSame(source.getTask(), copy.getTask());
        assertSame(source.getUser(), copy.getUser());
        assertSame(source.getCode(), copy.getCode());
    }
    
    @Test
    public void deepCopyShouldCopyTimestamp() {
        Answer source = SampleAnswers.createAnswerComment(this.tasks.standardTask, this.users.nykanen);
        Answer copy = source.deepCopy();
        assertNotSame(source.getTimestamp(), copy.getTimestamp());
        assertEquals(source.getTimestamp(), copy.getTimestamp());
    }
    
    @Test
    public void deepCopyShouldNotUpdateCollectionInTask() {
        Answer source = SampleAnswers.createAnswerComment(this.tasks.standardTask, this.users.nykanen);
        Answer copy = source.deepCopy();
        assertFalse(source.getTask().getAnswers().contains(copy));
    }
    
    @Test
    public void deepCopyShouldNotCopyOrDuplicateValidations() {
        Answer source = SampleAnswers.createAnswerComment(this.tasks.standardTask, this.users.nykanen);
        Criterion criterion = SampleCriteria.createPointlessCriterion(this.tasks.standardTask);
        Validation validation = new Validation();
        validation.setCriterion(criterion);
        validation.setAnswer(source);
        source.getValidations().add(validation);
        Answer copy = source.deepCopy();
        assertEquals(1, source.getValidations().size());
        assertSame(validation, source.getValidations().iterator().next());
        assertEquals(0, copy.getValidations().size());
    }
    
    /* Test deepCopy(boolean) */
    
    @Test
    public void deepCopyShouldUpdateCollectionInTask() {
        Answer source = SampleAnswers.createAnswerComment(this.tasks.standardTask, this.users.nykanen);
        Answer copy = source.deepCopy(true);
        // Can't save the copy because that would cause a unique constraint violation.
        assertTrue(this.tasks.standardTask.getAnswers().contains(source));
        assertTrue(this.tasks.standardTask.getAnswers().contains(copy));
    }

}