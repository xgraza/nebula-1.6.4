package ez.nebula.client.api.manager.module.type;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author xgraza
 * @since 9/4/26
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface RotationPriority
{
    int value();
}
