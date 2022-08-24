package wtf.nebula.impl.module.render;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import wtf.nebula.Nebula;
import wtf.nebula.event.MotionUpdateEvent;
import wtf.nebula.event.RenderHUDEvent;
import wtf.nebula.impl.gui.hud.HUDElement;
import wtf.nebula.impl.gui.hud.impl.Arraylist;
import wtf.nebula.impl.gui.hud.impl.Watermark;
import wtf.nebula.impl.gui.ui.ClickGUIScreen;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;
import wtf.nebula.impl.value.Value;
import wtf.nebula.repository.impl.ModuleRepository;
import wtf.nebula.util.render.ColorUtil;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;

// TODO: use hud elements
public class HUD extends Module {
    public static final List<HUDElement> elements = new ArrayList<>();

    public HUD() {
        super("HUD", ModuleCategory.RENDER);

        setState(true);
        drawn.setValue(false);

        elements.add(new Watermark(this));
        elements.add(new Arraylist(this));
    }

    public final Value<Mode> mode = new Value<>("Mode", Mode.OLD);

    public final Value<Boolean> watermark = new Value<>("Watermark", true);
    public final Value<Boolean> arrayList = new Value<>("ArrayList", true);

    public final Value<Boolean> rainbow = new Value<>("Rainbow", false);

    private double speed = 0.0;

    @Override
    protected void onDeactivated() {
        super.onDeactivated();
    }

    @EventListener
    public void onRenderHUD(RenderHUDEvent event) {
        if (mc.currentScreen instanceof ClickGUIScreen || mc.gameSettings.showDebugInfo) {
            return;
        }

        glPushMatrix();

        if (mode.getValue().equals(Mode.NEW)) {
            elements.forEach((element) -> element.onRender(event.getResolution()));
        }

        else {

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

            y -= (mc.fontRenderer.FONT_HEIGHT + 2);

            str = EnumChatFormatting.GRAY + "FPS: " + EnumChatFormatting.RESET + Minecraft.debugFPS;
            mc.fontRenderer.drawStringWithShadow(str,
                    event.getResolution().getScaledWidth() - mc.fontRenderer.getStringWidth(str) - 2,
                    (int) y,
                    -1);


            // armor hud
            ScaledResolution resolution = event.getResolution();
            for (int i = 0; i < 4; ++i) {
                ItemStack stack = mc.thePlayer.inventory.armorInventory[i];
                if (stack == null || stack.getItem() == null) {
                    continue;
                }

                double x = (resolution.getScaledWidth() / 2.0) + ((9 - i) * 16) - 80.0;
                double y1 = resolution.getScaledHeight() - 55.0;

                glPushMatrix();

                RenderHelper.enableGUIStandardItemLighting();

                RenderItem renderItem = (RenderItem) RenderManager.instance.getEntityClassRenderObject(EntityItem.class);

                renderItem.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), stack, (int) x, (int) y1);
                renderItem.renderItemOverlayIntoGUI(mc.fontRenderer, mc.getTextureManager(), stack, (int) x, (int) y1);

                RenderHelper.disableStandardItemLighting();

                glPopMatrix();
            }
        }

        glPopMatrix();
    }

    @EventListener
    public void onMotionUpdate(MotionUpdateEvent event) {
        double diffX = 0.0;
        double diffZ = 0.0;

        if (mc.thePlayer.isRiding()) {
            Entity ridingEntity = mc.thePlayer.ridingEntity;

            diffX = ridingEntity.posX - ridingEntity.lastTickPosX;
            diffZ = ridingEntity.posZ - ridingEntity.lastTickPosZ;
        }

        else {
            diffX = mc.thePlayer.posX - mc.thePlayer.lastTickPosX;
            diffZ = mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ;
        }

        speed = (Math.sqrt(diffX * diffX + diffZ * diffZ) / 1000) / (0.05 / 3600);
        speed *= mc.timer.timerSpeed;
    }

    private String getSpeedFormatted() {
        return String.format("%.2f", speed);
    }

    public int color(int d) {
        if (rainbow.getValue()) {
            return ColorUtil.rainbow(d, 1.0f, 1.0f);
        }

        return -1;
    }

    public enum Mode {
        NEW, OLD
    }
}
