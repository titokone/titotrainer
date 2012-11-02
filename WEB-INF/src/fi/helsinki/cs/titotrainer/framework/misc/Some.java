package fi.helsinki.cs.titotrainer.framework.misc;

/**
 * The case of {@link Maybe} that contains a value.
 */
public final class Some<T> implements Maybe<T> {
	
	/**
	 * The value refered to. May be null.
	 */
	public final T value;
	
	/**
	 * Constructor.
	 * @param value The desired value of the object. May be null.
	 */
	public Some(T value) {
		this.value = value;
	}
	
	@Override
	public boolean hasValue() {
		return true;
	}
	
	@Override
	public T getValue() {
	    return this.value;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Some) {
			if (this.value != null)
				return this.value.equals(((Some<?>)obj).value);
			else
				return ((Some<?>)obj).value == null;
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		if (this.value != null)
			return this.value.hashCode();
		else
			return -1;
	}
}
