package us.nebula.client.impl.gui.overlay;

import net.minecraft.client.gui.ScaledResolution;
import us.nebula.client.ClientSettings;
import us.nebula.client.api.gui.font.Fonts;
import us.nebula.client.api.manager.overlay.Overlay;
import us.nebula.client.api.manager.overlay.OverlayManifest;
import us.nebula.client.api.manager.overlay.StaticPosition;

/**
 * @author xgraza
 * @since 02/26/25
 */
@StaticPosition
@OverlayManifest(value = "Watermark", defaultState = true)
public final class WatermarkOverlay extends Overlay
{
    @Override
    public void render(final ScaledResolution resolution, final float partialTicks)
    {
        Fonts.POPPINS.drawStringShadow("Nebula " + ClientSettings.VERSION, 2, 2, -1);
    }
}
