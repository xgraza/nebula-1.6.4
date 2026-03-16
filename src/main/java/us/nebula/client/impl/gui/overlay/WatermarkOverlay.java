package us.nebula.client.impl.gui.overlay;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import us.nebula.client.ClientSettings;
import us.nebula.client.api.gui.font.Fonts;
import us.nebula.client.api.manager.overlay.Overlay;
import us.nebula.client.api.manager.overlay.OverlayManifest;
import us.nebula.client.api.manager.overlay.StaticPosition;
import us.nebula.client.api.value.Setting;
import us.nebula.client.impl.cheat.render.HUDCheat;
import us.nebula.client.util.render.RenderUtil;

/**
 * @author xgraza
 * @since 02/26/25
 */
@StaticPosition
@OverlayManifest(value = "Watermark", defaultState = true)
public final class WatermarkOverlay extends Overlay
{
    private static final ResourceLocation NEBULA_LOGO_RESOURCE = new ResourceLocation(
            "nebula", "texture/icon/128x.png");

    private final Setting<Boolean> showIconSetting = new Setting<>(
            "Icon", false);

    @Override
    public void render(final ScaledResolution resolution, final float partialTicks)
    {
        int offset = 0;
        if (showIconSetting.getValue())
        {
            int size = (int) Fonts.POPPINS.getFontHeight();
            RenderUtil.texture(NEBULA_LOGO_RESOURCE, 2, 2, size, size);
            offset = size + 2;
        }
        Fonts.POPPINS.drawStringShadow("Nebula " + ClientSettings.VERSION, 2 + offset, 2, HUDCheat.INSTANCE.getBaseColor(10));
    }
}
