package wtf.nebula.client.impl.module.active;

import me.bush.eventbus.annotation.EventListener;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.core.Launcher;
import wtf.nebula.client.impl.event.impl.render.RenderHUDEvent;
import wtf.nebula.client.impl.module.Module;

public class HUD extends Module {
    private final Property<Boolean> watermark = new Property<>(true, "Watermark", "name", "displayname");
    private final Property<Boolean> arraylist = new Property<>(true, "Active", "arraylist");

    public HUD() {
        super("HUD", new String[]{"hud", "overlay", "hudoverlay"});
        offerProperties(watermark, arraylist);
    }

    @EventListener
    public void onRenderHUD(RenderHUDEvent event) {
        if (watermark.getValue()) {
            mc.fontRenderer.drawStringWithShadow(Launcher.NAME + " " + Launcher.VERSION.getVersionString(), 3, 3, Colors.getClientColor());
        }
    }
}
