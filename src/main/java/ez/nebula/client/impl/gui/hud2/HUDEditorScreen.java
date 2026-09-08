package ez.nebula.client.impl.gui.hud2;

import ez.nebula.client.Nebula;
import ez.nebula.client.api.manager.hud2.HUDElement;
import ez.nebula.client.util.render.font.AWTFontRenderer;
import ez.nebula.client.util.render.font.Fonts;
import ez.nebula.client.util.render.gui.Render2D;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

import java.awt.Color;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author xgraza
 * @since 9/6/2026
 */
public final class HUDEditorScreen extends GuiScreen
{
    private static final Color NORMAL_BG_COLOR = new Color(20, 20, 20, 80);
    private static final Color DISABLED_BG_COLOR = new Color(255, 40, 40, 80);
    private static final double GRID_SNAP_PIXELS = 10;
    private static final float RADIUS = 2.5f;

    private final GuiScreen parent;

    public HUDEditorScreen(final GuiScreen parent)
    {
        this.parent = parent;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        mouseX /= (int) Render2D.getGUIScaleFactor();
        mouseY /= (int) Render2D.getGUIScaleFactor();

        glPushMatrix();
        glScaled(Render2D.getGUIScaleFactor(), Render2D.getGUIScaleFactor(), Render2D.getGUIScaleFactor());

        AWTFontRenderer.DYNAMIC_FONT_RESIZING = false;

        for (final HUDElement element : Nebula.HUD_NEW.getAll())
        {
            // what a block of code!
            final int color = (element.isMouseIn(mouseX, mouseY) ?
                    (element.isToggled() ? NORMAL_BG_COLOR : DISABLED_BG_COLOR).brighter().brighter().brighter() :
                    (element.isToggled() ? NORMAL_BG_COLOR : DISABLED_BG_COLOR)).getRGB();
            Render2D.roundedRectangle(element.getX(), element.getY(), element.getWidth(), element.getHeight(), RADIUS, color);
            if (element.isDragging())
            {
                Fonts.POPPINS.drawStringShadow(String.format("X: %s, Y: %s", (int) element.getX(), (int) element.getY()),
                        element.getX(), element.getY() + element.getHeight() + 1, -1);
            }
            element.render(mouseX, mouseY);
            if (element.isDragging() && !clipElements(element))
            {
                snapElement(element);
            }
        }

        AWTFontRenderer.DYNAMIC_FONT_RESIZING = true;

        glPopMatrix();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        mouseX /= (int) Render2D.getGUIScaleFactor();
        mouseY /= (int) Render2D.getGUIScaleFactor();
        for (final HUDElement element : Nebula.HUD_NEW.getAll())
        {
            element.mouseClicked(mouseX, mouseY, mouseButton);
            if (element.isDragging())
            {
                return;
            }
        }
    }

    private void snapElement(final HUDElement renderElement)
    {
        if (isCtrlKeyDown())
        {
            return;
        }
        // basic snapping

        double halfWidth = width / 2.0;
        double halfHeight = height / 2.0;

        double elementMidX = renderElement.getX() + (renderElement.getWidth() / 2.0);
        double elementMidY = renderElement.getY() + (renderElement.getHeight() / 2.0);

        boolean snapped = false;

        if (Math.abs(halfWidth - elementMidX) <= GRID_SNAP_PIXELS)
        {
            renderElement.setX(halfWidth - (renderElement.getWidth() / 2.0));
            Render2D.line(halfWidth, 0, halfWidth, height, 1.0f, 0xFF00FF00);
            snapped = true;
        }

        if (Math.abs(halfHeight - elementMidY) <= GRID_SNAP_PIXELS)
        {
            renderElement.setY(halfHeight - (renderElement.getHeight() / 2.0));
            Render2D.line(0, halfHeight, width, halfHeight, 1.0f, 0xFF00FF00);
            snapped = true;
        }
        if (snapped)
        {
            return;
        }

        for (final HUDElement element : Nebula.HUD_NEW.getAll())
        {
            if (renderElement.equals(element))
            {
                continue;
            }

            if (Math.abs(element.getX() - renderElement.getX()) <= GRID_SNAP_PIXELS)
            {
                renderElement.setX(element.getX());
                Render2D.line(element.getX(), element.getY() + element.getHeight(), renderElement.getX(), renderElement.getY(), 1.0f, 0xFFFF0000);
            }

            if (Math.abs((element.getX() + element.getWidth()) - renderElement.getX()) <= GRID_SNAP_PIXELS)
            {
                renderElement.setX(element.getX() + element.getWidth());
                Render2D.line(element.getX() + element.getWidth(), element.getY() + element.getHeight(), renderElement.getX(), renderElement.getY(), 1.0f, 0xFFFF0000);
            }
        }
    }

    private boolean clipElements(final HUDElement renderElement)
    {
        double elementX = renderElement.getX();
        double elementY = renderElement.getY();
        double elementW = renderElement.getWidth();
        double elementH = renderElement.getHeight();

        for (final HUDElement element : Nebula.HUD_NEW.getAll())
        {
            if (renderElement.equals(element))
            {
                continue;
            }

            final boolean isClippedXAxis = (elementX >= element.getX() && elementX <= element.getX() + element.getWidth()) ||
                    (element.getX() >= elementX && element.getX() <= elementX + elementW);
            final boolean isClippedYAxis = (elementY >= element.getY() && elementY <= element.getY() + element.getHeight()) ||
                    (element.getY() >= elementY && element.getY() <= elementY + elementH);
            final boolean clipped = isClippedXAxis && isClippedYAxis;
            if (clipped)
            {
                // TODO: move renderElement to not clip with other elements
                return true;
            }
        }
        return false;
    }

    @Override
    protected void keyTyped(final char typedChar, final int keyCode)
    {
        if (keyCode == Keyboard.KEY_ESCAPE)
        {
            mc.displayGuiScreen(parent);
            mc.setIngameFocus();
        }
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }
}
