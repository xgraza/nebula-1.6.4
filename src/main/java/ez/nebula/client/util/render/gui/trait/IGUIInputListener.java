package ez.nebula.client.util.render.gui.trait;

/**
 * @author xgraza
 * @since 02/28/25
 */
public interface IGUIInputListener
{
    void mouseClicked(final int mouseX, final int mouseY, final int mouseButton);

    void keyTyped(final char typedChar, final int keyCode);
}
