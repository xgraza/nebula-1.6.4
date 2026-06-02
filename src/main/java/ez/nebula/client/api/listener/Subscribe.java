package ez.nebula.client.api.listener;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author xgraza
 * @since 02/14/25
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Subscribe
{
    int priority() default IEventPriorities.DEFAULT;

    boolean receiveCanceled() default false;
}
