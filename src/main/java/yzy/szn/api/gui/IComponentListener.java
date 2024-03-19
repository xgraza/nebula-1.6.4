package yzy.szn.api.gui;

/**
 * @author graza
 * @since 02/18/24
 */
public interface IComponentListener {

    void render(final int mouseX, final int mouseY);
    void mouseClicked(final int mouseX, final int mouseY, final int mouseButton);
    void keyTyped(final int keyCode);

}
