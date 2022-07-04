package wtf.nebula.impl.gui.hud.impl;

import net.minecraft.src.ScaledResolution;
import wtf.nebula.impl.gui.hud.HUDElement;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.render.HUD;
import wtf.nebula.impl.module.render.HUD.ArraylistMode;
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

        List<Module> modules = ModuleRepository.get().getChildren().stream()
                .filter((m) -> m.getState() && m.drawn.getValue())
                .collect(Collectors.toList());

        if (modules.isEmpty()) {
            return;
        }

        ArraylistMode mode = hud.arrayList.getValue();
        if (mode.equals(ArraylistMode.NEW)) {

            modules.sort(Comparator.comparingInt((a) -> -mc.fontRenderer.getStringWidth(a.getName())));

//            if (x == 0.0) {
//                x = resolution.getScaledWidth_double() - mc.fontRenderer.getStringWidth(modules.get(0).getName()) - 2;
//            }

            x = resolution.getScaledWidth_double() - mc.fontRenderer.getStringWidth(modules.get(0).getName()) - 2;
            y = 0.0;

            for (int i = 0; i < modules.size(); ++i) {
                Module module = modules.get(i);

                int width = mc.fontRenderer.getStringWidth(module.getName());

                double tx = resolution.getScaledWidth_double() - width - 2.0;
                double ty = y + (i * (mc.fontRenderer.FONT_HEIGHT + 2));

                // RenderUtil.drawRect(tx - 4, ty, width + 8, mc.fontRenderer.FONT_HEIGHT + 2, 0x70000000);

                mc.fontRenderer.drawStringWithShadow(module.getName(), (int) tx, (int) ty, hud.color((i * 100) + 100));
            }
        }

        else if (mode.equals(ArraylistMode.OLD)) {

//            if (x == 0.0 && y == 0.0) {
//                x = 4.0;
//                y = mc.fontRenderer.FONT_HEIGHT + 4.0;
//            }

            x = 4.0;
            y = mc.fontRenderer.FONT_HEIGHT + 4.0;

            for (int i = 0; i < modules.size(); ++i) {
                Module module = modules.get(i);

                mc.fontRenderer.drawStringWithShadow(
                        ">" + module.getName(),
                        (int) x,
                        (int) (y + (i * (mc.fontRenderer.FONT_HEIGHT + 2))),
                        hud.color((i * 100) + 100)
                );
            }
        }
    }
}
