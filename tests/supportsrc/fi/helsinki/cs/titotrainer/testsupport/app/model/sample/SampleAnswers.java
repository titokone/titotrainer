package fi.helsinki.cs.titotrainer.testsupport.app.model.sample;

import org.hibernate.Session;

import fi.helsinki.cs.titotrainer.app.model.Answer;
import fi.helsinki.cs.titotrainer.app.model.Task;
import fi.helsinki.cs.titotrainer.app.model.User;

public class SampleAnswers {
    
    
    /////////////////
    // SAMPLE DATA //
    /////////////////
    
    public static final String COMMENT_ONLY_ANSWER = "; This answer consists of two lines\n; of comments only...";
    
    public final Answer commentOnlyAnswer;
    
    public SampleInputs inputs;
    public SampleTasks  tasks;
    public SampleUsers  users;
    
    //////////////////
    // CONSTRUCTORS //
    //////////////////    

    public SampleAnswers(Session session, SampleTasks tasks) {
        this.tasks = tasks;
        this.inputs = new SampleInputs(session, this.tasks);
        this.users = this.inputs.users;
        session.save(this.commentOnlyAnswer = SampleAnswers.createAnswerComment(this.tasks.standardTask, this.users.pullman));
        session.flush();
    }

    /////////////////////
    // FACTORY METHODS //
    /////////////////////

    public static Answer createAnswerComment(Task task, User user) {
        Answer answer = new Answer(user, task, SampleAnswers.COMMENT_ONLY_ANSWER, true);
        task.getAnswers().add(answer);
        return answer;
    }

}