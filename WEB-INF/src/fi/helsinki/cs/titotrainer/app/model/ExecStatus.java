package fi.helsinki.cs.titotrainer.app.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.validator.NotNull;

import fi.helsinki.cs.titotrainer.app.model.titokone.TitokoneState.ExitStatus;
import fi.helsinki.cs.titotrainer.framework.model.Bidirectional;

/**
 * <p>The execution status of an answer on an input.</p>
 */
@Entity
@Table(name = "execStatus")
public class ExecStatus extends AbstractTitoEntity {
    
    ////////////////////
    // PRIVATE FIELDS //
    ////////////////////
    
    private Answer answer;
    private ExitStatus exitStatus;
    private long id;
    private Input input;
    
    //////////////////
    // CONSTRUCTORS //
    //////////////////
    
    public ExecStatus() {
    }
    
    public ExecStatus(Answer answer, Input input, ExitStatus exitStatus) {
        this.answer = answer;
        this.input = input;
        this.exitStatus = exitStatus;
    }
    
    ///////////////
    // ACCESSORS //
    ///////////////
    
    @Bidirectional
    @JoinColumn(name = "answerId")
    @ManyToOne
    @NotNull
    public Answer getAnswer() {
        return answer;
    }
    
    public void setAnswer(Answer answer) {
        this.answer = answer;
    }
    
    @Id
    @GeneratedValue
    @Override
    public long getId() {
        return id;
    }
    
    @Override
    public void setId(long id) {
        this.id = id;
    }
    
    @NotNull
    public ExitStatus getExitStatus() {
        return exitStatus;
    }
    
    public void setExitStatus(ExitStatus exitStatus) {
        this.exitStatus = exitStatus;
    }
    
    @Bidirectional
    @JoinColumn(name = "inputId")
    @ManyToOne
    @NotNull
    public Input getInput() {
        return input;
    }
    
    public void setInput(Input input) {
        this.input = input;
    }
}
