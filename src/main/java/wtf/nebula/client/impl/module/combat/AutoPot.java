package wtf.nebula.client.impl.module.combat;

import com.google.common.collect.Lists;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.core.Nebula;
import wtf.nebula.client.impl.event.base.Era;
import wtf.nebula.client.impl.event.impl.move.EventMotionUpdate;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;
import wtf.nebula.client.impl.module.world.Scaffold;
import wtf.nebula.client.utils.client.Timer;
import wtf.nebula.client.utils.player.MoveUtils;
import wtf.nebula.client.utils.player.PlayerUtils;
import wtf.nebula.client.utils.player.RotationUtils;
import wtf.nebula.client.utils.world.RayTraceUtils;

import java.util.List;

public class AutoPot extends ToggleableModule {

    public static final List<Potion> VALID_POTIONS = Lists.newArrayList(
            Potion.fireResistance, Potion.heal, Potion.moveSpeed,
            Potion.digSpeed, Potion.invisibility, Potion.damageBoost, Potion.regeneration,
            Potion.resistance, Potion.waterBreathing, Potion.nightVision, Potion.jump
    );

    private final Property<Double> delay = new Property<>(1000.0, 0.0, 5000.0, "Delay", "dl");
    private final Property<Boolean> rotate = new Property<>(false, "Rotate", "rot", "face");
    private final Property<Boolean> raytrace = new Property<>(true, "Raytrace", "raytrace", "raycast")
            .setVisibility(rotate::getValue);

    private final Timer timer = new Timer();

    public AutoPot() {
        super("Auto Pot", new String[]{"autopot", "potter"}, ModuleCategory.COMBAT);
        offerProperties(delay, rotate, raytrace);
    }

    @EventListener
    public void onMotionUpdate(EventMotionUpdate event) {

        // do not try to pot while attacking / scaffolding
        if (Aura.attacking || Nebula.getInstance().getModuleManager().getModule(Scaffold.class).isRunning()) {
            return;
        }

        if (PlayerUtils.isOverLiquid() || mc.thePlayer.isInWater() || mc.currentScreen != null) {
            return;
        }

        int slot = getBestPotSlot();
        if (slot == -1) {
            timer.resetTime();
            return;
        }

        if (timer.hasPassed(delay.getValue().longValue() / 2L, false) && rotate.getValue()) {
            float[] angles = { mc.thePlayer.rotationYaw, 90.0f };

            if (MoveUtils.isMoving()) {

                double x = mc.thePlayer.posX + (mc.thePlayer.motionX * 26.0);
                double y = mc.thePlayer.boundingBox.minY - 3.6;
                double z = mc.thePlayer.posZ + (mc.thePlayer.motionZ * 26.0);

                float[] a = RotationUtils.calcAngles(new Vec3(Vec3.fakePool, x, y, z));
                angles[1] = a[1];
            }

            if (raytrace.getValue()) {
                MovingObjectPosition result = RayTraceUtils.rayTrace(angles[0], angles[1]);
                if (result == null || !result.typeOfHit.equals(MovingObjectPosition.MovingObjectType.BLOCK)) {
                    return;
                }
            }

            Nebula.getInstance().getRotationManager().setRotations(angles);
            event.yaw = angles[0];
            event.pitch = angles[1];
        }

        if (timer.hasPassed(delay.getValue().longValue(), false)) {

            PlayerUtils.stopUseCurrentItem();

            if (event.getEra().equals(Era.POST)) {
                timer.resetTime();

                mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(slot));
                mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(-1, -1, -1, 255, mc.thePlayer.inventory.getStackInSlot(slot), 0.0F, 0.0F, 0.0F));
                mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
            }

        }
    }

    public int getBestPotSlot() {
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
            if (stack != null && stack.getItem() instanceof ItemPotion) {
                ItemPotion itemPotion = (ItemPotion) stack.getItem();
                if (!ItemPotion.isSplash(stack.getItemDamage())) {
                    continue;
                }

                List<PotionEffect> effectList = itemPotion.getEffects(stack);
                for (PotionEffect effect : effectList) {
                    Potion potion;
                    try {
                        potion = Potion.potionTypes[effect.getPotionID()];
                    } catch (IndexOutOfBoundsException e) {
                        continue;
                    }

                    if (VALID_POTIONS.contains(potion) && !mc.thePlayer.isPotionActive(potion)) {
                        return i;
                    }
                }
            }
        }

        return -1;
    }

}
