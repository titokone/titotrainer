package fi.helsinki.cs.titotrainer.framework.misc;

/**
 * The case of {@link Maybe} that contains no value.
 */
public final class None<T> implements Maybe<T> {
	@Override
	public boolean hasValue() {
		return false;
	}
	
	@Override
	public T getValue() {
	    throw new ClassCastException("getValue() invoked on a None object.");
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof None);
	}
	
	@Override
	public int hashCode() {
		return 0;
	}
}
