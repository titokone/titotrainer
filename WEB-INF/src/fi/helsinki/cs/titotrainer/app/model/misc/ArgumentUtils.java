package fi.helsinki.cs.titotrainer.app.model.misc;

import java.util.Locale;

import fi.helsinki.cs.titotrainer.app.access.TitoBaseRole;
import fi.helsinki.cs.titotrainer.app.model.Course;
import fi.helsinki.cs.titotrainer.app.model.TString;
import fi.helsinki.cs.titotrainer.app.model.User;

/**
 * Auxiliary class providing static methods for argument validation. The static methods in this class can be
 * used to validate arguments to methods in the model layer against certain constraints. These constraints can
 * be set by using the symbolic constants in this class.
 */
public class ArgumentUtils {
    
    //////////////////////
    // PUBLIC CONSTANTS //
    //////////////////////
    
    /**
     * Disallow a {@link Course} argument to be {@code null}. If this flag is set, it causes the validation
     * method to throw a {@link NullPointerException} if an argument of type {@link Course} is {@code null}. 
     * @see #validateCourseArgument(Course, String, int)
     */
    public static int COURSE_CONSTRAINT_NOT_NULL = 1;
    
    /**
     * Disallow a {@link Locale} argument to be {@code null}. If this flag is set, it causes the validation
     * method to throw a {@link NullPointerException} if an argument of type {@link Locale} is {@code null}.
     * @see #validateLocaleArgument(Locale, String, int)
     */
    public static int LOCALE_CONSTRAINT_NOT_NULL = 1;
    
    /**
     * Disallow a {@link String} argument to be {@code null}. If this flag is set, it causes the validation
     * method to throw a {@link NullPointerException} if an argument of type {@link String} is {@code null}.
     * @see #validateStringArgument(String, String, int) 
     */
    public static int STRING_CONSTRAINT_NOT_NULL = 1;
    
    /**
     * Disallow empty {@link String} arguments. If this flag is set, it causes the validation method to throw
     * an {@link IllegalArgumentException} if an argument of type {@link String} is either {@code null} or
     * or the empty string {@code ""}.
     * @see #validateStringArgument(String, String, int)
     */
    public static int STRING_CONSTRAINT_NOT_EMPTY = 2;
    
    /**
     * Disallow blank {@link String} arguments. If this flag is set, it causes the validation method to throw
     * an {@link IllegalArgumentException} if an argument of type {@link String} is either {@code null} or
     * the empty string {@code ""} or contains only whitespace characters.
     * @see #validateStringArgument(String, String, int)
     * @see #STRING_CONSTRAINT_NOT_WHITESPACE_ONLY
     */
    public static int STRING_CONSTRAINT_NOT_BLANK = 4;
    
    /**
     * Disallow a {@link String} argument to consist of whitespace only. If this flag is set, it causes the
     * validation method to throw an {@link IllegalArgumentException} if an argument of type {@link String}
     * is either the empty string {@code ""} or contains only of whitespace characters. This flag should be
     * used in cases in which {@code null} is permitted.
     * @see #validateStringArgument(String, String, int)
     * @see #STRING_CONSTRAINT_NOT_BLANK
     */
    public static int STRING_CONSTRAINT_NOT_WHITESPACE_ONLY = 8;
        
    /**
     * Disallow a {@link User} argument to be {@code null}. If this flag is set, it causes the validation
     * method to throw a {@link NullPointerException} if an argument of type {@link User} is {@code null}. 
     * @see #validateUserArgument(User, String, int)
     */
    public static int USER_CONSTRAINT_NOT_NULL = 1;
    
    /**
     * Disallow a {@link User} argument to represent a student. If this flag is set, it causes the validation
     * method to throw a {@link IllegalArgumentException} if an argument of type {@link User} points to a user
     * record for a student.
     * @see ArgumentUtils#validateUserArgument(User, String, int)
     * @see #USER_CONSTRAINT_NOT_INSTRUCTOR
     * @see #USER_CONSTRAINT_NOT_EDITOR
     * @see #USER_CONSTRAINT_NOT_ADMINISTRATOR
     */
    public static int USER_CONSTRAINT_NOT_STUDENT = 256;

    /**
     * Disallow a {@link User} argument to represent an assistant. If this flag is set, it causes the validation
     * method to throw a {@link IllegalArgumentException} if an argument of type {@link User} points to a user
     * record for an assistant.
     * @see ArgumentUtils#validateUserArgument(User, String, int)
     * @see #USER_CONSTRAINT_NOT_STUDENT
     * @see #USER_CONSTRAINT_NOT_EDITOR
     * @see #USER_CONSTRAINT_NOT_ADMINISTRATOR
     */    
    public static int USER_CONSTRAINT_NOT_INSTRUCTOR = 512;
    
