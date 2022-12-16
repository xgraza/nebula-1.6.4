package wtf.nebula.client.impl.module.miscellaneous;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.util.EnumChatFormatting;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.core.Nebula;
import wtf.nebula.client.impl.event.impl.render.EventRenderTabListName;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;

public class ExtraTab extends ToggleableModule {
    private final Property<Boolean> highlightFriends = new Property<>(true, "Highlight Friends", "friendhighlight");

    public ExtraTab() {
        super("Extra Tab", new String[]{"extratab", "xtab", "tabmodifier"}, ModuleCategory.MISCELLANEOUS);
        offerProperties(highlightFriends);

        setDrawn(false);
        setRunning(true);
    }

    @EventListener
    public void onRenderTabListName(EventRenderTabListName event) {
        if (Nebula.getInstance().getFriendManager().isFriend(event.getInfo().name) && highlightFriends.getValue()) {
            event.setText(EnumChatFormatting.AQUA + event.getInfo().name);
            event.setCancelled(true);
        }
    }
}
