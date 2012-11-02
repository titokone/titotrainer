package fi.helsinki.cs.titotrainer.testsupport.framework.misc;

import static org.junit.Assert.*;
import fi.helsinki.cs.titotrainer.framework.misc.Maybe;
import fi.helsinki.cs.titotrainer.framework.misc.None;
import fi.helsinki.cs.titotrainer.framework.misc.Some;

/**
 * Extra assertions for our internal use.
 */
public class Assert {

	/**
	 * Asserts that the {@link Maybe} object is a {@link None} object.
	 * @param maybe The {@link Maybe} object.
	 */
	public static void assertNone(Maybe<?> maybe) {
		assertFalse(maybe.hasValue());
	}
	
	/**
	 * Asserts that the {@link Maybe} object is a {@link Some} object.
	 * @param maybe The {@link Maybe} object.
	 */
	public static void assertSome(Maybe<?> maybe) {
		assertTrue(maybe.hasValue());
	}
	
	/**
	 * Asserts that the {@link Maybe} object is a {@link Some} object with a specific value.
	 * @param expected The expected value.
	 * @param maybe The {@link Maybe} object.
	 */
	public static void assertSome(Object expected, Maybe<?> maybe) {
		assertTrue(maybe.hasValue());
		assertEquals(expected, maybe.getValue());
	}
	
	protected Assert() {
		// Not directly instantiatable.
	}
}
