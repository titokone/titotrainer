package fi.helsinki.cs.titotrainer.app.model;

import static org.junit.Assert.*;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import fi.helsinki.cs.titotrainer.testsupport.TitoTestCase;
import fi.helsinki.cs.titotrainer.testsupport.app.model.SimpleCriterion;
import fi.helsinki.cs.titotrainer.testsupport.app.model.sample.SampleAnswers;
import fi.helsinki.cs.titotrainer.testsupport.app.model.sample.SampleCriteria;
import fi.helsinki.cs.titotrainer.testsupport.app.model.sample.SampleInputs;
import fi.helsinki.cs.titotrainer.testsupport.app.model.sample.SampleTasks;
import fi.helsinki.cs.titotrainer.testsupport.app.model.sample.SampleUsers;
import fi.helsinki.cs.titotrainer.testsupport.framework.model.TestDb;

public class ValidationTest extends TitoTestCase {
    
    private Session session;

    private SampleAnswers  answers;
    private SampleCriteria criteria;
    private SampleInputs   inputs;
    private SampleTasks    tasks;
    private SampleUsers    users;
    
    @Override
    protected SessionFactory getNewSessionFactory() {
        AnnotationConfiguration conf = TestDb.createHibernateConfig();
        conf.addAnnotatedClass(SimpleCriterion.class);
        this.sessionFactory = TestDb.createSessionFactory(conf);
        return this.sessionFactory;
    }

    @Before
    public void setUp() {
        this.session = this.openAutoclosedSession();
        this.tasks = new SampleTasks(this.session);
        this.users = this.tasks.users;
        this.inputs = new SampleInputs(this.session, this.tasks);
        this.answers = new SampleAnswers(this.session, this.tasks);
        this.criteria = new SampleCriteria(this.session, this.tasks);
    }
    
    /* General Tests */
    
    @Test
    public void shouldSaveValidValidation() {
        Validation validation = new Validation();
        validation.setAnswer(this.answers.commentOnlyAnswer);
        this.answers.commentOnlyAnswer.getValidations().add(validation);
        validation.setCriterion(this.criteria.pointlessCriterion);
        this.criteria.pointlessCriterion.getValidations().add(validation);
        this.session.save(validation);
        this.session.flush();
    }
    
    /* Test setAnswer(...) */
    
    @Test
    public void shouldAcceptValidAnswer() {
        Validation validation = new Validation();        
        validation.setAnswer(this.answers.commentOnlyAnswer);
        assertEquals(this.answers.commentOnlyAnswer, validation.getAnswer());
    }
    
