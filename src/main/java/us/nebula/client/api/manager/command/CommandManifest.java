package us.nebula.client.api.manager.command;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author xgraza
 * @since 08/12/25
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandManifest
{
    String[] aliases();

    String description() default Command.DEFAULT_DESCRIPTION;
}
