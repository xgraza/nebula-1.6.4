package wtf.nebula.client.utils.player;

import net.minecraft.item.Item;
import net.minecraft.util.Vec3;
import wtf.nebula.client.utils.client.Wrapper;

public class PlayerUtils implements Wrapper {
    public static Vec3 getPosUnder() {
        return new Vec3(
                Vec3.fakePool,
                Math.floor(mc.thePlayer.posX),
                mc.thePlayer.posY - 2,
                Math.floor(mc.thePlayer.posZ));
    }

    public static boolean isHolding(Item item) {
        return mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem().equals(item);
    }

    public static boolean isHolding(Class<? extends Item> clazz) {
        return mc.thePlayer.getHeldItem() != null && clazz.isInstance(mc.thePlayer.getHeldItem().getItem());
    }
}
