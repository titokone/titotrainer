package fi.helsinki.cs.titotrainer.framework.request.coercer;

import static fi.helsinki.cs.titotrainer.testsupport.framework.misc.Assert.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;


public class IntegerCoercerTest {

	private IntegerCoercer ic;
	
	@Before
	public void setUp() {
		ic = new IntegerCoercer();
	}
	
	@Test
	public void resultTypeShouldBeInteger() {
	    assertSame(Integer.class, ic.getResultType());
	}
	
	@Test
	public void shouldNotMatchEmptyString() {
		assertNone(ic.coerce(""));
	}
	
	@Test
	public void shouldMatchAllIntegers() {
	    assertSome(Integer.MAX_VALUE, ic.coerce("" + Integer.MAX_VALUE));
        assertSome(new Integer(0), ic.coerce("0"));
        assertSome(Integer.MIN_VALUE, ic.coerce("" + Integer.MIN_VALUE));
	}
	
	@Test
	public void shouldNotAcceptAlphaCharacters() {
		assertNone(ic.coerce("h1"));
		assertNone(ic.coerce("1h"));
	}
}
