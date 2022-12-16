package wtf.nebula.client.impl.module.miscellaneous;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.util.EnumChatFormatting;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.impl.event.base.Era;
import wtf.nebula.client.impl.event.impl.client.EventTick;
import wtf.nebula.client.impl.event.impl.network.EventPacket;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;

public class AutoRespawn extends ToggleableModule {
    private final Property<Boolean> logDeath = new Property<>(false, "Log Death", "logdeath");

    private boolean sentDeathCoords = false;

    public AutoRespawn() {
        super("Auto Respawn", new String[]{"autorespawn", "respawn"}, ModuleCategory.MISCELLANEOUS);
        offerProperties(logDeath);
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        sentDeathCoords = false;
    }

    @EventListener
    public void onTick(EventTick event) {
        if (mc.thePlayer.isDead || mc.thePlayer.getHealth() <= 0.0f) {
            mc.thePlayer.sendQueue.addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.PERFORM_RESPAWN));
        }
    }

    @EventListener
    public void onPacket(EventPacket event) {
        if (event.getEra().equals(Era.PRE)) {

            if (event.getPacket() instanceof C16PacketClientStatus) {
                C16PacketClientStatus packet = event.getPacket();
                if (packet.func_149435_c().equals(C16PacketClientStatus.EnumState.PERFORM_RESPAWN)) {

                    if (mc.thePlayer.isDead || mc.thePlayer.getHealth() <= 0.0f) {
                        if (logDeath.getValue() && !sentDeathCoords) {
                            sentDeathCoords = true;
                            print("You died at X: "
                                    + EnumChatFormatting.RED + String.format("%.2f", mc.thePlayer.posX)
                                    + EnumChatFormatting.GRAY + ", Y: "
                                    + EnumChatFormatting.RED + String.format("%.2f", mc.thePlayer.boundingBox.minY)
                                    + EnumChatFormatting.GRAY + ", Z: "
                                    + EnumChatFormatting.RED + String.format("%.2f", mc.thePlayer.posZ)
                                    + EnumChatFormatting.GRAY + ".", -1337420566);
                        }
                    }
                }
            } else if (event.getPacket() instanceof S07PacketRespawn) {
                sentDeathCoords = false;
            }
        }
    }
}
