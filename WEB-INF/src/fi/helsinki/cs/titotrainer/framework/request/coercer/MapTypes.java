package fi.helsinki.cs.titotrainer.framework.request.coercer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Used with request fields of type Map to specify how
 * the map keys and values should be converted.</p>
 * 
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MapTypes {
    Class<? extends SimpleFieldCoercer<?>> keyCoercer() default StringCoercer.class;
    Class<? extends SimpleFieldCoercer<?>> valueCoercer() default StringCoercer.class;
}
