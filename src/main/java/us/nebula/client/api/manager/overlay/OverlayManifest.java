package us.nebula.client.api.manager.overlay;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author xgraza
 * @since 02/26/25
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface OverlayManifest
{
    String value();
    boolean defaultState() default false;
}
