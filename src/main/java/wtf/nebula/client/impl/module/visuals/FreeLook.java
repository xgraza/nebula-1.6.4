package wtf.nebula.client.impl.module.visuals;

import me.bush.eventbus.annotation.EventListener;
import wtf.nebula.client.impl.event.impl.player.EventPlayerTurn;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;

public class FreeLook extends ToggleableModule {
    public static boolean rotate = false;
    public static float yaw, pitch;
    public static float oldYaw, oldPitch;

    public FreeLook() {
        super("Free Look", new String[]{"freelook"}, ModuleCategory.VISUALS);
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        rotate = false;
    }

    @EventListener
    public void onTurn(EventPlayerTurn event) {

        if (mc.gameSettings.thirdPersonView != 0) {
            rotate = true;
            event.setCancelled(true);

            float var3 = FreeLook.pitch;
            float var4 = FreeLook.yaw;
            FreeLook.yaw = (float) ((double) FreeLook.yaw + (double) event.getYaw() * 0.15D);
            FreeLook.pitch = (float) ((double) FreeLook.pitch - (double) event.getPitch() * 0.15D);

            if (FreeLook.pitch < -90.0F) {
                FreeLook.pitch = -90.0F;
            }

            if (FreeLook.pitch > 90.0F) {
                FreeLook.pitch = 90.0F;
            }

            FreeLook.oldPitch += FreeLook.pitch - var3;
            FreeLook.oldYaw += FreeLook.yaw - var4;
        } else {
            rotate = false;
        }
    }
}
