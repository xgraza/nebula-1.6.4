package lol.nebula.module.combat;

import lol.nebula.Nebula;
import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.entity.move.EventWalkingUpdate;
import lol.nebula.listener.events.render.world.EventRender3D;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.setting.Setting;
import lol.nebula.util.math.RotationUtils;
import lol.nebula.util.render.RenderUtils;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBed;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.*;
import net.minecraft.world.Explosion;

import java.awt.*;
import java.util.List;

import static lol.nebula.util.render.RenderUtils.filledAabb;
import static lol.nebula.util.render.RenderUtils.setColor;
import static lol.nebula.util.world.WorldUtils.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glPopMatrix;

/**
 * @author aesthetical
 * @since 05/27/23
 */
public class AutoBed extends Module {

    private static final float BED_EXPLOSION_STRENGTH = 5.0f;
    private static final float BED_EXPLOSION_SIZE = BED_EXPLOSION_STRENGTH * 2.0f;

    private final Setting<Boolean> place = new Setting<>(true, "Place");
    private final Setting<Boolean> destroy = new Setting<>(true, "Break");

    private final Setting<Boolean> rotate = new Setting<>(true, "Rotate");
    private final Setting<Switch> swap = new Setting<>(Switch.CLIENT, "Switch");

    private final Setting<Double> range = new Setting<>(4.0, 0.01, 1.0, 6.0, "Range");

    private final Setting<Float> damageMultiplier = new Setting<>(1.5f, 0.1f, 1.0f, 2.0f, "Damage Multiplier");
    private final Setting<Float> maxLocal = new Setting<>(6.0f, 0.01f, 0.0f, 20.0f, "Maximum Local");
    private final Setting<Float> minDamage = new Setting<>(4.0f, 0.01f, 0.0f, 20.0f, "Minimum Damage");

    private EntityPlayer target;
    private PlaceInfo info;
    private int previousSlot = -1;

    public AutoBed() {
        super("Auto Bed", "Automatically places and breaks beds", ModuleCategory.COMBAT);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        target = null;
        info = null;

        if (previousSlot != -1) mc.thePlayer.inventory.currentItem = previousSlot;
        previousSlot = -1;
    }

    @Listener
    public void onRender3D(EventRender3D event) {
        if (target == null || (info.x == -1 && info.y == -1 && info.z == -1)) return;

        double renderX = RenderManager.renderPosX;
        double renderY = RenderManager.renderPosY;
        double renderZ = RenderManager.renderPosZ;

        AxisAlignedBB bb = new AxisAlignedBB(info.x, info.y, info.z,
                info.x + 1.0, info.y + 0.5, info.z + 1.0)
                .addCoord(info.facing.getFrontOffsetX(),
                        info.facing.getFrontOffsetY(),
                        info.facing.getFrontOffsetZ())
                .offset(-renderX, -renderY, -renderZ);

        {
            glPushMatrix();
            glEnable(GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 0, 1);
            glDisable(GL_TEXTURE_2D);
            glDisable(GL_DEPTH_TEST);

            setColor(new Color(255, 0, 0, 80).getRGB());
            filledAabb(bb);

            glEnable(GL_DEPTH_TEST);
            glEnable(GL_TEXTURE_2D);
            glDisable(GL_BLEND);
            glPopMatrix();
        }

        {
            glPushMatrix();
            glEnable(GL_POLYGON_OFFSET_FILL);
            glPolygonOffset(1.0f, -1100000.0f);

            RenderHelper.disableStandardItemLighting();
            glDisable(GL_LIGHTING);

            Vec3 center = bb.getCenter();

            glTranslated(center.xCoord, (center.yCoord + 0.25), center.zCoord);
            glRotatef(-RenderManager.instance.playerViewY, 0.0f, 1.0f, 0.0f);
            glRotatef(RenderManager.instance.playerViewX, mc.gameSettings.thirdPersonView == 2 ? -1.0f : 1.0f, 0.0f, 0.0f);

            double distance = mc.renderViewEntity.getDistance(info.x, info.y, info.z);
            double scaling = (0.25 * Math.max(distance, 4.0)) / 50.0;
            glScaled(-scaling, -scaling, scaling);

            glDisable(GL_DEPTH_TEST);

            String text = String.format("%.2f", info.damage);
            int height = mc.fontRenderer.FONT_HEIGHT;
            double width = mc.fontRenderer.getStringWidth(text) / 2.0;

            mc.fontRenderer.drawStringWithShadow(text, (int) -width, (int) (-height + (((height + 3) / 2.0) - (height / 2.0))), -1);

            glEnable(GL_DEPTH_TEST);

            glEnable(GL_DEPTH_TEST);
            glDepthMask(true);

            glPolygonOffset(1.0f, 1100000.0f);
            glDisable(GL_POLYGON_OFFSET_FILL);

            glEnable(GL_ALPHA_TEST);

            glPopMatrix();
        }
    }

