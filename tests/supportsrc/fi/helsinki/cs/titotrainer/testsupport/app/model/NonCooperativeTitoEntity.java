package fi.helsinki.cs.titotrainer.testsupport.app.model;

public class NonCooperativeTitoEntity extends SimpleTitoEntity {

    public long getId() {
        throw new UnsupportedOperationException();
    }

    public void setId(long id) {
        throw new UnsupportedOperationException();
    }    
    
}