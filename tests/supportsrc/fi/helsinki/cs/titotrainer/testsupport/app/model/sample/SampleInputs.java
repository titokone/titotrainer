package fi.helsinki.cs.titotrainer.testsupport.app.model.sample;

import org.hibernate.Session;

import fi.helsinki.cs.titotrainer.app.model.Input;
import fi.helsinki.cs.titotrainer.app.model.Task;

public class SampleInputs {
    
    /////////////////
    // SAMPLE DATA //
    /////////////////
    
    public static final String STANDARD_INPUT = "1,2,3,4";    
    
    public SampleTasks tasks;
    public SampleUsers users;
    
    public final Input standardInput;
    
    //////////////////
    // CONSTRUCTORS //
    //////////////////
    
    public SampleInputs(Session session, SampleTasks tasks) {
        this.tasks = tasks;
        this.users = this.tasks.users;
        session.save(this.standardInput = SampleInputs.createInputStandard(this.tasks.standardTask));
        session.flush();
    }

    /////////////////////
    // FACTORY METHODS //
    /////////////////////
    
    public static Input createInputStandard(Task task) {
        Input input;
        input = new Input(task, SampleInputs.STANDARD_INPUT, false);
        task.getInputs().add(input);
        return input;
    }    

}