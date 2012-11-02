package fi.helsinki.cs.titotrainer.app.view.template;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

import org.apache.commons.collections.iterators.ArrayIterator;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import fi.helsinki.cs.titotrainer.app.view.template.velocity.event.OverridableEscapeHtmlReference;


/**
 * <p>Provides utility methods for a template.</p>
 * 
 * <p>This is a stateless singleton.</p>
 */
public final class TemplateUtils {
    
    private static TemplateUtils instance = new TemplateUtils();
    
    public static TemplateUtils getInstance() {
        return instance;
    }
    
    /**
     * Provides printf-style formatting using {@link Formatter}.
     * 
     * @param format The format string.
     * @param args The arguments for the format string.
     * @return The formatted string.
     */
    public String format(String format, Object... args) {
        return new Formatter().format(format, args).out().toString();
    }
    
    /**
     * <p>Escapes an HTML string. NOTE: you usually don't need this.</p>
     * 
     * <p>In TitoTrainer, all strings in a template are HTML-escaped by default.
     * See {@link OverridableEscapeHtmlReference} and {@link #noEscape(Object)}.
     * This method is useful in some situations where you want to combine
     * an escaped and non-escaped part in a template.</p>
     * 
     * <p>If the parameter is a {@link NoEscapeWrapper} then the
     * no escaping is done.</p>
     * 
     * @param s A object whose toString() representation to escape. May be null.
     * @return The escaped string. Null if {@code s} is null.
     */
    public String escapeHtml(Object s) {
        if (s == null)
            return null;
        if (s instanceof NoEscapeWrapper) {
            return s.toString();
        }
        return StringEscapeUtils.escapeHtml(s.toString());
    }
    
    /**
     * <p>Returns a string quoted and escaped for Javascript.</p>
     * 
     * <p>{@link NoEscapeWrapper} does not affect this method.</p>
     * 
     * @param s A object whose toString() representation to escape. May be null. 
     * @return The quoted Javascript string. Null if {@code s} is null.
     */
    public NoEscapeWrapper quoteJavascript(Object s) {
        if (s == null)
            return null;
        return new NoEscapeWrapper("\"" + StringEscapeUtils.escapeJavaScript(s.toString()) + "\"");
    }
    
    /**
     * Converts null into an empty string.
     * 
     * @param o Any object, or null.
     * @return An empty string if <code>o</code> is null, otherwise o.
     */
    public Object maybe(Object o) {
        if (o == null)
            return "";
        else
            return o;
    }
    
    /**
     * Returns the first non-null argument.
     * 
     * Returns null iff all arguments are null.
     * 
     * @param args The arguments.
     * @return The first non-null argument, or null if all arguments are null.
     */
    public Object coalesce(Object ... args) {
        for (Object arg : args)
            if (arg != null)
                return arg;
        return null;
    }
    
    /**
     * <p>Does equality comparison where either value may be null.</p>
     * 
     * @see ObjectUtils#equals(Object, Object)
     */
    public boolean equals(Object a, Object b) {
        return ObjectUtils.equals(a, b);
    }
    
    /**
     * Makes an HTML anchor tag with the specified href and contents.
     * 
     * @param href The target of the link. Defaults to <code>"#"</code> if null.
     *             This is automatically HTML-escaped by this method unless wrapped in a {@link NoEscapeWrapper}.
     * @param title The text inside the link. Not null.
     *             This is automatically HTML-escaped by this method unless wrapped in a {@link NoEscapeWrapper}.
     */
    public NoEscapeWrapper makeLink(String href, String title) {
        return makeLink(href, title, null);
    }
    
    /**
     * Makes an HTML anchor tag with the specified href, contents ands HTML class.
     * 
     * @param href The target of the link. Defaults to <code>"#"</code> if null.
     *             This is automatically HTML-escaped by this method unless wrapped in a {@link NoEscapeWrapper}.
     * @param title The text inside the link.
     *              This is automatically HTML-escaped by this method unless wrapped in a {@link NoEscapeWrapper}.
     * @param cls The HTML class(es). May be null.
     *            This is automatically HTML-escaped by this method unless wrapped in a {@link NoEscapeWrapper}.
     * @return A span tag wrapped in a NoEscapeWrapper.
     */
    public NoEscapeWrapper makeLink(String href, String title, String cls) {
        String begin = "<a href=\"" + escapeHtml(href) + "\"";
        if (cls != null)
            begin += " class=\"" + escapeHtml(cls) + "\"";
        begin += ">";
        
        return this.noEscape(begin + escapeHtml(title) + "</a>");
    }
    
