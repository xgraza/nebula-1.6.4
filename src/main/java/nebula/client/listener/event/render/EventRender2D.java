package nebula.client.listener.event.render;

import net.minecraft.client.gui.ScaledResolution;

/**
 * @author Gavin
 * @since 08/09/23
 */
public record EventRender2D(ScaledResolution resolution, float tickDelta) {
}
