package fi.helsinki.cs.titotrainer.app.model.misc;

import static fi.helsinki.cs.titotrainer.app.model.misc.ArgumentUtils.*;
import static java.util.Locale.*;
import static org.junit.Assert.*;

import org.junit.Test;

import fi.helsinki.cs.titotrainer.app.model.TString;
import fi.helsinki.cs.titotrainer.testsupport.app.model.sample.SampleUsers;

public class ArgumentUtilsTest {

    /* Test ArgumentUtils() */
    
    @SuppressWarnings("deprecation")
    @Test(expected = UnsupportedOperationException.class)
    public void simpleConstructorShouldNotInstantiateArgumentUtils() {
        ArgumentUtils test = new ArgumentUtils();
        assertNull(test);
    }
    
    /* Test copyOrNull(TString) */
    
    @Test
    public void copyOrNullShouldReturnNullOnNullSource() {
        assertNull(copyOrNull(null));
    }
    
    @Test
    public void copyOrNullShouldReturnTrueCopyOnNonemptySource() {
        TString source = new TString(ENGLISH, "Message");
        TString copy = copyOrNull(source);
        assertNotSame(source, copy);
        assertNotSame(source.getTranslations(), copy.getTranslations());
        assertEquals(source.getTranslations(), copy.getTranslations());
        assertSame(source.get(ENGLISH), copy.get(ENGLISH));
    }
    
    /* Test validateCourseArgument(Course, String, int) */
    
    @Test
    public void validateCourseArgumentShouldNotThrowAnythingWhenNoChecksAreSpecified() {
        validateCourseArgument(null, "course", 0);
        validateCourseArgument(null, null, 0);
    }
    
    @Test(expected = NullPointerException.class)
    public void validateCourseArgumentShouldThrowNullPointerExceptionOnNullConstraint() {
        validateCourseArgument(null, "course", COURSE_CONSTRAINT_NOT_NULL);
    }

    /* Test validateLocaleArgument(String, String, int) */
    
    @Test
    public void validateLocaleArgumentShouldNotThrowAnyhingWhenNoChecksAreSpecified() {
        validateLocaleArgument(null, "locale", 0);
        validateLocaleArgument(null, null, 0);
    }
   
    @Test(expected = NullPointerException.class)
    public void validateLocaleArgumentShouldThrowNullPointerExceptionOnNullConstraint() {
        validateLocaleArgument(null, "locale", LOCALE_CONSTRAINT_NOT_NULL);
    }
    
    /* Test validateStringArgument(String, String, int) */
    
    @Test
    public void validateStringArgumentShouldNotThrowAnythingWhenNoChecksAreSpecified() {
        validateStringArgument(null, "value", 0);
        validateStringArgument(null, null, 0);
    }
    
    @Test
    public void validateStringArgumentShouldNotThrowAnythingForValidArgument() {
        ArgumentUtils.validateStringArgument("This is a valid argument!", "value", ArgumentUtils.STRING_CONSTRAINT_NOT_BLANK + ArgumentUtils.STRING_CONSTRAINT_NOT_EMPTY + ArgumentUtils.STRING_CONSTRAINT_NOT_NULL);
    }
    
    @Test(expected = NullPointerException.class)
    public void validateStringArgumentShouldThrowNullPointerExceptionA() {
        ArgumentUtils.validateStringArgument(null, "value", ArgumentUtils.STRING_CONSTRAINT_NOT_NULL);
    }
    
    @Test(expected = NullPointerException.class)
    public void validateStringArgumentShouldThrowNullPointerExceptionB() {
        ArgumentUtils.validateStringArgument(null, "value", ArgumentUtils.STRING_CONSTRAINT_NOT_EMPTY);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void validateStringArgumentShouldThrowIllegaldArgumentExceptionA() {
        ArgumentUtils.validateStringArgument("", "value", ArgumentUtils.STRING_CONSTRAINT_NOT_EMPTY);
    }
    
    @Test(expected = NullPointerException.class)
    public void validateStringArgumentShouldThrowNullPointerExceptionC() {
        validateStringArgument(null, "value", ArgumentUtils.STRING_CONSTRAINT_NOT_BLANK);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void validateStringArgumentShouldThrowIllegaldArgumentExceptionB() {
        validateStringArgument("", "value", ArgumentUtils.STRING_CONSTRAINT_NOT_BLANK);
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateStringArgumentShouldThrowIllegaldArgumentExceptionC() {
        validateStringArgument("\n  \t         \n  \n  \t ", "value", ArgumentUtils.STRING_CONSTRAINT_NOT_BLANK);
    }
    
    @Test
    public void validateStringArgumentShouldNotThrowNullPointerException() {
        validateStringArgument(null, "value", STRING_CONSTRAINT_NOT_WHITESPACE_ONLY);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void validateStringArgumentShouldThrowIllegalArgumentExceptionD() {
        validateStringArgument("\n     \n    \n       \t \n", "value", STRING_CONSTRAINT_NOT_WHITESPACE_ONLY);
    }
    
    /* Test validateUserArgument(User, String, int) */
    
    @Test
    public void validateUserArgumentShouldNotThrowAnythingWhenNoChecksAreSpecified() {
        validateUserArgument(null, null, 0);
        validateUserArgument(SampleUsers.createUserAdmin(), "user", 0);
        validateUserArgument(SampleUsers.createUserEditor(), "user", 0);
        validateUserArgument(SampleUsers.createUserAssistant(), "user", 0);
        validateUserArgument(SampleUsers.createUserPullman(), "user", 0);
    }
    
    @Test(expected = NullPointerException.class)
    public void validateUserArgumentShouldThrowNullPointerException() {
        validateUserArgument(null, "user", USER_CONSTRAINT_NOT_NULL);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void validateUserArgumentShouldThrowIllegalArgumentExceptionIfAdminUser() {
        validateUserArgument(SampleUsers.createUserAdmin(), "user", ArgumentUtils.USER_CONSTRAINT_NOT_ADMINISTRATOR);
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateUserArgumentShouldThrowIllegalArgumentExceptionIfEditorUser() {
        validateUserArgument(SampleUsers.createUserEditor(), "user", ArgumentUtils.USER_CONSTRAINT_NOT_EDITOR);
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateUserArgumentShouldThrowIllegalArgumentExceptionIfInstructorUser() {
        validateUserArgument(SampleUsers.createUserAssistant(), "user", ArgumentUtils.USER_CONSTRAINT_NOT_INSTRUCTOR);
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateUserArgumentShouldThrowIllegalArgumentExceptionIfStudentUser() {
        validateUserArgument(SampleUsers.createUserPullman(), "user", ArgumentUtils.USER_CONSTRAINT_NOT_STUDENT);
    }
    
}