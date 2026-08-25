package ez.nebula.client.impl.module.player;

import ez.nebula.client.api.DebugFeature;
import ez.nebula.client.api.listener.event.render.EventRender2D;
import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.util.render.gui.Render2D;
import ez.nebula.client.util.render.font.Fonts;
import org.lwjgl.input.Keyboard;
import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;

@DebugFeature
@ModuleManifest(name = "Test", category = ModuleCategory.PLAYER)
public final class TestModule extends Module
{
    private boolean bl = true;

    @Override public void onEnable()
    {
        super.onEnable();
        bl = true;
    }

    @Subscribe
    private final EventListener<EventRender2D> render2DEventListener = event ->
    {
        if (Keyboard.isKeyDown(Keyboard.KEY_GRAVE))
        {
            bl = !bl;
        }

        double x = event.getResolution().getScaledWidth_double() / 2.0;
        double y = event.getResolution().getScaledHeight_double() / 2.0;
        String text = "This is a test set of text";
        double textWidth = bl ? Fonts.POPPINS.getStringWidth(text) : MC.fontRenderer.getStringWidth(text);
        Render2D.rectangle(x, y, textWidth, bl ? Fonts.POPPINS.getFontHeight() : MC.fontRenderer.FONT_HEIGHT, 0x95000000);
        if (bl)
        {
            Fonts.POPPINS.drawString(text, x, y, - 1, false);
        } else
        {
            MC.fontRenderer.drawString(text, (int) x, (int) y + 2, - 1);
        }
    };
}
