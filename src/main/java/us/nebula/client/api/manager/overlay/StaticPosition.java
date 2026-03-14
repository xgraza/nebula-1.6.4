package us.nebula.client.api.manager.overlay;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author xgraza
 * @apiNote If an {@link Overlay} class is annotated with this, the overlay is immovable
 * @since 03/06/25
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface StaticPosition
{
}