    /**
     * Disallow a {@link User} argument to represent an editor. If this flag is set, it causes the validation
     * method to throw a {@link IllegalArgumentException} if an argument of type {@link User} points to a user
     * record for an editor.
     * @see ArgumentUtils#validateUserArgument(User, String, int)
     * @see #USER_CONSTRAINT_NOT_STUDENT
     * @see #USER_CONSTRAINT_NOT_INSTRUCTOR
     * @see #USER_CONSTRAINT_NOT_ADMINISTRATOR
     */
    public static int USER_CONSTRAINT_NOT_EDITOR = 1024;

    /**
     * Disallow a {@link User} argument to represent an administrator. If this flag is set, it causes the
     * validation method to throw a {@link IllegalArgumentException} if an argument of type {@link User}
     * points to a user record for an administrator.
     * @see ArgumentUtils#validateUserArgument(User, String, int)
     * @see #USER_CONSTRAINT_NOT_STUDENT
     * @see #USER_CONSTRAINT_NOT_INSTRUCTOR
     * @see #USER_CONSTRAINT_NOT_EDITOR
     */    
    public static int USER_CONSTRAINT_NOT_ADMINISTRATOR = 2048;
    
    ////////////////////////////
    // PRIVATE STATIC METHODS //
    ////////////////////////////

    private static boolean testFlag(int number, int flag) {
        if ((number & flag) == flag) {
            return true;
        }
        return false;
    }
    
    ////////////////////
    // STATIC METHODS //
    ////////////////////

    /**
     * Returns a (deep) copy of a TString, or null if the TString was null.
     *
     * @param source The TString to deep copy.
     * @return A deep copy of the argument, or null if the argument was null.
     */
    public static TString copyOrNull(TString source) {
        if (source != null) {
            return source.deepCopy();
        }
        return null;
    }
    
    /**
     * Converts an empty, null or whitespace-only string to null.
     * 
     * @param source The string to examine.
     * @return <code>source</code>, or null if <code>source</code> was whitespace-only, empty or null.
     */
    public static String nullifyOnEmpty(String source) {
        if ((source == null) || (source.trim().isEmpty())) {
            return null;
        }
        return source;
    }
    
    /**
     * Returns the given String, or an empty string if the argument was null.
     */
    public static String emptyOnNull(String source) {
        if (source == null)
            return "";
        else
            return source;
    }

    /**
     * Asserts that an object is not null and throws a {@link NullPointerException} if it is.
     */
    public static void notNull(Object o) {
        if (o == null)
            throw new NullPointerException();
    }
    
    /**
     * Validates an argument of type {@link Course}. If the argument violates one of the
     * specified constraints, an appropriate exception is thrown. The constraints to use
     * are given as parameter to this function.
     * 
     * @param argument The argument of type {@link Course} that is to be validated
     * @param argumentName The name of the argument, used to create error messages
     * @param constraints The constraints to validate this argument against. Use the symbolic constants provided in this class to specify the constraints.
     * @see #COURSE_CONSTRAINT_NOT_NULL
     * @throws NullPointerException If the argument is {@code null}
     */
    public static void validateCourseArgument(Course argument, String argumentName, int constraints) {
        /* Check for null argument */
        if (testFlag(constraints, ArgumentUtils.COURSE_CONSTRAINT_NOT_NULL)) {
            if (argument == null) {
                throw new NullPointerException("Argument '" + argumentName + "' must not be null!");
            }
        }
    }

    /**
     * Validates an argument of type {@link Locale}. If the argument violates one of the
     * specified constraints, an appropriate exception is thrown. The constraints to use
     * are given as parameter to this function.
     * 
     * @param argument The argument of type {@link Locale} that is to be validated
     * @param argumentName The name of the argument, used to create error messages
     * @param constraints The constraints to validate this argument against. Use the symbolic constants provided in this class to specify the constraints.
     * @see #LOCALE_CONSTRAINT_NOT_NULL
     * @throws NullPointerException
     */
    public static void validateLocaleArgument(Locale argument, String argumentName, int constraints) {
        /* Check for null argument */
        if (testFlag(constraints, ArgumentUtils.LOCALE_CONSTRAINT_NOT_NULL)) {
            if (argument == null) {
                throw new NullPointerException("Argument '" + argumentName + "' must not be null!");
            }
        }
    }
    
