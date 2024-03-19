package yzy.szn.api.eventbus;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author graza
 * @since 02/17/24
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Subscribe {
    int priority() default 0;
    boolean canceled() default false;
}