    @Test(expected = NullPointerException.class)
    public void shouldNotAcceptNullAnswer() {
        Validation validation = new Validation();        
        validation.setAnswer(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptAnswerThatCollidesWithInput() {
        Validation validation = new Validation();
        Answer answer = new Answer(this.users.pullman, this.tasks.standardTask, "; Some answer!", true);
        Input input = new Input(this.tasks.minimalTask, "1,2,3,4", false);
        validation.setInput(input);
        validation.setAnswer(answer);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptAnswerThatCollidesWithCriterion() {
        Validation validation = new Validation();
        Answer answer = new Answer(this.users.pullman, this.tasks.standardTask, "; Some answer!", true);
        Criterion criterion = SampleCriteria.createPointlessCriterion(this.tasks.minimalTask);
        validation.setCriterion(criterion);
        validation.setAnswer(answer);
    }
    
    /* Test setCriterion(...) */
    
    @Test
    public void shouldAcceptValidCriterion() {
        Validation validation = new Validation();
        validation.setCriterion(this.criteria.pointlessCriterion);
        assertEquals(this.criteria.pointlessCriterion, validation.getCriterion());
    }
    
    @Test(expected = NullPointerException.class)
    public void shouldNotAcceptNullCriterion() {
        Validation validation = new Validation();
        validation.setCriterion(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptCriterionThatCollidesWithInput() {
        Validation validation = new Validation();
        Input input = new Input(this.tasks.standardTask, "1,2,3,4", false);
        Criterion criterion = SampleCriteria.createPointlessCriterion(this.tasks.minimalTask);
        validation.setInput(input);
        validation.setCriterion(criterion);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptCriterionThatCollidesWithAnswer() {
        Validation validation = new Validation();
        Answer answer = new Answer(this.users.nykanen, this.tasks.standardTask, "SVC =HALT", true);
        Criterion criterion = SampleCriteria.createPointlessCriterion(this.tasks.minimalTask);
        validation.setAnswer(answer);
        validation.setCriterion(criterion);        
    }
    
    /* Test setInput(...) */
    
    @Test
    public void shouldAcceptValidInput() {
        Validation validation = new Validation();
        validation.setInput(this.inputs.standardInput);
        assertEquals(this.inputs.standardInput, validation.getInput());
    }
    
    @Test
    public void shouldAcceptNullInput() {
        Validation validation = new Validation();
        /* First store a valid input */
        validation.setInput(this.inputs.standardInput);
        /* Now try to set it to null */
        validation.setInput(null);
        assertNull(validation.getInput());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptInputThatCollidesWithAnswer() {
        Validation validation = new Validation();
        Answer answer = new Answer(this.users.pullman, this.tasks.standardTask, "; Some answer!", true);
        Input input = new Input(this.tasks.minimalTask, "1,2,3,4", false);
        validation.setAnswer(answer);
        validation.setInput(input);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptInputThatCollidesWithCriterion() {
        Validation validation = new Validation();
        Criterion criterion = SampleCriteria.createPointlessCriterion(this.tasks.standardTask);
        Input input = new Input(this.tasks.minimalTask, "1,2,3,4", false);
        validation.setCriterion(criterion);
        validation.setInput(input);
    }
    
    @Test
    public void setSatisfiedShouldBehaveNormally() {
        Validation validation = new Validation();
        validation.setSatisfied(true);
        assertTrue(validation.isSatisfied());
        validation.setSatisfied(false);
        assertFalse(validation.isSatisfied());
    }
    
    
    private Validation createAndSaveValidation() {
        Validation val = new Validation();
        val.setAnswer(answers.commentOnlyAnswer);
        val.setCriterion(criteria.pointlessCriterion);
        val.setInput(inputs.standardInput);
        
        answers.commentOnlyAnswer.getValidations().add(val);
        criteria.pointlessCriterion.getValidations().add(val);
        session.save(val);
        session.flush();
        return val;
    }
    
    @Test
    public void shouldBeCascadedWhenAnswerIsDeleted() {
        Validation val = createAndSaveValidation();
        
        long id = val.getId();
        session.evict(val);
        
        val.getAnswer().getTask().getAnswers().remove(val.getAnswer());
        session.delete(val.getAnswer());
        session.flush();
        
        assertNull(session.get(Validation.class, id));
    }
    
    @Test
    public void shouldBeCascadedWhenCriterionIsDeletedIfTheValidationIsRemovedFromItsAnswerFirst() {
        Validation val = createAndSaveValidation();
        
        long id = val.getId();
        session.evict(val);
        
        assert(val.getCriterion().getInput() == null); // This criterion doesn't happen to be associated with an input
        val.getCriterion().getTask().getCriteria().remove(val.getCriterion());
        val.getAnswer().getValidations().remove(val);
        session.delete(val.getCriterion());
        session.flush();
        
        assertNull(session.get(Validation.class, id));
    }
    
    @Test
    @Ignore //TODO
    public void shouldBeCascadedWhenInputIsDeletedIfTheValidationIsRemovedFromItsAnswerFirst() {
        Validation val = createAndSaveValidation();
        
        long id = val.getId();
        session.evict(val);
        
        val.getInput().getTask().getInputs().remove(val.getInput());
        val.getAnswer().getValidations().remove(val);
        session.delete(val.getInput());
        session.flush();
        
        assertNull(session.get(Validation.class, id));
    }
    
}