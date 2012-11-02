package fi.helsinki.cs.titotrainer.testsupport.app.model;

import fi.helsinki.cs.titotrainer.app.model.AbstractTitoEntity;

/**
 * The simplest possible implementation for AbstractTitoEntity. This class is used for testing purposes only.
 */
public class SimpleTitoEntity extends AbstractTitoEntity {
    
    private long id;
    
    public long getId() {
        return this.id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
}