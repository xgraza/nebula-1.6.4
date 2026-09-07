package ez.nebula.client.impl.hud2;

import ez.nebula.client.api.manager.hud2.trait.HUDManifest;
import ez.nebula.client.api.manager.hud2.type.TextHUDElement;

/**
 * @author xgraza
 * @since 9/6/26
 */
@HUDManifest(value = "Health", description = "Displays your health")
public final class HealthHUDElement extends TextHUDElement
{
    @Override
    public String text()
    {
        return "Health: " + String.format("%.1f", MC.thePlayer.getHealth() + MC.thePlayer.getAbsorptionAmount());
    }
}
