package fi.helsinki.cs.titotrainer.framework.request.coercer;

import static fi.helsinki.cs.titotrainer.testsupport.framework.misc.Assert.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;


public class LongCoercerTest {

    private LongCoercer lc;
    
    @Before
    public void setUp() {
        lc = new LongCoercer();
    }
    
    @Test
    public void resultTypeShouldBeLong() {
        assertSame(Long.class, lc.getResultType());
    }
    
    @Test
    public void shouldNotMatchEmptyString() {
        assertNone(lc.coerce(""));
    }
    
    @Test
    public void shouldMatchAllLongs() {
        assertSome(Long.MAX_VALUE, lc.coerce("" + Long.MAX_VALUE));
        assertSome(new Long(0), lc.coerce("0"));
        assertSome(Long.MIN_VALUE, lc.coerce("" + Long.MIN_VALUE));
    }
    
    @Test
    public void shouldNotAcceptAlphaCharacters() {
        assertNone(lc.coerce("h1"));
        assertNone(lc.coerce("1h"));
    }
}
