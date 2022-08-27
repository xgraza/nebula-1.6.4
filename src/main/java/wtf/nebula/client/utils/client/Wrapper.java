package wtf.nebula.client.utils.client;

import net.minecraft.client.Minecraft;

public interface Wrapper {
    Minecraft mc = Minecraft.getMinecraft();

    default boolean isNull() {
        return mc.thePlayer == null || mc.theWorld == null;
    }
}
