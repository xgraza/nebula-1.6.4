package wtf.nebula.impl.module.misc;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.src.Packet3Chat;
import wtf.nebula.event.PacketEvent;
import wtf.nebula.event.PacketEvent.Era;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;
import wtf.nebula.impl.value.Value;
import wtf.nebula.repository.impl.CommandRepository;

public class ChatModifier extends Module {
    private static final String NEBULA_SUFFIX = "\u0274\u1d07\u0299\u1d1c\u029f\u1d00";

    public ChatModifier() {
        super("ChatModifier", ModuleCategory.MISC);
    }

    public final Value<Boolean> greenText = new Value<>("GreenText", true);
    public final Value<Boolean> suffix = new Value<>("Suffix", true);

    @EventListener
    public void onPacketSend(PacketEvent.Send event) {

        if (event.getPacket() instanceof Packet3Chat && event.getEra().equals(Era.PRE)) {
            Packet3Chat packet = event.getPacket();

            String message = packet.message;
            if (message.startsWith("/") || message.startsWith(CommandRepository.get().getPrefix())) {
                return;
            }

            if (greenText.getValue()) {
                message = "> " + message;
            }

            if (suffix.getValue()) {
                message += " \u23D0 " + NEBULA_SUFFIX;
            }

            packet.message = message;
        }
    }
}
