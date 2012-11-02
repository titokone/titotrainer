package fi.helsinki.cs.titotrainer.app.model;

import static fi.helsinki.cs.titotrainer.app.model.misc.ArgumentUtils.*;

import java.lang.reflect.Modifier;
import java.util.Locale;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;

/**
 * <p>Information specific to a criterion type.</p>
 * 
 * <p>A valid database must have one of these for each criterion type.</p>
 */
@Entity
@Table(name = "criterionType")
public class CriterionType {
    
    private String className;
    private TString defaultAcceptMessage;
    private TString defaultRejectMessage;
    
    public CriterionType() {
        this.defaultAcceptMessage = new TString();
        this.defaultRejectMessage = new TString();
    }
    
    /**
     * Returns the class name of the criterion, which also acts as a primary key.
     */
    @Id
    @NotNull
    @NotEmpty
    public String getClassName() {
        return className;
    }
    
    public void setClassName(String className) {
        notNull(className);
        this.className = className;
    }
    
    /**
     * Returns the actual criterion class.
     */
    @Transient
    @SuppressWarnings("unchecked")
    public Class<? extends Criterion> getCriterionClass() {
        try {
            Class<?> cls = Class.forName(className);
            assert(Criterion.class.isAssignableFrom(cls));
            assert(!Modifier.isAbstract(cls.getModifiers()));
            return (Class<? extends Criterion>)cls;
        } catch (ClassNotFoundException e) {
            throw new AssertionError(className);
        }
    }
    
    @Cascade( { CascadeType.ALL } )
    @JoinColumn(name = "defaultAcceptMessageId")
    @NotNull
    @OneToOne
    public TString getDefaultAcceptMessage() {
        return defaultAcceptMessage;
    }
    
    public String getDefaultAcceptMessage(Locale locale) {
        return defaultAcceptMessage.get(locale);
    }
    
    public void setDefaultAcceptMessage(TString defaultAcceptMessage) {
        notNull(defaultAcceptMessage);
        this.defaultAcceptMessage = defaultAcceptMessage;
    }
    
    @Cascade( { CascadeType.ALL } )
    @JoinColumn(name = "defaultRejectMessageId")
    @NotNull
    @OneToOne
    public TString getDefaultRejectMessage() {
        return defaultRejectMessage;
    }
    
    public String getDefaultRejectMessage(Locale locale) {
        return defaultRejectMessage.get(locale);
    }
    
    public void setDefaultRejectMessage(TString defaultRejectMessage) {
        notNull(defaultRejectMessage);
        this.defaultRejectMessage = defaultRejectMessage;
    }
    
}