    @Listener
    public void onWalkingUpdate(EventWalkingUpdate event) {

        // calculate bed placement
        calculatePlace();

        // if info is null or there is no placement position found, return
        if (info == null || (info.x == -1 && info.y == -1 && info.z == -1)) return;

        // rotate
        if (rotate.getValue()) {

            // beds are placed based on yaw calculations
            float yaw = MathHelper.wrapAngleTo180_float(getHorizontalFace(info.facing) * 90.0f);

            // use whatever pitch was calculated
            float pitch = RotationUtils.toBlock(info.x, info.y, info.z, info.facing)[1];

            // spoof
            Nebula.getInstance().getRotations().spoof(new float[] { yaw, pitch });
        }

        // interact result
        boolean result = false;

        if (place.getValue()) {

            // swap to a bed
            switch (swap.getValue()) {
                case NONE: {

                    ItemStack itemStack = Nebula.getInstance().getInventory().getStack();
                    if (itemStack == null || !(itemStack.getItem() instanceof ItemBed)) return;

                    break;
                }

                case SERVER:
                case CLIENT: {
                    int slot = -1;
                    for (int i = 0; i < 9; ++i) {
                        ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);
                        if (itemStack != null && itemStack.getItem() instanceof ItemBed) slot = i;
                    }

                    if (slot == -1) return;

                    if (swap.getValue() == Switch.SERVER) {
                        Nebula.getInstance().getInventory().setSlot(slot);
                    } else if (swap.getValue() == Switch.CLIENT) {
                        previousSlot = mc.thePlayer.inventory.currentItem;
                        mc.thePlayer.inventory.currentItem = slot;
                    }
                    break;
                }
            }

            result = mc.playerController.onPlayerRightClick(
                    mc.thePlayer,
                    mc.theWorld,
                    Nebula.getInstance().getInventory().getStack(),
                    info.x, info.y - 1, info.z,
                    EnumFacing.UP.getOrder_a(),
                    getHitVec(info.x, info.y, info.z, EnumFacing.UP));

            if (result) mc.thePlayer.swingItemSilent();
        }

