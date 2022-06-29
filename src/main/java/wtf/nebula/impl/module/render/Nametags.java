package wtf.nebula.impl.module.render;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.src.*;
import wtf.nebula.event.RenderWorldEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;
import wtf.nebula.impl.value.Value;
import wtf.nebula.repository.impl.FriendRepository;
import wtf.nebula.util.MathUtil;
import wtf.nebula.util.render.RenderUtil;

import static org.lwjgl.opengl.GL11.*;

public class Nametags extends Module {
    public Nametags() {
        super("Nametags", ModuleCategory.RENDER);
    }

    public final Value<Boolean> armor = new Value<>("Armor", true);
    public final Value<Boolean> ping = new Value<>("Ping", true);
    public final Value<Boolean> health = new Value<>("Health", true);

    @EventListener
    public void onRenderWorld(RenderWorldEvent event) {
        for (Object obj : mc.theWorld.playerEntities) {
            if (!(obj instanceof EntityPlayer)) {
                continue;
            }

            EntityPlayer player = (EntityPlayer) obj;

            double x = MathUtil.interpolate(player.posX, player.lastTickPosX, event.getPartialTicks()) - RenderManager.renderPosX;
            double y = MathUtil.interpolate(player.posY, player.lastTickPosY, event.getPartialTicks()) - RenderManager.renderPosY;
            double z = MathUtil.interpolate(player.posZ, player.lastTickPosZ, event.getPartialTicks()) - RenderManager.renderPosZ;

            renderPlayerTag(player, x, y, z);
        }
    }

    private void renderPlayerTag(EntityPlayer player, double x, double y, double z) {
        double renderX = RenderManager.renderPosX;
        double renderY = RenderManager.renderPosY;
        double renderZ = RenderManager.renderPosZ;

        double distance = mc.renderViewEntity.getDistance(x + renderX, y + renderY, z + renderZ);
        double scale = (0.3 * Math.max(distance, 4.0)) / 50.0;

        glPushMatrix();

        RenderHelper.enableGUIStandardItemLighting();
        glDisable(GL_LIGHTING);

        glEnable(GL_POLYGON_OFFSET_FILL);
        glPolygonOffset(1.0f, -1500000.0f);

        glTranslated(x, y + 0.5, z);
        glRotated(-RenderManager.instance.playerViewY, 0.0f, 1.0f, 0.0f);
        glRotated(RenderManager.instance.playerViewX, mc.gameSettings.thirdPersonView == 2 ? -1.0f : 1.0f, 0.0f, 0.0f);
        glScaled(-scale, -scale, scale);

        glDisable(GL_DEPTH_TEST);

        String info = getPlayerInfo(player);

        float width = mc.fontRenderer.getStringWidth(info) / 2.0f;

        RenderUtil.drawRect(-(width - 2.0), -(mc.fontRenderer.FONT_HEIGHT - 1.0), (width * 2.0) + 2.0, mc.fontRenderer.FONT_HEIGHT + 2.0, 0x70000000);
        mc.fontRenderer.drawStringWithShadow(info, (int) -(width - 4.0), -(mc.fontRenderer.FONT_HEIGHT - 2), -1);

//        int xOffset = -24 / 2 * player.inventory.armorInventory.length;
//
//        if (player.getHeldItem() != null && player.getHeldItem().getItem() != null) {
//            renderItem(player.getHeldItem(), xOffset, scale);
//        }
//
//        xOffset += 16;
//
//        if (armor.getValue()) {
//            for (ItemStack armor : player.inventory.armorInventory) {
//                if (armor == null || armor.getItem() == null) {
//                    continue;
//                }
//
//                renderItem(armor, xOffset, scale);
//                xOffset += 16;
//            }
//        }

        glPolygonOffset(1.0f, 1500000.0f);
        glDisable(GL_POLYGON_OFFSET_FILL);

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_LIGHTING);

        glPopMatrix();
    }

    // this is extremely broken... fix later i dont feel like it
    private void renderItem(ItemStack stack, int x, double scale) {
        // glPushMatrix();
        glDepthMask(true);
        RenderHelper.enableStandardItemLighting();

        RenderItem r = (RenderItem) RenderManager.instance.getEntityClassRenderObject(EntityItem.class);


        r.zLevel = -150.0f;

        glEnable(32826);

//        // glTranslated(x, -26, 0.0f);
//        glScaled(1.0f, -1.0f, 1.0f);
//        glScaled(16.0f, 16.0f, 16.0f);
        glTranslated(-0.5f, -0.5f, -0.5f);

        r.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), stack, x, -26);
        r.renderItemOverlayIntoGUI(mc.fontRenderer, mc.getTextureManager(), stack, x, -26);
        r.zLevel = 0.0f;

        RenderHelper.disableStandardItemLighting();

        // glPopMatrix();
    }

    private String getPlayerInfo(EntityPlayer player) {
        StringBuilder builder = new StringBuilder();

        if (ping.getValue()) {
            int latency = 0;

            try {
                for (GuiPlayerInfo info : mc.getNetHandler().playerInfoList) {
                    if (info.name.equals(player.getEntityName())) {
                        latency = info.responseTime;
                    }
                }
            } catch (Exception ignored) {

            }

            builder.append(latency).append("ms").append(" ");
        }

        if (FriendRepository.get().isFriend(player)) {
            builder.append(EnumChatFormatting.AQUA);
        }

        else {

            if (player.isSneaking()) {
                builder.append(EnumChatFormatting.GOLD);
            }
        }

        builder.append(player.getEntityName()).append(EnumChatFormatting.RESET);

        if (health.getValue()) {
            builder.append(" ");

            float health = player.getHealth() + player.getAbsorptionAmount();

            if (health >= 20.0f) {
                builder.append(EnumChatFormatting.GREEN);
            }

            if (health < 12.0f) {
                builder.append(EnumChatFormatting.RED);
            }

            if (health < 8.0f) {
                builder.append(EnumChatFormatting.DARK_RED);
            }

            builder.append(String.format("%.1f", health));
        }

        return builder.toString();
    }
}
