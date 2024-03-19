package yzy.szn.impl.module.render;

import yzy.szn.api.eventbus.Subscribe;
import yzy.szn.api.module.Module;
import yzy.szn.api.module.ModuleCategory;
import yzy.szn.api.module.ModuleManifest;
import yzy.szn.impl.event.EventRender;

import java.util.function.Consumer;

import static yzy.szn.api.MinecraftInstance.mc;

/**
 * @author xgraza
 * @since 03/12/24
 */
@ModuleManifest(name = "HUD",
        category = ModuleCategory.RENDER)
public final class ModuleHUD extends Module {

    public ModuleHUD() {
        setToggled(true);
    }

    @Subscribe
    private final Consumer<EventRender.Overlay> renderOverlayListener = event -> {
        mc.fontRenderer.drawStringWithShadow("Yeezus", 2, 2, -1);
    };
}
