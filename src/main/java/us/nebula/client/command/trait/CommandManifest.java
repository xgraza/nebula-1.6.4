package us.nebula.client.command.trait;

import us.nebula.client.command.Command;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CommandManifest
{
    String[] aliases();

    String description() default Command.DEFAULT_DESCRIPTION;
}
