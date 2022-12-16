package wtf.nebula.client.impl.module.world;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.network.play.server.S28PacketEffect;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.impl.event.base.Era;
import wtf.nebula.client.impl.event.impl.input.EventKeyInput;
import wtf.nebula.client.impl.event.impl.network.EventPacket;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;

public class SoundLocator extends ToggleableModule {
    private final Property<Boolean> thunder = new Property<>(true, "Thunder", "lightning");
    private final Property<Boolean> wither = new Property<>(true, "Wither", "witherspawn");

    public SoundLocator() {
        super("Sound Locator", new String[]{"soundlocator", "thunderlocator", "thunderhack"}, ModuleCategory.WORLD);
        offerProperties(thunder, wither);
    }

    @EventListener
    public void onKeyInput(EventKeyInput event) {
        if (event.getKeyCode() == Keyboard.KEY_DELETE) {
            mc.theWorld.playBroadcastSound(1013, (int) mc.thePlayer.posX, (int) mc.thePlayer.posY, (int) mc.thePlayer.posZ, 0);
        }
    }

    @EventListener
    public void onPacket(EventPacket event) {
        // S28PacketEffect

        if (event.getEra().equals(Era.PRE)) {

            if (event.getPacket() instanceof S29PacketSoundEffect) {
                S29PacketSoundEffect packet = event.getPacket();

                if (packet.func_149212_c().equals("ambient.weather.thunder") && thunder.getValue()) {
                    double x = packet.getX();
                    double y = packet.getY();
                    double z = packet.getZ();

                    print("Thunder sounded at X: "
                            + EnumChatFormatting.RED + x
                            + EnumChatFormatting.GRAY + ", Y: "
                            + EnumChatFormatting.RED + y
                            + EnumChatFormatting.GRAY + ", Z: "
                            + EnumChatFormatting.RED + z
                            + EnumChatFormatting.GRAY + ".", packet.hashCode());
                }
            } else if (event.getPacket() instanceof S28PacketEffect) {
                S28PacketEffect packet = event.getPacket();

                if (packet.getEffectId() == 1013 && wither.getValue()) {
                    double x = packet.getX();
                    double y = packet.getY();
                    double z = packet.getZ();

                    print("Wither spawned at X: "
                            + EnumChatFormatting.RED + x
                            + EnumChatFormatting.GRAY + ", Y: "
                            + EnumChatFormatting.RED + y
                            + EnumChatFormatting.GRAY + ", Z: "
                            + EnumChatFormatting.RED + z
                            + EnumChatFormatting.GRAY + ".", packet.hashCode());
                }
            }
        }
    }
}
