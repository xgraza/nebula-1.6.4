package wtf.nebula.client.impl.module.miscellaneous;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.block.Block;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.MovingObjectPosition;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.impl.event.impl.client.EventTick;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;
import wtf.nebula.client.impl.module.movement.AutoWalk;
import wtf.nebula.client.utils.world.WorldUtils;

public class AutoEat extends ToggleableModule {
    private final Property<Integer> hunger = new Property<>(6, 0, 20, "Hunger", "level");
    private final Property<Boolean> gapple = new Property<>(true, "GApple", "goldenapple");

    private int oldSlot = -1;
    private boolean sneaking = false;

    public static boolean pause = false;

    public AutoEat() {
        super("Auto Eat", new String[]{"autoeat", "eater"}, ModuleCategory.MISCELLANEOUS);
        offerProperties(hunger, gapple);
    }

    @Override
    protected void onDisable() {
        super.onDisable();

        if (oldSlot != -1) {
            mc.thePlayer.inventory.currentItem = oldSlot;
            oldSlot = -1;
        }

        if (pause) {
            mc.gameSettings.keyBindUseItem.pressed = false;
        }

        if (sneaking) {
            mc.gameSettings.keyBindSneak.pressed = false;
            sneaking = false;
        }

        pause = false;
        AutoWalk.pause = false;
    }

    @EventListener
    public void onTick(EventTick event) {
        int slot = -1;
        int amount = -1;

        if (gapple.getValue()) {

            if (!mc.thePlayer.isPotionActive(Potion.regeneration) && !mc.thePlayer.isPotionActive(Potion.fireResistance) && !mc.thePlayer.isPotionActive(Potion.resistance)) {
                for (int i = 0; i < 9; ++i) {
                    ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
                    if (stack != null && stack.getItem() instanceof ItemAppleGold) {

                        if (slot == -1) {
                            slot = i;
                            if (stack.hasEffect()) {
                                break;
                            }
                        } else {
                            ItemStack s = mc.thePlayer.inventory.getStackInSlot(slot);
                            if (!s.hasEffect() && stack.hasEffect()) {
                                slot = i;
                                break;
                            }
                        }
                    }


                }
            }
        }

        if (slot == -1) {

            for (int i = 0; i < 9; ++i) {
                ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
                if (stack == null) {
                    return;
                }

                if (stack.getItem() instanceof ItemFood) {
                    ItemFood itemFood = (ItemFood) stack.getItem();

                    if (itemFood.healAmount > amount) {
                        amount = itemFood.healAmount;
                        slot = i;
                    }
                }
            }
        }

        if (slot == -1) {
            pause = false;
            return;
        }

        if (shouldEat()) {

            if (mc.objectMouseOver != null) {
                MovingObjectPosition result = mc.objectMouseOver;
                if (result.typeOfHit.equals(MovingObjectPosition.MovingObjectType.BLOCK)) {

                    Block at = WorldUtils.getBlock(result.blockX, result.blockY, result.blockZ);
                    sneaking = WorldUtils.SNEAK_BLOCKS.contains(at);
                }
            } else {
                sneaking = false;
            }

            mc.gameSettings.keyBindSneak.pressed = sneaking;

            AutoWalk.pause = true;
            pause = true;

            if (oldSlot == -1) {
                oldSlot = mc.thePlayer.inventory.currentItem;
            }

            mc.thePlayer.inventory.currentItem = slot;
            mc.gameSettings.keyBindUseItem.pressed = true;
        } else {
            pause = false;
            AutoWalk.pause = false;

            if (oldSlot != -1) {
                mc.thePlayer.inventory.currentItem = oldSlot;
                oldSlot = -1;
                mc.gameSettings.keyBindUseItem.pressed = false;
            }

            if (sneaking) {
                sneaking = false;
                mc.gameSettings.keyBindSneak.pressed = false;
            }
        }
    }

    private boolean shouldEat() {
        if (gapple.getValue()) {

            if (!mc.thePlayer.isPotionActive(Potion.regeneration) && !mc.thePlayer.isPotionActive(Potion.fireResistance) && !mc.thePlayer.isPotionActive(Potion.resistance)) {
                return true;
            }
        }

        return mc.thePlayer.getFoodStats().getFoodLevel() <= hunger.getValue();
    }

    public enum Mode {
        GAPPLE, FOOD
    }
}
