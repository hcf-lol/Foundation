package lol.hcf.foundation.command.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation may be used to mark the last variable of a {@link CommandEntryPoint} annotated method nullable/non-required.
 * Whenever this annotation is present, if specified in the command arguments, the parameter will be non-null, otherwise it will
 * be null.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Optional {}
