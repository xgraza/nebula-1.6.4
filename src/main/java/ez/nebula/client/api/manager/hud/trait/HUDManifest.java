package ez.nebula.client.api.manager.hud.trait;

import ez.nebula.client.api.manager.hud.HUDElement;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author xgraza
 * @since 3/23/26
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface HUDManifest
{
    String name();

    String description() default HUDElement.DEFAULT_DESCRIPTION;

    double x() default 0;

    double y() default 0;

    double height() default 0;

    double width() default 0;

    double padding() default 1;
}
