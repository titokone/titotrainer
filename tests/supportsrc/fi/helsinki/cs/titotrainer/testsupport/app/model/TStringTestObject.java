package fi.helsinki.cs.titotrainer.testsupport.app.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import fi.helsinki.cs.titotrainer.app.model.TString;

@Entity
public class TStringTestObject {
    
    private Long id;

    private TString title;
    private TString msg;
    
    @Id
    @GeneratedValue
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    @OneToOne
    @Cascade( { CascadeType.ALL } )
    public TString getTitle() {
        return title;
    }
    
    public void setTitle(TString title) {
        this.title = title;
    }
    
    @OneToOne
    @Cascade( { CascadeType.ALL } )
    public TString getMsg() {
        return msg;
    }
    
    public void setMsg(TString msg) {
        this.msg = msg;
    }
    
}
