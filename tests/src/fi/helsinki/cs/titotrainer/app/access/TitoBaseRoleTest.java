package fi.helsinki.cs.titotrainer.app.access;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Test;

public class TitoBaseRoleTest {
    
    /**
     * A TitoBaseRole with the same identity (not merely
     * equality) should be returned when it is unserialized.
     * 
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @Test
    public void shouldRetainIdentityAfterSerialization() throws IOException, ClassNotFoundException {
        TitoBaseRole original = TitoBaseRole.EDITOR;
        
        ByteArrayOutputStream storage = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(storage);
        oos.writeObject(original);
        oos.close();
        
        InputStream is = new ByteArrayInputStream(storage.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(is);
        TitoBaseRole unserialized = (TitoBaseRole)ois.readObject();
        
        assertSame(original, unserialized);
    }
    
}
