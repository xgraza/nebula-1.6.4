package ez.nebula.client.api.manager.command.trait;

import ez.nebula.client.api.manager.command.Command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE_USE, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandManifest
{
    String[] aliases() default {};

    String description() default Command.DEFAULT_DESCRIPTION;
}
