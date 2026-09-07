package ez.nebula.client.api.manager.hud2.trait;

import ez.nebula.client.api.manager.hud2.HUDElement;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface HUDManifest
{
    String value();
    String description() default HUDElement.DEFAULT_DESCRIPTION;

    double defaultX() default -1.0;
    double defaultY() default -1.0;
    double defaultWidth() default -1.0;
    double defaultHeight() default -1.0;
}
