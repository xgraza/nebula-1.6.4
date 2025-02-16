package us.nebula.api.manager.command;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author xgraza
 * @since 02/16/25
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandManifest
{
    String[] aliases();
    String description() default Command.DEFAULT_DESCRIPTION;
}
