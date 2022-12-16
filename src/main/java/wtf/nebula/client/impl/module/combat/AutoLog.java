package wtf.nebula.client.impl.module.combat;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.core.Nebula;
import wtf.nebula.client.impl.command.impl.FakePlayer;
import wtf.nebula.client.impl.event.impl.client.EventTick;
import wtf.nebula.client.impl.event.impl.world.EventEntitySpawn;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;
import wtf.nebula.client.impl.module.active.Notifications;
import wtf.nebula.client.impl.module.world.FreeCamera;
import wtf.nebula.client.utils.io.SystemTrayUtils;

public class AutoLog extends ToggleableModule {
    private final Property<Float> health = new Property<>(10.0f, 1.0f, 20.0f, "Health", "minhealth");
    private final Property<Boolean> visualRange = new Property<>(true, "Visual Range", "visualrange", "playerspawn");
    private final Property<Boolean> autoDisable = new Property<>(true, "Auto Disable", "autodisable", "disable");

    public AutoLog() {
        super("Auto Log", new String[]{"autolog", "antideath"}, ModuleCategory.COMBAT);
        offerProperties(health, visualRange, autoDisable);
    }

    @Override
    public String getTag() {
        return String.valueOf(health.getValue());
    }

    @EventListener
    public void onTick(EventTick event) {
        if (mc.thePlayer.getHealth() < health.getValue()) {

            if (mc.thePlayer.getHealth() <= 0.0f || mc.thePlayer.isDead) {
                disconnect("You were killed at X: "
                        + EnumChatFormatting.RED + String.format("%.2f", mc.thePlayer.posX)
                        + EnumChatFormatting.GRAY + ", Y: "
                        + EnumChatFormatting.RED + String.format("%.2f", mc.thePlayer.boundingBox.minY)
                        + EnumChatFormatting.GRAY + ", Z: "
                        + EnumChatFormatting.RED + String.format("%.2f", mc.thePlayer.posZ)
                        + EnumChatFormatting.GRAY + ".");
            } else {
                disconnect("At " + EnumChatFormatting.RED + String.format("%.1f", mc.thePlayer.getHealth()) + EnumChatFormatting.GRAY + ".");
            }

            if (Notifications.shouldUseTray()) {

                if (mc.thePlayer.getHealth() <= 0.0f || mc.thePlayer.isDead) {
                    SystemTrayUtils.showMessage("AutoLog logged you out because you died");
                } else {
                    SystemTrayUtils.showMessage("AutoLog logged you out because you were at " + mc.thePlayer.getHealth() + " health!");
                }
            }
        }
    }

    @EventListener
    public void onEntitySpawn(EventEntitySpawn event) {
        if (event.getEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntity();
            if (player.equals(mc.thePlayer)) {
                return;
            }

            // don't logout on our camera guy
            if (Nebula.getInstance().getModuleManager().getModule(FreeCamera.class).isRunning() && player.getEntityId() == -133769420) {
                return;
            }

            // do not log upon spawning a fake player
            if (FakePlayer.fakePlayerId != -1 && FakePlayer.fakePlayerId == player.getEntityId()) {
                return;
            }

            if (visualRange.getValue() && !Nebula.getInstance().getFriendManager().isFriend(player)) {
                disconnect("Player " + EnumChatFormatting.RED + player.getCommandSenderName() + EnumChatFormatting.GRAY + " came into your visual range!");

                if (Notifications.shouldUseTray()) {
                    SystemTrayUtils.showMessage("AutoLog logged you out because \"" + player.getCommandSenderName() + "\" came into your visual range!");
                }
            }
        }
    }

    private void disconnect(String reason) {
        mc.thePlayer.sendQueue.getNetworkManager().closeChannel(
                new ChatComponentText(
                        EnumChatFormatting.RED + "[AutoLog] " + EnumChatFormatting.GRAY + reason));

        if (autoDisable.getValue()) {
            setRunning(false);
        }
    }
}
