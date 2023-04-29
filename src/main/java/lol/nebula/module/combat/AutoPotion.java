package lol.nebula.module.combat;

import com.google.common.collect.Lists;
import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.EventStage;
import lol.nebula.listener.events.entity.EventWalkingUpdate;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.setting.Setting;
import lol.nebula.util.math.RotationUtils;
import lol.nebula.util.math.timing.Timer;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import java.util.List;

/**
 * @author aesthetical
 * @since 04/29/23
 */
public class AutoPotion extends Module {

    /**
     * A list of valid potions
     */
    private static final List<Potion> VALID_POTIONS = Lists.newArrayList(
            Potion.fireResistance, Potion.heal, Potion.moveSpeed,
            Potion.digSpeed, Potion.invisibility, Potion.damageBoost, Potion.regeneration,
            Potion.resistance, Potion.waterBreathing, Potion.nightVision, Potion.jump
    );

    private final Setting<Double> delay = new Setting<>(0.1, 0.01, 0.0, 5.0, "Delay");
    private final Setting<Float> health = new Setting<>(14.0f, 1.0f, 19.0f, "Health");
    private final Setting<Boolean> rotate = new Setting<>(true, "Rotate");

    private final Timer timer = new Timer();
    private int potStage = -1;

    public AutoPotion() {
        super("Auto Potion", "Automatically throws splash potions at your feet", ModuleCategory.COMBAT);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if (mc.thePlayer != null) {
            // sync hotbar slot
            mc.thePlayer.sendQueue.addToSendQueue(
                    new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
        }

        potStage = -1;
    }

    @Listener
    public void onWalkingUpdate(EventWalkingUpdate event) {
        int slot = getBestPotSlot();
        if (slot == -1) {
            if (potStage != -1) {
                mc.thePlayer.sendQueue.addToSendQueue(
                        new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
            }

            potStage = -1;
            return;
        }

        if (event.getStage() == EventStage.POST) {

            if (rotate.getValue()) {
                RotationUtils.setRotations(event, new float[] { mc.thePlayer.rotationYaw, 90.0f });
            }

            if (timer.ms((long) (delay.getValue() * 1000.0), true)) {
                if (potStage <= 0) {
                    potStage = 1;
                    mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(slot));
                } else if (potStage == 1) {
                    potStage = 2;
                    mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(
                            -1, -1, -1, 255,
                            mc.thePlayer.inventory.getStackInSlot(slot),
                            0.0F, 0.0F, 0.0F));
                } else if (potStage == 2) {
                    mc.thePlayer.sendQueue.addToSendQueue(
                            new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                    potStage = -1;
                }

            }
        }
    }

    private int getBestPotSlot() {
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
            if (stack != null && stack.getItem() instanceof ItemPotion) {
                ItemPotion itemPotion = (ItemPotion) stack.getItem();
                if (!ItemPotion.isSplash(stack.getItemDamage())) {
                    continue;
                }

                List<PotionEffect> effectList = itemPotion.getEffects(stack);
                for (PotionEffect effect : effectList) {
                    Potion potion = Potion.potionTypes[effect.getPotionID()];

                    // if our health is low and the pot is an instant healing pot, we'll use this
                    if (mc.thePlayer.getHealth() <= health.getValue() && potion.id == Potion.heal.id) return i;

                    // if the potion effect is valid and the potion is not on ourselves, and it is not an instant healing potion
                    if (VALID_POTIONS.contains(potion) && !mc.thePlayer.isPotionActive(potion) && !potion.isInstant()) return i;
                }
            }
        }

        return -1;
    }
}
