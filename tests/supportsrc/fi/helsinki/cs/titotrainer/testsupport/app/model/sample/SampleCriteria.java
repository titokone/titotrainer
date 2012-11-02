package fi.helsinki.cs.titotrainer.testsupport.app.model.sample;

import java.util.Locale;

import org.hibernate.Session;

import fi.helsinki.cs.titotrainer.app.model.Criterion;
import fi.helsinki.cs.titotrainer.app.model.Task;
import fi.helsinki.cs.titotrainer.testsupport.app.model.SimpleCriterion;

public class SampleCriteria {

    /////////////////
    // SAMPLE DATA //
    /////////////////
    
    public static final String POINTLESS_CRITERION_ACCEPT_MESSAGE_EN = "Accepted!";
    public static final String POINTLESS_CRITERION_PARAMETERS        = "I have no idea, what 'data' is...";
    public static final String POINTLESS_CRITERION_REJECT_MESSAGE_EN = "Rejected!";
    
    public final Criterion pointlessCriterion;
    
    public SampleTasks tasks;

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public SampleCriteria(Session session, SampleTasks tasks) {
        this.tasks = tasks;
        session.save(this.pointlessCriterion = SampleCriteria.createPointlessCriterion(this.tasks.standardTask));
        session.flush();
    }

    /////////////////////
    // FACTORY METHODS //
    /////////////////////    
    
    public static Criterion createPointlessCriterion(Task task) {
        Criterion criterion;
        criterion = new SimpleCriterion();
        criterion.setTask(task);
        criterion.setAcceptMessage(Locale.ENGLISH, SampleCriteria.POINTLESS_CRITERION_ACCEPT_MESSAGE_EN);
        criterion.setRejectMessage(Locale.ENGLISH, SampleCriteria.POINTLESS_CRITERION_REJECT_MESSAGE_EN);
        criterion.setParameters(SampleCriteria.POINTLESS_CRITERION_PARAMETERS);
        task.getCriteria().add(criterion);
        return criterion;
    }
    
}