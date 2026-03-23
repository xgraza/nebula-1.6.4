package us.nebula.client.api.manager.hud;

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

    double x() default -1;
    double y() default -1;
    double height() default -1;
    double width() default -1;
    double padding() default 1;
}
