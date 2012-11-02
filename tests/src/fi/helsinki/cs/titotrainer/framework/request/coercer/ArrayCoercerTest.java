package fi.helsinki.cs.titotrainer.framework.request.coercer;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import fi.helsinki.cs.titotrainer.framework.misc.Maybe;

public class ArrayCoercerTest {
    
    private ArrayCoercer<Integer> integerArrayCoercer;
    private ArrayCoercer<String> stringArrayCoercer;
    
    @Before
    public void setUp() {
        this.integerArrayCoercer = new ArrayCoercer<Integer>(new IntegerCoercer());
        this.stringArrayCoercer = new ArrayCoercer<String>(new StringCoercer());
    }
    
    @Test
    public void resultTypeShouldBeArray() {
        assertSame(Integer[].class, this.integerArrayCoercer.getResultType());
    }
    
    @Test
    public void shouldParseCommaSeparatedArray() {
        String input = "1,5,-8,0,7";
        Integer[] expected = {1, 5, -8, 0, 7};
        Maybe<Integer[]> value = this.integerArrayCoercer.coerce(input);
        assertTrue(value.hasValue());
        assertArrayEquals(expected, value.getValue());
    }
    
    @Test
    public void shouldReturnNoneIfAnElementCouldNotBeCoerced() {
        String input = "1,a,3";
        Maybe<Integer[]> value = this.integerArrayCoercer.coerce(input);
        assertFalse(value.hasValue());
    }
    
    @Test
    public void shouldTrimWhitespaceBeforeElementCoercion() {
        String input = " a ,  b  ,c,d";
        String[] expected = {"a", "b", "c", "d"};
        Maybe<String[]> value = this.stringArrayCoercer.coerce(input);
        assertTrue(value.hasValue());
        assertArrayEquals(expected, value.getValue());
    }
}
