package us.nebula.impl.gui.overlay;

import net.minecraft.client.gui.ScaledResolution;
import us.nebula.api.gui.font.Fonts;
import us.nebula.api.manager.overlay.Overlay;
import us.nebula.api.manager.overlay.OverlayManifest;

/**
 * @author xgraza
 * @since 02/26/25
 */
@OverlayManifest(value = "Watermark", defaultState = true)
public final class WatermarkOverlay extends Overlay
{
    @Override
    public void render(final ScaledResolution resolution, final float partialTicks)
    {
        Fonts.drawStringShadow("Nebula Client", 2, 2, -1);
    }
}
