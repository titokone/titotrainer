package fi.helsinki.cs.titotrainer.app.admin.controller;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;

import fi.helsinki.cs.titotrainer.app.access.TitoBaseRole;
import fi.helsinki.cs.titotrainer.app.admin.request.DeleteUserRequest;
import fi.helsinki.cs.titotrainer.app.model.Answer;
import fi.helsinki.cs.titotrainer.app.model.User;
import fi.helsinki.cs.titotrainer.framework.response.RedirectResponse;
import fi.helsinki.cs.titotrainer.framework.response.Response;
import fi.helsinki.cs.titotrainer.testsupport.app.controller.ControllerTestCase;
import fi.helsinki.cs.titotrainer.testsupport.app.model.sample.SampleAnswers;
import fi.helsinki.cs.titotrainer.testsupport.app.model.sample.SampleTasks;
import fi.helsinki.cs.titotrainer.testsupport.app.model.sample.SampleUsers;

public class DeleteUserControllerTest extends ControllerTestCase<DeleteUserRequest, DeleteUserController> {
    
    private Session hs;
    private SampleUsers users;
    private SampleTasks tasks;
    private SampleAnswers answers;
    
    @Before
    public void setUp() {
        this.hs = this.openAutoclosedSession();
        this.tasks = new SampleTasks(hs);
        this.answers = new SampleAnswers(hs, tasks);
        this.users = tasks.users;
    }
    
    @Override
    protected TitoBaseRole getModelAccessCheckerRole() {
        return TitoBaseRole.ADMINISTRATOR;
    }
    
    private Response callOnUser(long userId) throws Exception {
        DeleteUserRequest req = this.createRequest();
        req.id = userId;
        return this.controller.handle(req);
    }
    
    @Test
    public void shouldSetCreatorAttributeOfAssociatedTasksToNull() throws Exception {
        User creator = tasks.standardTask.getCreator();
        
        callOnUser(creator.getId());
        
        hs.refresh(tasks.standardTask);
        
        assertNull(tasks.standardTask.getCreator());
    }
    
    @Test
    public void shouldDeleteAllOfTheUsersAnswers() throws Exception {
        long answerId = answers.commentOnlyAnswer.getId();
        User user = answers.commentOnlyAnswer.getUser();
        
        hs.evict(answers.commentOnlyAnswer);
        callOnUser(user.getId());
        
        assertNull(hs.get(Answer.class, answerId));
    }
    
    @Test
    public void shouldForwardToLogoutControllerOnSelfDelete() throws Exception {
        DeleteUserRequest req = this.createRequest(users.admin);
        req.id = users.admin.getId();
        Response resp = this.controller.handle(req);
        
        assertThat(resp, instanceOf(RedirectResponse.class));
        assertThat(resp, hasProperty("path", equalTo("/dologout")));
    }
    
    @Override
    protected Class<DeleteUserController> getControllerType() {
        return DeleteUserController.class;
    }
}
