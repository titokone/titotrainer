package fi.helsinki.cs.titotrainer.testsupport;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Defines the order of execution of JUnit <code>@Before</code> methods.</p>
 * 
 * <p>Methods with a lower priority value are run first.
 * The priority of methods without this annoatation is considered to be 0.</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface BeforePriority {
    int value();
}
