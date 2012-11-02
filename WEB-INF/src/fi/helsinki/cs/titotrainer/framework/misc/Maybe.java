package fi.helsinki.cs.titotrainer.framework.misc;

/**
 * <p>The superclass of {@link Some} and {@link None}.</p>
 * 
 * <p>Used to wrap an object when it must be returned conditionally
 * but null is a valid return value.</p>
 */
public interface Maybe<T> {
	/**
	 * Returns true for {@link Some}, false for {@link None}.
	 * @return Whether this is an instance of {@link Some}.
	 */
	boolean hasValue();
	
	/**
	 * Returns the value of the object if it is a {@link Some} object.
	 * @return The value of this object (which may be null).
	 * @throws ClassCastException if this was a {@link None} object.
	 */
	T getValue();
	
	/**
	 * Two Maybe objects are equal if they are both {@link None}
	 * of if they are both {@link Some} and their values are equal.
	 * 
	 * @param obj Another {@link Maybe} object.
	 * @return Equality.
	 */
	@Override
	public boolean equals(Object obj);
	
	/**
	 * {@link None} shall return a hashCode of 0. {@link Some} shall
	 * return the hashCode of its value, or -1 if the value is null.
	 * 
	 * @return The hashCode of the Maybe object.
	 */
	@Override
	public int hashCode();
}