    /**
     * Wraps content in a <code>&lt;span&gt;</code> tag.
     * 
     * @param content The text inside the link.
     *                This is automatically HTML-escaped by this method unless wrapped in a {@link NoEscapeWrapper}.
     * @param cls The HTML class(es). May be null.
     *            This is automatically HTML-escaped by this method unless wrapped in a {@link NoEscapeWrapper}.
     * @return A span tag wrapped in a NoEscapeWrapper.
     */
    public NoEscapeWrapper makeSpan(String content, String cls) {
        if (cls != null)
            return this.noEscape("<span class=\"" + escapeHtml(cls) + "\">" + escapeHtml(content) + "</span>");
        else
            return this.noEscape("<span>" + escapeHtml(content) + "</span>");
    }
    
    /**
     * <p>Renders text given by the user as HTML with the appropriate tags.</p>
     * 
     * <p>For now this simply quotes the content and translates newlines into <br/>s.</p>
     */
    public Object renderUserFormatedText(String content) {
        content = escapeHtml(content);
        content = StringUtils.replace(content, "\n", "<br/>\n");
        return noEscape(content);
    }
    
    /**
     * <p>Writes Javascript to disable all fields in a web form and remove any submit buttons it has.</p>
     * 
     * <p>This is implemented here because it is needed so often that a shorthand is useful.</p>
     * 
     * @param jQueryForm A jQuery that returns the form.
     * @return
     */
    public NoEscapeWrapper disableForm(String jQueryForm) {
        String q = quoteJavascript(jQueryForm).toString();
        return noEscape(
            "<script type=\"text/javascript\">\n" +
            "$(document).ready(function() {\n" +
            "\tformUtils.disableForm($(" + q + ")[0]);" +
            "});\n" +
            "</script>"
            );
    }
    
    /**
     * Concatenates the toString() values of all objects.
     * This is sometimes useful when the template language's
     * concatenation mechanism is inconvenient.
     * 
     * @param objs The objects to concatenate. Nulls are ignored.
     * @return The toString() values of the objects to concatenate.
     */
    public String concat(Object ... objs) {
        return StringUtils.join(objs);
    }
    
    /**
     * Converts a collection of objects to strings and joins them using a given separator.
     */
    public <T> String join(Collection<T> a, String sep) {
        return StringUtils.join(a, sep);
    }
    
    /**
     * Converts an array of objects to strings and joins them using a given separator.
     */
    public <T> String join(T[] a, String sep) {
        return StringUtils.join(a, sep);
    }
    
    /**
     * Converts an array of integers to strings and joins them using a given separator.
     */
    public String join(int[] a, String sep) {
        return StringUtils.join(new ArrayIterator(a), sep);
    }
    
    /**
     * Returns true iff the object is null or has an isEmpty() method that returns true.
     */
    public boolean isEmpty(Object obj) {
        if (obj == null)
            return true;
        try {
            Method isEmpty = obj.getClass().getMethod("isEmpty");
            if (isEmpty.invoke(obj).equals(Boolean.TRUE))
                return true;
        } catch (Exception e) {
        }
        return false;
    }
    
    public String formatShortDateTime(Date date, Locale locale) {
        //DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, locale);
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        return df.format(date);
    }
    
    /**
     * Puts an object in a no-escape wrapper so that
     * {@link OverridableEscapeHtmlReference} and {@link #escapeHtml(Object)}
     * do not escape it.
     * 
     * @param obj The object to wrap.
     * @return A {@link NoEscapeWrapper} of the object.
     */
    public NoEscapeWrapper noEscape(Object obj) {
        return new NoEscapeWrapper(obj);
    }
    
    private TemplateUtils() {
        // Singleton
    }
}
