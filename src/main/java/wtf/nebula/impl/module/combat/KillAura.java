package wtf.nebula.impl.module.combat;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import wtf.nebula.event.MotionUpdateEvent;
import wtf.nebula.event.MotionUpdateEvent.Era;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;
import wtf.nebula.impl.value.Value;
import wtf.nebula.repository.impl.FriendRepository;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class KillAura extends Module {
    public KillAura() {
        super("KillAura", ModuleCategory.COMBAT);
    }

    public final Value<Double> range = new Value<>("Range", 4.0, 1.0, 6.0);
    public final Value<Boolean> walls = new Value<>("Walls", true);

    public final Value<Integer> minCps = new Value<>("MinCPS", 12, 1, 20);
    public final Value<Integer> maxCps = new Value<>("MaxCPS", 16, 1, 20);

    public final Value<Boolean> onlySword = new Value<>("OnlySword", true);
    public final Value<Boolean> autoBlock = new Value<>("AutoBlock", true);

    // entities
    public final Value<Boolean> players = new Value<>("Players", true);
    public final Value<Boolean> animals = new Value<>("Animals", true);
    public final Value<Boolean> tamed = new Value<>("Tamed", false);
    public final Value<Boolean> mobs = new Value<>("Mobs", true);

    private long lastAttack = 0L;
    private boolean blocking = false;

    public EntityLivingBase target = null;

    @Override
    protected void onDeactivated() {
        super.onDeactivated();

        if (!nullCheck() && blocking) {
            blocking = false;
            mc.thePlayer.stopUsingItem();
        }

        target = null;
        AutoGG.target = null;
    }

    @EventListener
    public void onMotionUpdate(MotionUpdateEvent event) {
        if (target == null
                || target.isDead
                || target.getHealth() <= 0.0f
                || mc.thePlayer.getDistanceToEntity(target) > range.getValue()) {

            target = null;

            double r = 0.0;

            for (Object obj : new ArrayList<>(mc.theWorld.loadedEntityList)) {
                if (!(obj instanceof EntityLivingBase)) {
                    continue;
                }

                EntityLivingBase base = (EntityLivingBase) obj;

                if (base.equals(mc.thePlayer) || base.isDead || base.getHealth() <= 0.0f) {
                    continue;
                }

                if (!players.getValue() && base instanceof EntityPlayer) {
                    continue;
                }

                if (animals.getValue()) {
                    if (!tamed.getValue() && base instanceof EntityTameable) {
                        EntityTameable animal = (EntityTameable) base;
                        if (animal.isTamed()) {
                            continue;
                        }
                    }
                } else {
                    if (base instanceof EntityAnimal || base instanceof EntityAmbientCreature || base instanceof EntityWaterMob) {
                        continue;
                    }
                }

                if (!mobs.getValue() && base instanceof EntityMob) {
                    continue;
                }

                // do not target friends
                if (base instanceof EntityPlayer && FriendRepository.get().isFriend((EntityPlayer) base)) {
                    continue;
                }

                // FUCK PIGLINS!!!!
                if (base instanceof EntityPigZombie) {
                    continue;
                }

                if (!mc.thePlayer.canEntityBeSeen(base) && !walls.getValue()) {
                    continue;
                }

                double entityRange = mc.thePlayer.getDistanceSqToEntity(base);
                if (entityRange > range.getValue() * range.getValue()) {
                    continue;
                }

                if (r == 0.0 || r > entityRange) {
                    r = entityRange;
                    target = base;
                }
            }
        }

        if (target == null) {
            return;
        }

        if (target instanceof EntityPlayer) {
            AutoGG.target = (EntityPlayer) target;
        }

        boolean holdingSword = mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
        if (onlySword.getValue() && !holdingSword) {
            return;
        }

        if (holdingSword && autoBlock.getValue()) {
            if (event.getEra().equals(Era.PRE)) {
                blocking = false;
                mc.playerController.unblockSilent();
            }

            else {
                blocking = true;
                mc.playerController.blockSword();
            }
        }

        if (System.currentTimeMillis() - lastAttack >= 1000L / randomInBounds(minCps.getValue(), maxCps.getValue())) {
            lastAttack = System.currentTimeMillis();

            mc.thePlayer.swingItem();
            mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));

            // fun little particle
            mc.thePlayer.onEnchantmentCritical(target);
        }
    }

    private int randomInBounds(int min, int max) {
        return ThreadLocalRandom.current().nextInt((max + 1) - min) + min;
    }
}
