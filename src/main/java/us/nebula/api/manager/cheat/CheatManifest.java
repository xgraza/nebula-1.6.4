package us.nebula.api.manager.cheat;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author xgraza
 * @since 02/14/25
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface CheatManifest
{
    String name();
    String description() default Cheat.DEFAULT_DESCRIPTION;
    CheatCategory category();
}
