package wtf.nebula.impl.module.movement;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.src.Packet10Flying;
import org.lwjgl.input.Keyboard;
import wtf.nebula.event.PacketEvent;
import wtf.nebula.event.PlayerSlowdownEvent;
import wtf.nebula.event.TickEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;
import wtf.nebula.impl.value.Value;

public class NoSlow extends Module {
    public NoSlow() {
        super("NoSlow", ModuleCategory.MOVEMENT);
        setBind(Keyboard.KEY_L);
    }

    public final Value<Boolean> ncp = new Value<>("NCP", true);
    public final Value<Boolean> guis = new Value<>("GUIs", true);

    @EventListener
    public void onTick(TickEvent event) {
        if (guis.getValue() && mc.currentScreen != null) {

            mc.currentScreen.allowUserInput = true;
        }
    }

    @EventListener
    public void onPlayerSlowdown(PlayerSlowdownEvent event) {
        if (mc.thePlayer.isUsingItem() && !mc.thePlayer.isRiding()) {

            event.getMovementInput().moveForward *= 5.0f;
            event.getMovementInput().moveStrafe *= 5.0f;
        }
    }

    @EventListener
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof Packet10Flying && mc.thePlayer.isBlocking() && ncp.getValue()) {
            // TODO: onMotionUpdate pre & post
        }
    }
}