        if (destroy.getValue() && result) {

            if (swap.getValue() == Switch.SERVER) {
                Nebula.getInstance().getInventory().sync();
            } else if (swap.getValue() == Switch.CLIENT) {
                if (previousSlot != -1) mc.thePlayer.inventory.currentItem = previousSlot;
                previousSlot = -1;
            }

            result = mc.playerController.onPlayerRightClick(
                    mc.thePlayer,
                    mc.theWorld,
                    Nebula.getInstance().getInventory().getStack(),
                    info.x, info.y, info.z,
                    EnumFacing.UP.getOrder_a(),
                    getHitVec(info.x, info.y, info.z, EnumFacing.UP));

            if (result) mc.thePlayer.swingItemSilent();
        }
    }

    private void calculatePlace() {
        Vec3 groundPos = mc.thePlayer.getGroundPosition();

        PlaceInfo placeInfo = new PlaceInfo();
        placeInfo.damage = damageMultiplier.getValue() - 0.5f;
        target = null;

        int r = range.getValue().intValue();
        for (int x = -r; x <= r; ++x) {
            for (int y = -r; y <= r; ++y) {
                for (int z = -r; z <= r; ++z) {

                    int posX = (int) (Math.floor(groundPos.xCoord) + x);
                    int posY = (int) (Math.floor(groundPos.yCoord) + y);
                    int posZ = (int) (Math.floor(groundPos.zCoord) + z);

                    if (mc.thePlayer.getDistanceSq(
                            posX + 0.5,
                            posY + 1.0,
                            posZ + 0.5) >= range.getValue() * range.getValue()) continue;

                    float m = damageMultiplier.getValue();

                    float selfDamage = calcDamage(mc.thePlayer, posX, posY, posZ) * m;
                    if (selfDamage > maxLocal.getValue() || selfDamage > mc.thePlayer.getHealth() * m) continue;

                    for (EntityPlayer player : (List<EntityPlayer>) mc.theWorld.playerEntities) {
                        float playerDamage = calcDamage(player, posX, posY, posZ) * m;
                        if (playerDamage < minDamage.getValue()
                                || selfDamage > playerDamage
                                || playerDamage < placeInfo.damage) continue;

                        EnumFacing face = getPlaceFace(posX, posY, posZ);
                        if (face == null) return;

                        placeInfo.damage = playerDamage;
                        placeInfo.facing = face;
                        placeInfo.x = posX;
                        placeInfo.y = posY;
                        placeInfo.z = posZ;

                        target = player;
                    }

                }
            }
        }

        info = placeInfo;
    }

    private int getHorizontalFace(EnumFacing facing) {
        // LOL

        switch (facing) {
            case SOUTH:
                return 0;

            case EAST:
                return 1;

            case NORTH:
                return 2;

            case WEST:
                return 3;
        }

        return -1;
    }

    private EnumFacing getPlaceFace(int x, int y, int z) {

        // if the coordinate to place at is not replaceable or there is no surface to place on
        if (!isReplaceable(x, y, z) || isReplaceable(x, y - 1, z)) return null;

        EnumFacing f = null;
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            int posX = x + facing.getFrontOffsetX();
            int posY = y + facing.getFrontOffsetY();
            int posZ = z + facing.getFrontOffsetZ();

            // if the coordinate direction offset is not replaceable or there is no surface to place on
            if (!isReplaceable(posX, posY, posZ) || isReplaceable(posX, posY - 1, posZ)) continue;

            // get the block at this position
            Block block = getBlock(posX, posY, posZ);

            // if the block is blocked from being placed at
            if (block == Blocks.fire
                    || block == Blocks.water
                    || block == Blocks.lava
                    || block == Blocks.flowing_water
                    || block == Blocks.flowing_lava) continue;

            // re-assign "f" to this one
            f = facing;
        }

        // return "f" or null
        return f;
    }

    private float calcDamage(EntityPlayer target, int x, int y, int z) {
        double dist = target.getDistance(x, y, z) / BED_EXPLOSION_SIZE;
        if (dist > 1.0) return 0.0f;

        double v = (1.0 - dist) * target.worldObj.getBlockDensity(
                new Vec3(Vec3.fakePool, x, y, z), target.boundingBox.copy());

        float roughDamage = (float) ((v * v + v) / 2.0 * 7.0 * BED_EXPLOSION_SIZE + 1.0);
        float damage = getDamageAfterAbsorb(
                getDamageMultiplierOnDiff(roughDamage),
                target.getTotalArmorValue(),
                (float) target.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).getAttribute().getDefaultValue());

        DamageSource src = DamageSource.setExplosionSource(
                new Explosion(target.worldObj, target, x, y, z, BED_EXPLOSION_STRENGTH));

        int n = EnchantmentHelper.getEnchantmentModifierDamage(target.inventory.armorInventory, src);
        damage = getDamageAfterMagicAbsorb(damage, (float) n);

        if (target.isPotionActive(Potion.resistance.id)) {
            int amp = mc.thePlayer.getActivePotionEffect(Potion.resistance).getAmplifier();
            damage = damage * (25.0f - (amp + 1.0f) * 5.0f) / 25.0f;
        }

        return (float) Math.max(0.0, damage);
    }

    private float getDamageAfterAbsorb(float damage, float totalArmor, float toughnessAttribute) {
        float f = 2.0F + toughnessAttribute / 4.0F;
        float f1 = MathHelper.clamp_float(totalArmor - damage / f, totalArmor * 0.2F, 20.0F);
        return damage * (1.0F - f1 / 25.0F);
    }

    private float getDamageAfterMagicAbsorb(float damage, float enchantModifiers) {
        float f = MathHelper.clamp_float(enchantModifiers, 0.0F, 20.0F);
        return damage * (1.0F - f / 25.0F);
    }

    private float getDamageMultiplierOnDiff(float damage) {
        switch (mc.theWorld.difficultySetting) {
            case PEACEFUL:
                return 0.0f;

            case EASY:
                return Math.min(damage / 2.0f + 1.0f, damage);

            case HARD:
                return damage * 3.0f / 2.0f;
        }

        return damage;
    }

    public enum Switch {
        NONE, SERVER, CLIENT
    }

    private static class PlaceInfo {
        public EnumFacing facing;
        public float damage;
        public int x = -1, y = -1, z = -1;
    }
}
