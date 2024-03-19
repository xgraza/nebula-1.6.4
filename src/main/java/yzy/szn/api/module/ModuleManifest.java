package yzy.szn.api.module;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author graza
 * @since 02/17/24
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ModuleManifest {
    String name();
    String description() default "No description was provided";
    ModuleCategory category();
}
