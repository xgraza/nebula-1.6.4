package lol.nebula.module.combat;

import lol.nebula.Nebula;
import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.EventStage;
import lol.nebula.listener.events.entity.EventWalkingUpdate;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.module.exploit.FastUse;
import lol.nebula.setting.Setting;
import lol.nebula.util.math.timing.Timer;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.potion.Potion;

/**
 * @author aesthetical
 * @since 04/30/23
 */
public class AutoGapple extends Module {

    private final Setting<Double> delay = new Setting<>(1.0, 0.01, 0.0, 10.0, "Delay");
    private final Setting<Float> health = new Setting<>(14.0f, 0.1f, 1.0f, 19.0f, "Health");
    private final Setting<Boolean> silent = new Setting<>(false, "Silent");

    private final Timer timer = new Timer();
    private boolean eating;
    private int ticks, slot;

    public AutoGapple() {
        super("Auto Gapple", "Automatically eats gapples for you", ModuleCategory.COMBAT);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if (eating) {
            if (silent.getValue()) {
                Nebula.getInstance().getInventory().sync();
            } else {
                if (slot != -1) mc.thePlayer.inventory.currentItem = slot;
                slot = -1;
            }
        }

        eating = false;
        ticks = 0;
    }

    @Listener
    public void onUpdateWalking(EventWalkingUpdate event) {
        if (event.getStage() != EventStage.PRE) return;

        if (mc.thePlayer.getHealth() < health.getValue() || !mc.thePlayer.isPotionActive(Potion.field_76444_x)) {

            if (!timer.ms((long) (delay.getValue() * 1000.0), false)) return;

            if (!eating) {
                int gappleSlot = -1;
                for (int i = 0; i < 9; ++i) {
                    ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
                    if (stack == null || !(stack.getItem() instanceof ItemAppleGold)) continue;

                    gappleSlot = i;
                }

                if (gappleSlot == -1) return;

                if (silent.getValue()) {
                    slot = gappleSlot;
                    Nebula.getInstance().getInventory().setSlot(slot);
                } else {
                    slot = mc.thePlayer.inventory.currentItem;
                    mc.thePlayer.inventory.currentItem = gappleSlot;
                }

                ticks = 0;
                eating = true;
            } else {

                if (silent.getValue()) {
                    if (ticks == 0) mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(
                            -1, -1, -1, 255,
                            mc.thePlayer.inventory.getStackInSlot(slot),
                            0.0F, 0.0F, 0.0F));

                    FastUse fastUse = Nebula.getInstance().getModules().get(FastUse.class);
                    if (fastUse.isToggled() && fastUse.food.getValue()) {

                        if (ticks >= fastUse.useTicks.getValue()) {
                            for (int i = 0; i < 35 - ticks; ++i) {
                                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer());
                            }

                            ticks = 35;
                        }
                    }
                } else {
                    mc.gameSettings.keyBindUseItem.pressed = true;
                }

                ++ticks;
                if (ticks >= 35) {
                    timer.resetTime();
                    eating = false;
                    ticks = 0;

                    if (silent.getValue()) {
                        Nebula.getInstance().getInventory().sync();
                    } else {
                        if (slot != -1) mc.thePlayer.inventory.currentItem = slot;
                    }

                    slot = -1;
                }

            }

        } else {

            if (eating) {
                eating = false;
                ticks = 0;

                if (silent.getValue()) {
                    Nebula.getInstance().getInventory().sync();
                } else {
                    if (slot != -1) mc.thePlayer.inventory.currentItem = slot;
                }

                slot = -1;
            }
        }
    }
}
