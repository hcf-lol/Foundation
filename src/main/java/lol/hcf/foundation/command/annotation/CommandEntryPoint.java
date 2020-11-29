package lol.hcf.foundation.command.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies the function entry point for a class extending {@link lol.hcf.factions.command.Command}
 * The entry point MUST include a {@link org.bukkit.command.CommandSender} as the first parameter and
 * all primitive types must be in their object type wrappers. All parameters can be assumed non-null except for
 * parameters that are marked with {@link Optional}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CommandEntryPoint {

}
