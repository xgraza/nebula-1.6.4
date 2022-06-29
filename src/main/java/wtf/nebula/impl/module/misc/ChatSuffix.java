package wtf.nebula.impl.module.misc;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.src.Packet3Chat;
import org.lwjgl.input.Keyboard;
import wtf.nebula.event.PacketEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;
import wtf.nebula.repository.impl.CommandRepository;

public class ChatSuffix extends Module {
    private static final String NEBULA_SUFFIX = "\u0274\u1d07\u0299\u1d1c\u029f\u1d00";

    public ChatSuffix() {
        super("ChatSuffix", ModuleCategory.MISC);
        setBind(Keyboard.KEY_B);
    }

    @EventListener
    public void onPacketSend(PacketEvent.Send event) {

        if (event.getPacket() instanceof Packet3Chat) {
            Packet3Chat packet = event.getPacket();

            String message = packet.message;
            if (message.startsWith("/") || message.startsWith(CommandRepository.get().getPrefix())) {
                return;
            }

            packet.message += " \u23D0 " + NEBULA_SUFFIX;
        }
    }
}
