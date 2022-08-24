package wtf.nebula.impl.gui.hud.impl;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;
import wtf.nebula.Nebula;
import wtf.nebula.impl.gui.hud.HUDElement;
import wtf.nebula.impl.module.render.HUD;

public class Watermark extends HUDElement {
    public Watermark(HUD hud) {
        super(hud, "Watermark");

        x = 4;
        y = 4;
    }

    @Override
    public void onRender(ScaledResolution resolution) {
        if (!hud.watermark.getValue()) {
            return;
        }

        String text = Nebula.NAME + " v" + Nebula.VERSION + "-" + Nebula.TAG;
        mc.fontRenderer.drawStringWithShadow((hud.rainbow.getValue() ? "" : EnumChatFormatting.LIGHT_PURPLE) + text, (int) x, (int) y, hud.color(100));
    }
}
