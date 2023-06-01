package lol.nebula.module.combat;

import lol.nebula.Nebula;
import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.entity.move.EventWalkingUpdate;
import lol.nebula.listener.events.net.EventPacket;
import lol.nebula.listener.events.render.world.EventRender3D;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.module.visual.Interface;
import lol.nebula.setting.Setting;
import lol.nebula.util.math.RotationUtils;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBed;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.potion.Potion;
import net.minecraft.util.*;
import net.minecraft.world.Explosion;

import java.util.List;

import static lol.nebula.util.render.ColorUtils.withAlpha;
import static lol.nebula.util.render.RenderUtils.*;
import static lol.nebula.util.world.WorldUtils.*;
import static org.lwjgl.opengl.GL11.*;

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
    private final Setting<Float> maxFriend = new Setting<>(8.0f, 0.01f, 0.0f, 20.0f, "Maximum Friend");
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
        if (target == null || info == null || (info.x == -1 && info.y == -1 && info.z == -1)) return;

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

            setColor(withAlpha(Interface.color.getValue().getRGB(), 120));
            filledAabb(bb);
            setColor(Interface.color.getValue().getRGB());
            outlinedAabb(bb);

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

        //     /** The id for the dimension (ex. -1: Nether, 0: Overworld, 1: The End) */
        //     public int dimensionId;
        if (mc.theWorld.provider.dimensionId == 0) return;

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

            mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(
                    info.x, info.y, info.z, EnumFacing.UP.getOrder_a(),
                    Nebula.getInstance().getInventory().getStack(), 0.0f, 0.5f, 0.0f));
            mc.thePlayer.swingItemSilent();
        }
    }

    @Listener
    public void onPacketInbound(EventPacket.Inbound event) {
        if (event.getPacket() instanceof S23PacketBlockChange) {
            S23PacketBlockChange packet = event.getPacket();
            if (packet.func_148880_c() == null || !packet.func_148880_c().equals(Blocks.bed)) return;

            int x = packet.func_148879_d();
            int y = packet.func_148878_e();
            int z = packet.func_148877_f();

            if (info.x == x && info.y == y && info.z == z) {
                mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(x, y, z, EnumFacing.UP.getOrder_a(),
                        Nebula.getInstance().getInventory().getStack(), 0.0f, 0.5f, 0.0f));
                mc.thePlayer.swingItemSilent();
            }
        }
    }

    private void calculatePlace() {
        Vec3 groundPos = mc.thePlayer.getGroundPosition();

        PlaceInfo placeInfo = new PlaceInfo();
        placeInfo.damage = minDamage.getValue();
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

                        // dont try to attack the player if theyre "invalid"
                        if (player == null
                                || player.isDead
                                || player.getHealth() <= 0.0f
                                || player.equals(mc.thePlayer)
                                || Nebula.getInstance().getFriends().isFriend(player)) continue;

                        float playerDamage = calcDamage(player, posX, posY, posZ) * m;
                        if (playerDamage < minDamage.getValue()
                                || selfDamage > playerDamage
                                || playerDamage < placeInfo.damage) continue;

                        EnumFacing face = getPlaceFace(posX, posY, posZ);
                        if (face == null) return;

//                        float other = calcDamage(player, posX + face.getFrontOffsetX(), posY + face.getFrontOffsetY(), posZ + face.getFrontOffsetZ());
//
//                        placeInfo.damage = (playerDamage + other) / 2.0f;
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
            if (block == Blocks.water
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
                new Vec3(Vec3.fakePool, x + 0.5, y + 0.5, z + 0.5), target.boundingBox.copy());

        float roughDamage = (float) ((v * v + v) / 2.0 * 8.0 * BED_EXPLOSION_SIZE + 1.0);
        float damage = getDamageAfterAbsorb(
                getDamageMultiplierOnDiff(roughDamage),
                target.getTotalArmorValue(),
                (float) target.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).getAttribute().getDefaultValue());

        Explosion explosion = new Explosion(target.worldObj, target, x + 0.5, y + 0.5, z + 0.5, BED_EXPLOSION_STRENGTH);
        explosion.isFlaming = true;
        explosion.isSmoking = true;

        DamageSource src = DamageSource.setExplosionSource(explosion);
        int n = EnchantmentHelper.getEnchantmentModifierDamage(target.inventory.armorInventory, src);
        damage = getDamageAfterMagicAbsorb(damage, (float) n);
        damage = (float) EnchantmentProtection.func_92092_a(mc.thePlayer, damage);

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
