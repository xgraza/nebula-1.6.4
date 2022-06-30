package wtf.nebula.impl.module.render;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.src.EnumChatFormatting;
import net.minecraft.src.GuiChat;
import wtf.nebula.Nebula;
import wtf.nebula.event.MotionUpdateEvent;
import wtf.nebula.event.RenderHUDEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;
import wtf.nebula.repository.impl.ModuleRepository;

import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;

// TODO: use hud elements
public class HUD extends Module {
    public HUD() {
        super("HUD", ModuleCategory.RENDER);

        setState(true);
        drawn.setValue(false);
    }

    private double speed = 0.0;

    @EventListener
    public void onRenderHUD(RenderHUDEvent event) {
        glPushMatrix();

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

        // speed and other counter shit

        String str = EnumChatFormatting.GRAY + "Speed: " + EnumChatFormatting.RESET + getSpeedFormatted();
        mc.fontRenderer.drawStringWithShadow(str,
                event.getResolution().getScaledWidth() - mc.fontRenderer.getStringWidth(str) - 2,
                (int) y,
                -1);

        glPopMatrix();
    }

    @EventListener
    public void onMotionUpdate(MotionUpdateEvent event) {
        double diffX = mc.thePlayer.posX - mc.thePlayer.lastTickPosX;
        double diffZ = mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ;

        speed = (Math.sqrt(diffX * diffX + diffZ * diffZ) / 1000) / (0.05 / 3600) * (mc.timer.timerSpeed);
    }

    private String getSpeedFormatted() {
        return String.format("%.2f", speed);
    }
}
