package fi.helsinki.cs.titotrainer.app.admin.controller;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;

import fi.helsinki.cs.titotrainer.app.access.TitoBaseRole;
import fi.helsinki.cs.titotrainer.app.admin.request.UpdateTaskRequest;
import fi.helsinki.cs.titotrainer.app.model.Answer;
import fi.helsinki.cs.titotrainer.app.model.Category;
import fi.helsinki.cs.titotrainer.app.model.Course;
import fi.helsinki.cs.titotrainer.app.model.Criterion;
import fi.helsinki.cs.titotrainer.app.model.ExecStatus;
import fi.helsinki.cs.titotrainer.app.model.Input;
import fi.helsinki.cs.titotrainer.app.model.TString;
import fi.helsinki.cs.titotrainer.app.model.Task;
import fi.helsinki.cs.titotrainer.app.model.Validation;
import fi.helsinki.cs.titotrainer.app.model.criteria.RequiredInstructionsCriterion;
import fi.helsinki.cs.titotrainer.app.model.titokone.TitokoneState.ExitStatus;
import fi.helsinki.cs.titotrainer.framework.response.RedirectResponse;
import fi.helsinki.cs.titotrainer.framework.response.Response;
import fi.helsinki.cs.titotrainer.testsupport.app.controller.ControllerTestCase;
import fi.helsinki.cs.titotrainer.testsupport.app.model.sample.SampleCategories;
import fi.helsinki.cs.titotrainer.testsupport.app.model.sample.SampleCourses;
import fi.helsinki.cs.titotrainer.testsupport.app.model.sample.SampleUsers;

public class UpdateTaskControllerTest extends ControllerTestCase<UpdateTaskRequest, UpdateTaskController> {
    
    private Session hs;
    private SampleUsers sampleUsers;
    private SampleCategories sampleCategories;
    private SampleCourses sampleCourses;
    
    private Course testCourse;
    private Category testCategory;
    private Task testTask;
    
    @Override
    protected TitoBaseRole getModelAccessCheckerRole() {
        return TitoBaseRole.ADMINISTRATOR;
    }
    
    @Before
    public void setUp() {
        this.hs = this.openAutoclosedSession();
        
        this.sampleUsers = new SampleUsers(this.hs);
        
        this.sampleCategories = new SampleCategories(this.hs);
        this.sampleCourses = this.sampleCategories.courses;
        
        this.testCourse = this.sampleCourses.emptyCourse;
        this.testCategory = new Category();
        this.testCategory.setName(Locale.ENGLISH, "Empty category");
        this.testCategory.setCourse(this.testCourse);
        this.testCourse.getCategories().add(this.testCategory);
        this.hs.save(this.testCategory);
        this.hs.update(this.testCourse);
        
        this.testTask = new Task(this.testCategory, sampleUsers.admin, this.testCourse);
        this.testTask.setTitle(new TString());
        this.testTask.setDescription(new TString());
        this.testCourse.getTasks().add(this.testTask);
        this.testCategory.getTasks().add(this.testTask);
        this.hs.save(this.testTask);
        this.hs.update(this.testCourse);
        this.hs.update(this.testCategory);
        
        this.hs.flush();
        
        this.createRequest(this.sampleUsers.editor);
        
        request.taskId = this.testTask.getId();
        request.categoryId = this.testCategory.getId();
        request.title = new HashMap<String, String>();
        request.title.put("en", "Test task");
        request.title.put("fi", "Testitehtävä");
        request.description = new HashMap<String, String>();
        request.description.put("en", "This is a test task");
        request.hidden = false;
        request.difficulty = 123;
        request.maxSteps = 10000;
        request.type = Task.Type.PROGRAMMING.toString();
        request.preCode = null;
        request.postCode = "SVC SP,=HALT";
        
        request.modelSolution = "SVC SP,=HALT";
        
        
        request.input = new HashMap<String, String>();
        request.inputSecret = new HashMap<String, String>();
        
        
        request.criterionType = new HashMap<String, String>();
        
        request.acceptMsg = new HashMap<String, Map<String, String>>();
        request.rejectMsg = new HashMap<String, Map<String, String>>();
        
        request.inputId = new HashMap<String, String>();
        
        request.leftParam = new HashMap<String, String>();
        request.relation = new HashMap<String, String>();
        request.rightParam = new HashMap<String, String>();
    }
    
    private long addTestAnswerAndStuff() {
        Input input = new Input(testTask, "", false);
        testTask.getInputs().add(input);
        hs.save(input);
        
        Criterion crit = new RequiredInstructionsCriterion("SVC");
        crit.setTask(testTask);
        testTask.getCriteria().add(crit);
        hs.save(crit);
        
        Answer answer = new Answer(sampleUsers.pullman, testTask, "foo", true);
        testTask.getAnswers().add(answer);
        hs.save(answer);
        
        Validation val = new Validation();
        val.setAnswer(answer);
        val.setCriterion(crit);
        val.setInput(input);
        val.setSatisfied(true);
        answer.getValidations().add(val);
        hs.save(val);
        
        ExecStatus es = new ExecStatus(answer, input, ExitStatus.SUCCESSFUL);
        answer.getExecStatuses().add(es);
        
        hs.update(testTask);
        hs.flush();
        hs.evict(answer);
        
        return answer.getId();
    }
    
    @Test
    public void shouldDeleteAllValidationsAndExecStatusesButNotAnswersOfWhenDoingUnsafeUpdateTask() throws Exception {
        long answerId = addTestAnswerAndStuff();
        
        Response resp = controller.handle(request);
        assertThat(resp, instanceOf(RedirectResponse.class));
        
        Answer answer = (Answer)hs.get(Answer.class, answerId);
        assertNotNull(answer);
        assertFalse(answer.isSuccessful());
        assertTrue(answer.isObsoleted());
        assertTrue(answer.getValidations().isEmpty());
        assertTrue(answer.getExecStatuses().isEmpty());
    }
    
    @Test
    public void shouldNotAffectUserAnswersWhenDoingSafeUpdate() throws Exception {
        long answerId = addTestAnswerAndStuff();
        
        request.safeUpdate = true;
        
        Response resp = controller.handle(request);
        assertThat(resp, instanceOf(RedirectResponse.class));
        
        Answer answer = (Answer)hs.get(Answer.class, answerId);
        assertNotNull(answer);
        assertTrue(answer.isSuccessful());
        assertFalse(answer.isObsoleted());
        assertFalse(answer.getValidations().isEmpty());
        assertFalse(answer.getExecStatuses().isEmpty());
    }
    
    @Test
    public void shouldCreateEmptyInputIfNoInputsGiven() throws Exception {
        Response resp = controller.handle(request);
        assertThat(resp, instanceOf(RedirectResponse.class));
        
        hs.refresh(testTask);
        assertEquals(1, testTask.getInputs().size());
        assertEquals("", testTask.getInputs().iterator().next().getInput());
    }
    
    @Override
    protected Class<UpdateTaskController> getControllerType() {
        return UpdateTaskController.class;
    }
    
}
