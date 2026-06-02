package ez.nebula.client.api.manager.module.trait;

import ez.nebula.client.api.manager.module.Module;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author xgraza
 * @since 02/14/25
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ModuleManifest
{
    String name();

    String description() default Module.DEFAULT_DESCRIPTION;

    ModuleCategory category();
}
