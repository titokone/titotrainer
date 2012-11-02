package fi.helsinki.cs.titotrainer.app.misc;

import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * An interface that specifies that the object formally
 * implements Serializable but doesn't actually serialize
 * itself - on reconstruction a new blank object is created.
 */
public interface FakeSerializable extends Serializable {
    public Object writeReplace() throws ObjectStreamException;
}
