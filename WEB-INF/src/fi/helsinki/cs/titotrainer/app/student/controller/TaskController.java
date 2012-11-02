package fi.helsinki.cs.titotrainer.app.student.controller;

import org.apache.commons.lang.ArrayUtils;
import org.hibernate.Session;

import fi.helsinki.cs.titotrainer.app.controller.TitoActionController;
import fi.helsinki.cs.titotrainer.app.model.Task;
import fi.helsinki.cs.titotrainer.app.session.Messenger;
import fi.helsinki.cs.titotrainer.app.student.model.AnswerChecker;
import fi.helsinki.cs.titotrainer.app.student.request.TaskExecutionRequest;
import fi.helsinki.cs.titotrainer.framework.i18n.Translator;
import fi.helsinki.cs.titotrainer.framework.response.ErrorResponse;
import fi.helsinki.cs.titotrainer.framework.response.RedirectResponse;
import fi.helsinki.cs.titotrainer.framework.response.Response;

public class TaskController extends TitoActionController<TaskExecutionRequest> {

    @Override
    protected Response handleValid(TaskExecutionRequest req, Session hs) throws Exception {
        Task task = (Task)hs.get(Task.class, req.taskId);
        if (task == null) {
            return new ErrorResponse(404);
        }

        int[] userInput;
        if (req.userInput != null) {
            userInput = ArrayUtils.toPrimitive(req.userInput);
        } else {
            userInput = new int[0];
        }
        
        AnswerChecker ac = new AnswerChecker();
        ac.setHibernateSession(hs);
        ac.setUserSession(req.getUserSession());
        ac.setTask(task);
        ac.setTitokoneFacade(req.getContext().getTitokoneFacade());
        ac.setCustomInput(userInput);
        ac.setCodeWrittenByUser(req.code);
        
        try {
            ac.checkAnswer();
        } catch (AnswerChecker.AnswerCheckException e) {
            return handleUnexpectedError(req, e.getCause(), e.getMessage());
        }
        
        return new RedirectResponse("/student/task?id=" + task.getId());
    }
    
    private Response handleUnexpectedError(TaskExecutionRequest req, Exception e, String techMsg) {
        Translator tr = this.getTranslator(req);
        req.getUserSession().getMessenger().appendMessage(Messenger.GLOBAL_ERROR_CATEGORY, tr.tr("problem_with_task_definition"));
        req.getUserSession().getMessenger().appendMessage(Messenger.GLOBAL_ERROR_CATEGORY, techMsg);
        logger.warn(techMsg, e);
        return new RedirectResponse("/student/task?id=" + req.taskId);
    }
    
    @Override
    public Class<TaskExecutionRequest> getRequestType() {
        return TaskExecutionRequest.class;
    }
    
}
