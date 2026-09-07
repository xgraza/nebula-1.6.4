package ez.nebula.client.impl.hud2;

import ez.nebula.client.ClientConfig;
import ez.nebula.client.api.manager.hud2.trait.HUDManifest;
import ez.nebula.client.api.manager.hud2.type.TextHUDElement;
import ez.nebula.client.impl.module.render.HUDModule;

/**
 * @author xgraza
 * @since 9/6/26
 */
@HUDManifest(value = "Watermark",
        description = "Displays the client watermark",
        defaultX = 2,
        defaultY = 2)
public final class WatermarkHUDElement extends TextHUDElement
{
    @Override
    public String text()
    {
        return "Nebula " + ClientConfig.FULL_VERSION;
    }

    @Override
    public int textColor()
    {
        return HUDModule.INSTANCE.getBaseColor(10);
    }
}
