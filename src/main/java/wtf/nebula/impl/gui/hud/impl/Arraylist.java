package wtf.nebula.impl.gui.hud.impl;

import net.minecraft.client.gui.ScaledResolution;
import wtf.nebula.impl.gui.hud.HUDElement;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.render.HUD;
import wtf.nebula.repository.impl.ModuleRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Arraylist extends HUDElement {
    public Arraylist(HUD hud) {
        super(hud, "ArrayList");

        x = 0.0;
        y = 0.0;
    }

    @Override
    public void onRender(ScaledResolution resolution) {

        if (!hud.arrayList.getValue()) {
            return;
        }

        List<Module> modules = ModuleRepository.get().getChildren().stream()
                .filter((m) -> m.getState() && m.drawn.getValue())
                .collect(Collectors.toList());

        if (modules.isEmpty()) {
            return;
        }

        modules.sort(Comparator.comparingInt((a) -> -mc.fontRenderer.getStringWidth(a.getName())));

        x = resolution.getScaledWidth_double() - mc.fontRenderer.getStringWidth(modules.get(0).getName()) - 4;
        y = 4.0;

        for (int i = 0; i < modules.size(); ++i) {
            Module module = modules.get(i);

            int width = mc.fontRenderer.getStringWidth(module.getName());

            double tx = resolution.getScaledWidth_double() - width - 4.0;
            double ty = y + (i * (mc.fontRenderer.FONT_HEIGHT + 2));

            // RenderUtil.drawRect(tx - 4, ty, width + 8, mc.fontRenderer.FONT_HEIGHT + 2, 0x70000000);

            mc.fontRenderer.drawStringWithShadow(module.getName(), (int) tx, (int) ty, hud.color((i * 100) + 100));
        }
    }
}
