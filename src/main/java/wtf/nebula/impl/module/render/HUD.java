package wtf.nebula.impl.module.render;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.src.EnumChatFormatting;
import net.minecraft.src.GuiChat;
import wtf.nebula.Nebula;
import wtf.nebula.event.RenderHUDEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;
import wtf.nebula.repository.impl.ModuleRepository;

// TODO: use hud elements
public class HUD extends Module {
    public HUD() {
        super("HUD", ModuleCategory.RENDER);

        setState(true);
        drawn.setValue(false);
    }

    @EventListener
    public void onRenderHUD(RenderHUDEvent event) {
        // watermark
        mc.fontRenderer.drawStringWithShadow(EnumChatFormatting.LIGHT_PURPLE + Nebula.NAME + " v" + Nebula.VERSION, 2, 2, -1);

        // active modules
        double y = mc.fontRenderer.FONT_HEIGHT + 4.0;

        for (Module module : ModuleRepository.get().getChildren()) {
            if (!module.getState() || !module.drawn.getValue()) {
                continue;
            }

            mc.fontRenderer.drawStringWithShadow(">" + module.getName(), 4, (int) y, -1);
            y += mc.fontRenderer.FONT_HEIGHT + 2.0;
        }

        // coordinates
        y = event.getResolution().getScaledHeight() - mc.fontRenderer.FONT_HEIGHT - 2;
        if (mc.currentScreen instanceof GuiChat) {
            y -= 14.0;
        }

        mc.fontRenderer.drawStringWithShadow(
                EnumChatFormatting.GRAY + "XYZ: " + EnumChatFormatting.RESET
                        + String.format("%.1f", mc.thePlayer.posX) + ", "
                        + String.format("%.1f", mc.thePlayer.boundingBox.minY) + ", "
                        + String.format("%.1f", mc.thePlayer.posZ),
                2, (int) y, -1);
    }
}
