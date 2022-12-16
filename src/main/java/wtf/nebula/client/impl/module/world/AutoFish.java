package wtf.nebula.client.impl.module.world;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.impl.event.impl.client.EventTick;
import wtf.nebula.client.impl.event.impl.network.EventPacket;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;
import wtf.nebula.client.utils.client.Timer;
import wtf.nebula.client.utils.player.PlayerUtils;

public class AutoFish extends ToggleableModule {
    private boolean canFish = false;
    private boolean caught = false;

    private boolean cast = false;
    private final Timer castTimer = new Timer();

    private final Property<Boolean> autoSwitch = new Property<>(false, "Auto Switch", "autoswitch", "switch");
    private final Property<Boolean> autoCast = new Property<>(true, "Auto Cast", "autocast", "cast");
    private final Property<Integer> delay = new Property<>(5, 0, 100, "Delay", "castdelay", "d");

    public AutoFish() {
        super("Auto Fish", new String[]{"autofish", "autofisher", "fisher"}, ModuleCategory.WORLD);
        offerProperties(autoSwitch, autoCast, delay);
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        canFish = false;
        caught = false;
        cast = false;
    }

    @EventListener
    public void onTick(EventTick event) {
        canFish = PlayerUtils.isHolding(ItemFishingRod.class);
        if (canFish) {
            if (mc.thePlayer.fishEntity == null) {

                if (autoCast.getValue() && castTimer.hasPassed(1000L, true)) {

                    // TODO: face towards nearby water
                    castRod();
                }

                return;
            }

            if (caught) {
                cast = true;
                castRod();
                castTimer.resetTime();
                caught = false;
            } else {
                if (cast && castTimer.hasPassed(delay.getValue() * 50L, false)) {
                    cast = false;

                    if (autoCast.getValue()) {
                        castRod();
                    }
                }
            }
        } else {
            if (!autoSwitch.getValue()) {
                return;
            }

            int slot = -1;
            float score = 0.0f;

            for (int i = 0; i < 9; ++i) {
                ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
                if (stack == null || !(stack.getItem() instanceof ItemFishingRod)) {
                    continue;
                }

                float curScore = 0.1f;

                curScore += EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, stack) * 0.0075f;
                curScore += EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack) / 100.0f;

                // these might not be 100% correct
                curScore += EnchantmentHelper.func_151386_g(mc.thePlayer) * 0.025f;
                curScore += EnchantmentHelper.func_151387_h(mc.thePlayer) * 0.025f;

                // TODO: factor durability

                if (curScore > score) {
                    slot = i;
                    score = curScore;
                }
            }

            if (slot != -1) {
                mc.thePlayer.inventory.currentItem = slot;
            }
        }
    }

    @EventListener
    public void onPacket(EventPacket event) {
        if (event.getPacket() instanceof S29PacketSoundEffect && canFish) {
            S29PacketSoundEffect packet = event.getPacket();
            if (packet.func_149212_c().contains("random.splash")) {
                Entity entity = mc.thePlayer.fishEntity;
                if (entity != null) {
                    double dist = entity.getDistance(packet.getX(), packet.getY(), packet.getZ());
                    if (dist <= 2.0) {
                        caught = true;
                    }
                }
            }
        }
    }

    private void castRod() {
        if (PlayerUtils.isHolding(ItemFishingRod.class)) {

            // Minecraft#func_147121_ag
            if (mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem())) {
                mc.entityRenderer.itemRenderer.resetEquippedProgress2();
            }
        }
    }
}
