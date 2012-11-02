package fi.helsinki.cs.titotrainer.app.student.controller;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import fi.helsinki.cs.titotrainer.app.TitoRequestContext;
import fi.helsinki.cs.titotrainer.app.model.Answer;
import fi.helsinki.cs.titotrainer.app.model.Criterion;
import fi.helsinki.cs.titotrainer.app.model.Task;
import fi.helsinki.cs.titotrainer.app.model.access.CurrentCredentials;
import fi.helsinki.cs.titotrainer.app.model.titokone.StaticSynchronizedTitokoneFacade;
import fi.helsinki.cs.titotrainer.app.model.titokone.TitokoneState;
import fi.helsinki.cs.titotrainer.app.model.titokone.TitokoneState.ExitStatus;
import fi.helsinki.cs.titotrainer.app.session.TitoUserSession;
import fi.helsinki.cs.titotrainer.app.student.request.TaskExecutionRequest;
import fi.helsinki.cs.titotrainer.framework.RequestContext;
import fi.helsinki.cs.titotrainer.framework.response.RedirectResponse;
import fi.helsinki.cs.titotrainer.framework.response.Response;
import fi.helsinki.cs.titotrainer.testsupport.app.controller.ControllerTestCase;
import fi.helsinki.cs.titotrainer.testsupport.app.model.sample.SampleTasks;
import fi.helsinki.cs.titotrainer.testsupport.app.model.sample.SampleUsers;


public class TaskControllerTest extends ControllerTestCase<TaskExecutionRequest, TaskController> {

    private static final String SUM_PROGRAM_CODE = StringUtils.join(new String[] {
        "IN R1,=KBD",
        "IN R2,=KBD",
        "ADD R1,R2",
        "OUT R1,=CRT",
        "SVC SP,=HALT"
        }, '\n');
    
    private Session hs;
    private SampleTasks tasks;
    private SampleUsers users;
    private Task sumTask;
    private Long[] sumTaskCriteriaIds;
    
    @Override
    protected RequestContext createRequestContext() {
        TitoRequestContext rc = (TitoRequestContext)super.createRequestContext();
        Mockito.doReturn(new StaticSynchronizedTitokoneFacade()).when(rc).getTitokoneFacade();
        return rc;
    }
    
    private Answer getCreatedAnswer() {
        return (Answer)hs.createQuery("FROM Answer ORDER BY id DESC LIMIT 1").uniqueResult();
    }
    
    @Before
    public void setUp() {
        this.hs = this.openAutoclosedSession();
        this.tasks = new SampleTasks(this.hs);
        this.users = this.tasks.users;
        this.sumTask = this.tasks.sumTask;
        
        {
            sumTaskCriteriaIds = new Long[this.sumTask.getCriteria().size()];
            int i = 0;
            for (Criterion c : this.sumTask.getCriteria()) {
                this.sumTaskCriteriaIds[i++] = c.getId();
            }
        }
        
        this.createRequest();
        
        request.taskId = sumTask.getId();
        request.userInput = ArrayUtils.toObject(new int[] {-3, 10});
        
        Mockito.doReturn(new TitoUserSession()).when(request.getAttribs()).getUserSession();
        request.getAttribs().getUserSession().setAuthenticatedUser(this.users.nykanen);
        CurrentCredentials.setCurrentUser(this.users.nykanen);
    }
    
    @Override
    protected Class<TaskController> getControllerType() {
        return TaskController.class;
    }
    
    @Test
    public void shouldExecuteValidTitotrainerProgram() throws Exception {
        request.code = SUM_PROGRAM_CODE;
        
        Response resp = controller.handle(request);
        assertThat(resp, instanceOf(RedirectResponse.class));
        
        TitoUserSession us = request.getAttribs().getUserSession();
        assertArrayEquals(ArrayUtils.toPrimitive(request.userInput), (int[])us.getAttribute("customInput"));
        assertThat(us.getAttribute("customInputTitokoneState"), instanceOf(TitokoneState.class));
        for (Object id : (Object[])us.getAttribute("customInputSatisfiedCriteria"))
            assertThat((Long)id, isIn(sumTaskCriteriaIds));
    }
    
    @Test
    public void shouldSetCustomInputCompilationErrorOnCompilationError() throws Exception {
        request.code = "OOPS";
        Response resp = controller.handle(request);
        assertThat(resp, instanceOf(RedirectResponse.class));
        
        TitoUserSession us = request.getAttribs().getUserSession();
        assertThat(us.getAttribute("customInputCompilationError"), instanceOf(String.class));
    }

    @Test
    public void shouldStoreExecStatuses() throws Exception {
        request.code = "DIV R0,=0";
        Response resp = controller.handle(request);
        assertThat(resp, instanceOf(RedirectResponse.class));
        
        Answer answer = getCreatedAnswer();
        assertEquals(ExitStatus.DIVISION_BY_ZERO, answer.getExecStatuses().iterator().next().getExitStatus());
    }
    
    @Test
    @Ignore // Doesn't work with test DB for some reason :/
    public void shouldOnlyStoreLatestAnswer() throws Exception {
        request.code = SUM_PROGRAM_CODE;
        
        Response resp;
        resp = controller.handle(request);
        assertThat(resp, instanceOf(RedirectResponse.class));
        hs.flush();
        resp = controller.handle(request);
        assertThat(resp, instanceOf(RedirectResponse.class));
    }
    
    //TODO: test more
    
}
