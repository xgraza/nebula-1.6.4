package wtf.nebula.client.impl.module.miscellaneous;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.network.play.client.C01PacketChatMessage;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.impl.event.impl.network.EventPlayerConnection;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;
import wtf.nebula.client.utils.client.MathUtils;

public class Announcer extends ToggleableModule {
    private static final String[] GREETINGS = { "Hello, %username%!", "Hey there, %username%!", "What's up, %username%?", "Welcome, %username%!", "Great to see you, %username%!" };
    private static final String[] GOODBYES = { "Goodbye, %username%!", "Hope to see you soon, %username%!", "Next time, %username%!", "Nice seeing you, %username%!" };

    private final Property<Boolean> greentext = new Property<>(true, "Green Text", "greentext", "arrow", ">");
    private final Property<Boolean> join = new Property<>(true, "Join", "playerjoin");
    private final Property<Boolean> leave = new Property<>(true, "Leave", "playerleave");

    public Announcer() {
        super("Announcer", new String[]{"announcer", "annoyer"}, ModuleCategory.MISCELLANEOUS);
        offerProperties(greentext, join, leave);
    }

    @EventListener
    public void onPlayerConnection(EventPlayerConnection event) {
        EventPlayerConnection.Action action = event.getAction();

        if (action.equals(EventPlayerConnection.Action.JOIN) && !join.getValue() || action.equals(EventPlayerConnection.Action.LEAVE) && !leave.getValue()) {
            return;
        }

        // don't greet / farewell urself LOL
        if (event.getUsername().equals(mc.session.getUsername())) {
            return;
        }

        if (mc.thePlayer.ticksExisted <= 250) {
            return;
        }

        String[] arr = event.getAction().equals(EventPlayerConnection.Action.JOIN) ? GREETINGS : GOODBYES;
        String random = MathUtils.randomElement(arr);

        if (random != null) {
            random = random.replaceAll("%username%", event.getUsername());
            if (greentext.getValue()) {
                random = "> " + random;
            }

            mc.thePlayer.sendQueue.addToSendQueueSilent(new C01PacketChatMessage(random));
        }
    }
}
