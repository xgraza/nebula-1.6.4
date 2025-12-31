package us.nebula.client.api.manager.overlay;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author xgraza
 * @since 03/06/25
 * @apiNote If an {@link Overlay} class is annotated with this, the overlay is immovable
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface StaticPosition
{
}
