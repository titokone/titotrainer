package fi.helsinki.cs.titotrainer.app.view.template;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;


public class TemplateUtilsTest {
    
    private TemplateUtils utils;
    
    @Before
    public void setUp() {
        this.utils = TemplateUtils.getInstance();
    }
    
    @Test
    public void equalsMethodShouldWorkWithNulls() {
        String a = "foo";
        String b = new String(a);
        assertTrue(utils.equals(null, null));
        assertTrue(utils.equals(a, b));
        assertFalse(utils.equals(null, a));
        assertFalse(utils.equals(a, null));
    }
    
    @Test
    public void coalesceMethodShouldReturnFirstNonNullArgument() {
        assertNotNull(utils.coalesce(null, null, 3));
        assertEquals(utils.coalesce(null, null, 3, 4), 3);
    }
    
    @Test
    public void coalesceMethodShouldReturnNullIfAllArgumentsAreNull() {
        assertNull(utils.coalesce());
        assertNull(utils.coalesce((Object)null));
        assertNull(utils.coalesce(null, null));
    }
    
    @Test
    public void formatMethodShouldInvokeFormatter() {
        assertEquals("abc 123", utils.format("%s %d", "abc", 123));
    }
    
    @Test
    public void escapeHtmlMethodShouldEscapeHtml() {
        assertEquals("&lt;foo&gt;", utils.escapeHtml("<foo>"));
    }
    
    @Test
    public void escapeHtmlMethodShouldPassThroughNull() {
        assertEquals(null, utils.escapeHtml(null));
    }
    
    @Test
    public void escapeHtmlMethodShouldNotEscapeObjectIfItIsWrappedInANoEscapeWrapper() {
        assertEquals("<foo>", utils.escapeHtml(utils.noEscape("<foo>")).toString());
    }
    
    @Test
    public void makeLinkMethodShouldMakeALink() {
        assertEquals("<a href=\"dest\">title</a>", utils.makeLink("dest", "title").toString());
    }
    
    @Test
    public void makeLinkMethodShouldReturnLinkInNoEscapeWrapper() {
        assertThat(utils.makeLink("dest", "title"), instanceOf(NoEscapeWrapper.class));
    }
    
    @Test
    public void makeLinkMethodShouldEscapeHref() {
        assertEquals("<a href=\"foo?a=b&amp;b=c\">title</a>", utils.makeLink("foo?a=b&b=c", "title").toString());
    }
    
    @Test
    public void makeLinkMethodShouldIncludeClassAndEscapeIt() {
        assertEquals("<a href=\"dest\" class=\"foo&amp;bar\">title</a>", utils.makeLink("dest", "title", "foo&bar").toString());
    }
    
    @Test
    public void makeLinkMethodShouldEscapeTitle() {
        assertEquals("<a href=\"dest\">a&amp;b</a>", utils.makeLink("dest", "a&b").toString());
    }
    
    @Test
    public void makeSpanShouldMakeASpanEntity() {
        assertEquals("<span>foo</span>", utils.makeSpan("foo", null).toString());
    }
    
    @Test
    public void makeSpanShouldEscapeTitleAndClass() {
        assertEquals("<span>f&amp;o</span>", utils.makeSpan("f&o", null).toString());
        assertEquals("<span class=\"a&amp;b\"></span>", utils.makeSpan("", "a&b").toString());
        assertEquals("<span class=\"a&amp;b\">f&amp;o</span>", utils.makeSpan("f&o", "a&b").toString());
    }
    
    @Test
    public void concatMethodShouldConcatenateToStringValuesOfObjects() {
        Object foo = new Object() {
            @Override
            public String toString() {
                return "foo";
            }
        };
        
        assertEquals("foobarbaz", utils.concat(foo, "bar", "baz"));
    }
    
    @Test
    public void concatMethodShouldTreatNullObjectsAsEmptyStrings() {
        assertEquals("foobar", utils.concat("foo", null, "bar"));
    }
    
    @Test
    public void formatShortDateTimeMethodShouldReturnDateInDDMMYYHHmmFormatForFinnishLocale() {
        Calendar cal = GregorianCalendar.getInstance();
        cal.set(2008, 0, 01, 01, 01);
        String result = utils.formatShortDateTime(cal.getTime(), new Locale("fi"));
        
        assertEquals("01.01.2008 01:01", result);
    }
    
    @Test
    public void noEscapeMethodShouldWrapObjectInNoEscapeWrapper() {
        Object obj, result;
        
        obj = new Object();
        result = utils.noEscape(obj);
        assertThat(result, instanceOf(NoEscapeWrapper.class));
        assertThat(result, hasProperty("object", equalTo(obj)));
        
        obj = null;
        result = utils.noEscape(obj);
        assertThat(result, instanceOf(NoEscapeWrapper.class));
        assertThat(result, hasProperty("object", equalTo(obj)));
    }
    
}
