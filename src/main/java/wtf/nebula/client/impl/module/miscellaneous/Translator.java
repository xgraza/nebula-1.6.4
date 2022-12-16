package wtf.nebula.client.impl.module.miscellaneous;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.event.HoverEvent;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import org.lwjgl.input.Mouse;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.impl.event.base.Era;
import wtf.nebula.client.impl.event.impl.client.EventTick;
import wtf.nebula.client.impl.event.impl.input.EventMouseInput;
import wtf.nebula.client.impl.event.impl.network.EventPacket;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;
import wtf.nebula.client.impl.translation.Language;
import wtf.nebula.client.impl.translation.Translation;
import wtf.nebula.client.impl.translation.TranslationAPI;

public class Translator extends ToggleableModule {
    private final Property<Language> target = new Property<>(Language.ENGLISH, "Target", "t");

    private TranslationAPI.SearchThread searchThread;

    public Translator() {
        super("Translator", new String[]{"autotranslate", "chattranslate"}, ModuleCategory.MISCELLANEOUS);
        offerProperties(target);
    }

    @Override
    protected void onDisable() {
        super.onDisable();

        if (searchThread != null) {
            searchThread.interrupt();
        }

        searchThread = null;
    }

    @EventListener
    public void onTick(EventTick event) {
        if (searchThread != null) {
            Translation translation = searchThread.translation;
            if (translation != null && !translation.getTranslatedText().isEmpty()) {
                print("Translated: " + translation.getTranslatedText());

                searchThread.interrupt();
                searchThread = null;
            }

        }
    }

    @EventListener
    public void onMouseInput(EventMouseInput event) {
        if (target.getValue().equals(Language.AUTO)) {
            target.next();
        }

        if (event.getButton() == 0 && event.isState() && mc.currentScreen instanceof GuiChat) {
            IChatComponent component = mc.ingameGUI.getChatGui().func_146236_a(Mouse.getX(), Mouse.getY());

            if (component == null || component.getChatStyle().getChatHoverEvent() == null) {
                return;
            }

            if (!component.getChatStyle().getChatHoverEvent().getAction().equals(HoverEvent.Action.SHOW_TEXT)) {
                return;
            }

            String content = component.getUnformattedTextForChat();
            if (content == null || content.isEmpty()) {
                return;
            }

            if (searchThread != null) {
                searchThread.interrupt();
                searchThread = null;
            }

            searchThread = TranslationAPI.getResult(Language.AUTO, target.getValue(), content);
        }
    }

    @EventListener
    public void onPacket(EventPacket event) {
        if (event.getEra().equals(Era.PRE) && event.getPacket() instanceof S02PacketChat) {
            S02PacketChat packet = event.getPacket();

            IChatComponent component = packet.field_148919_a;
            if (component == null) {
                return;
            }

            if (component.getUnformattedText().startsWith("<") && component.getChatStyle().getChatHoverEvent() == null) {
                component.getChatStyle().setChatHoverEvent(
                        new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                new ChatComponentText("")
                                        .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED))
                                        .appendText("Translate to")
                                        .appendText(" ")
                                        .appendText(Property.formatEnum(target.getValue())))
                );
            }
        }
    }

}
