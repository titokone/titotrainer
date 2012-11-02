package fi.helsinki.cs.titotrainer.testsupport.framework.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;

/**
 * A persistent entity class for testing basic database
 * connection functionality without using any
 * application-specific entity class.
 */
@Entity
@Table(name = "test_db_entity")
public class TestDbEntity {
    
    private String key;
    private String value;
    
    public TestDbEntity() {
    }
    
    public TestDbEntity(String key, String value) {
        this.key = key;
        this.value = value;
    }
    
    @Id
    @Column(nullable = false)
    public String getKey() {
        return key;
    }
    
    public void setKey(String key) {
        this.key = key;
    }
    
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof TestDbEntity))
            return false;
        return ObjectUtils.equals(this.getKey(), ((TestDbEntity)obj).getKey());
    }
    
    @Override
    public String toString() {
        return "TestDbEntity(" + key + " -> " + value + ")";
    }
}
