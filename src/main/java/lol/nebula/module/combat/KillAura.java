package lol.nebula.module.combat;

import lol.nebula.Nebula;
import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.EventStage;
import lol.nebula.listener.events.entity.move.EventWalkingUpdate;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.setting.Setting;
import lol.nebula.util.math.MathUtils;
import lol.nebula.util.math.RotationUtils;
import lol.nebula.util.math.timing.Timer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;

import java.util.Comparator;

import static java.lang.Math.min;
import static lol.nebula.module.player.FreeCamera.CAMERA_ENTITY_ID;

/**
 * @author aesthetical
 * @since 04/29/23
 */
public class KillAura extends Module {
    private final Setting<Mode> mode = new Setting<>(Mode.SINGLE, "Mode");
    private final Setting<Priority> priority = new Setting<>(Priority.DISTANCE, "Priority");

    private final Setting<Boolean> rotate = new Setting<>(true, "Rotate");

    private final Setting<Double> range = new Setting<>(4.5, 0.01, 1.0, 6.0, "Range");
    private final Setting<Double> wallRange = new Setting<>(2.0, 0.01, 1.0, 6.0, "Wall Range");

    private final Setting<Integer> minCPS = new Setting<>(8, 0, 20, "Min CPS");
    private final Setting<Integer> maxCPS = new Setting<>(14, 0, 20, "Max CPS");

    private final Setting<RequiredWeapon> weapon = new Setting<>(RequiredWeapon.SWORD, "Required");
    private final Setting<Boolean> autoBlock = new Setting<>(true, "Auto Block");

    private final Timer timer = new Timer();
    private EntityLivingBase target;
    private boolean blocking;

    public KillAura() {
        super("Kill Aura", "Attacks entities around you", ModuleCategory.COMBAT);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        target = null;

        if (blocking) {
            blocking = false;
            mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(
                    5, 0, 0, 0, 255));
        }
    }

    @Override
    public String getMetadata() {
        return Setting.formatEnumName(mode.getValue());
    }

    @Listener
    public void onWalkingUpdate(EventWalkingUpdate event) {

        if (!isValidEntity(target) || mode.getValue() == Mode.SWITCH) {
            target = (EntityLivingBase) mc.theWorld.loadedEntityList
                    .stream()
                    .filter((e) -> e != null && e instanceof EntityLivingBase && isValidEntity((EntityLivingBase) e))
                    .min(Comparator.comparingDouble((x) -> {
                        double compared = 0.0;
                        switch (priority.getValue()) {
                            case DISTANCE:
                                compared = mc.thePlayer.getDistanceSqToEntity((Entity) x);
                                break;

                            case HEALTH:
                                compared = ((EntityLivingBase) x).getHealth();
                                break;

                            case ARMOR:
                                compared = ((EntityLivingBase) x).getTotalArmorValue();
                                break;
                        }
                        return compared;
                    }))
                    .orElse(null);
        }

        if (target == null) {

            if (autoBlock.getValue() && blocking) {
                blocking = false;
                mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(
                        5, 0, 0, 0, 255));
            }

            return;
        }

        ItemStack itemStack = mc.thePlayer.getHeldItem();
        switch (weapon.getValue()) {
            case NONE:
                break;

            case SWITCH:
                float swordDamage = 0.0f;
                int slot = -1;

                for (int i = 0; i < 9; ++i) {
                    ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
                    if (stack != null && stack.getItem() instanceof ItemSword) {
                        ItemSword sword = (ItemSword) stack.getItem();

                        float damage = sword.field_150934_a;

                        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack) * 1.1f;
                        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack) * 0.8f;
                        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack) * 0.5f;

                        if (damage > swordDamage) {
                            swordDamage = damage;
                            slot = i;
                        }
                    }
                }

                if (slot == -1) return;
                mc.thePlayer.inventory.currentItem = slot;
                break;

            case SWORD:
                if (itemStack == null || !(itemStack.getItem() instanceof ItemSword)) return;
                break;
        }

        boolean holdingSword = weapon.getValue() == RequiredWeapon.SWORD
                || (itemStack != null && itemStack.getItem() instanceof ItemSword);

        if (rotate.getValue()) {
            RotationUtils.setRotations(event, RotationUtils.rotateTo(target));
        }

        if (autoBlock.getValue() && holdingSword) {
            if (blocking) {
                mc.playerController.sendUseItemClient(mc.thePlayer, mc.theWorld, itemStack);
            }
        } else {
            if (blocking) {
                blocking = false;
                mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(
                        5, 0, 0, 0, 255));
            }
        }

        if (event.getStage() == EventStage.PRE) {

            if (timer.ms((long) getAttackDelay(), false)) {

                if (autoBlock.getValue() && blocking) {
                    blocking = false;
                    mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(
                            5, 0, 0, 0, 255));
                }

                mc.thePlayer.swingItem();
                mc.playerController.attackEntity(mc.thePlayer, target);
            }
        } else {
            if (!blocking && holdingSword) {
                blocking = true;
                mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(
                        -1, -1, -1, 255,
                        itemStack, 0.0F, 0.0F, 0.0F));
            }
        }
    }

    private boolean isValidEntity(EntityLivingBase target) {

        if (target == null || target.isDead || target.getHealth() <= 0.0f || target.equals(mc.thePlayer)) {
            return false;
        }

        // dont attack friends
        if (target instanceof EntityPlayer && Nebula.getInstance().getFriends().isFriend((EntityPlayer) target)) {
            return false;
        }

        // dont attack our camera entity
        if (target.getEntityId() == CAMERA_ENTITY_ID) return false;

        if (target instanceof EntityPigZombie) return false;

        double distance = mc.thePlayer.getDistanceSqToEntity(target);
        if (!mc.thePlayer.canEntityBeSeen(target) && distance > wallRange.getValue() * wallRange.getValue()) {
            return false;
        }

        return distance <= range.getValue() * range.getValue();
    }

    private double getAttackDelay() {
        double min = minCPS.getValue() * MathUtils.random(0.0, 1.0);
        double max = maxCPS.getValue() * MathUtils.random(0.0, 1.0);
        return 1000.0 / (max / min * MathUtils.random(min, max));
    }

    public boolean isBlocking() {
        return blocking;
    }

    public EntityLivingBase getTarget() {
        return target;
    }

    public enum Mode {
        SINGLE, SWITCH
    }

    public enum Priority {
        DISTANCE, HEALTH, ARMOR
    }

    public enum RequiredWeapon {
        NONE, SWITCH, SWORD
    }
}