    /**
     * Validates an argument of type {@link String}. If the argument violates one of the
     * specified constraints, an appropriate exception is thrown. The constraints to use
     * are given as parameter to this function.
     * 
     * @param argument The argument of type {@link String} that is to be validated
     * @param argumentName The name of the argument, used to create error messages
     * @param constraints The constraints to validate this argument against. Use the symbolic constants provided in this class to specify the constraints.
     * @see #STRING_CONSTRAINT_NOT_BLANK
     * @see #STRING_CONSTRAINT_NOT_EMPTY
     * @see #STRING_CONSTRAINT_NOT_NULL
     * @see #STRING_CONSTRAINT_NOT_WHITESPACE_ONLY
     * @throws IllegalArgumentException
     * @throws NullPointerException
     */
    public static void validateStringArgument(String argument, String argumentName, int constraints) {
        /* Check for null argument */
        if (testFlag(constraints, ArgumentUtils.STRING_CONSTRAINT_NOT_NULL)) {
            if (argument == null) {
                throw new NullPointerException("Argument '" + argumentName + "' must not be null!");
            }
        }
        /* Check for emptiness */
        if (testFlag(constraints, ArgumentUtils.STRING_CONSTRAINT_NOT_EMPTY)) {
            if (argument == null) {
                throw new NullPointerException("Argument '" + argumentName + "' must not be null!");
            }
            if (argument.length() == 0) {
                throw new IllegalArgumentException("Argument '" + argumentName + "' must not be empty!");
            }
        }
        /* Check for blank, i.e. for strings of whitespace only. */
        if (testFlag(constraints, ArgumentUtils.STRING_CONSTRAINT_NOT_BLANK)) {
            if (argument == null) {
                throw new NullPointerException("Argument '" + argumentName + "' must not be null!");
            }
            if (argument.trim().length() == 0) {
                throw new IllegalArgumentException("Argument '" + argumentName + "' must not be blank!");
            }            
        }
        /* Check for whitespace only strings. Null strings are permitted in this case! */
        if (testFlag(constraints, ArgumentUtils.STRING_CONSTRAINT_NOT_WHITESPACE_ONLY)) {
            if (argument == null) {
                /* There's nothing to test here */
                return;
            }
            if (argument.trim().length() == 0) {
                throw new IllegalArgumentException("Argument '" + argumentName + "' must not consist of whitespace only!");                
            }
        }
    }
    
    /**
     * DOC Missing Documentation
     * 
     * @param argument
     * @param argumentName
     * @param constraints
     */
    public static void validateUserArgument(User argument, String argumentName, int constraints) {
        /* Check for null */
        if (testFlag(constraints, ArgumentUtils.USER_CONSTRAINT_NOT_NULL)) {
            if (argument == null) {
                throw new NullPointerException("Argument '" + argumentName + "' must not be null!");
            }
        }
        /* Check for student user */
        if (testFlag(constraints, ArgumentUtils.USER_CONSTRAINT_NOT_STUDENT)) {
            if (argument != null) {
                if (argument.getParentRole() == TitoBaseRole.STUDENT) {
                    throw new IllegalArgumentException("Argument '" + argumentName + "' must not represent a student user!");
                }
            }
        }
        /* Check for assistant user */
        if (testFlag(constraints, ArgumentUtils.USER_CONSTRAINT_NOT_INSTRUCTOR)) {
            if (argument != null) {
                if (argument.getParentRole() == TitoBaseRole.ASSISTANT) {
                    throw new IllegalArgumentException("Argument '" + argumentName + "' must not represent an assistant user!");
                }
            }
        }
        /* Check for editor user */
        if (testFlag(constraints, ArgumentUtils.USER_CONSTRAINT_NOT_EDITOR)) {
            if (argument != null) {
                if (argument.getParentRole() == TitoBaseRole.EDITOR) {
                    throw new IllegalArgumentException("Argument '" + argumentName + "' must not represent an editor user!");
                }
            }
        }
        /* Check for administrator user */
        if (testFlag(constraints, ArgumentUtils.USER_CONSTRAINT_NOT_ADMINISTRATOR)) {
            if (argument != null) {
                if (argument.getParentRole() == TitoBaseRole.ADMINISTRATOR) {
                    throw new IllegalArgumentException("Argument '" + argumentName + "' must not represent an administrator user!");
                }
            }
        }
    }

    //////////////////
    // CONSTRUCTORS //
    //////////////////
    
    /**
     * DOC Missing Documentation
     * 
     * @deprecated This class can not be instantiated
     * @throws UnsupportedOperationException
     */
    public ArgumentUtils() {
        throw new UnsupportedOperationException("Class 'ArgumentUtils' can not be instantiated meaningful!");
    }

}