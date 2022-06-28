package wtf.nebula.impl.gui;

import net.minecraft.src.*;
import org.lwjgl.input.Keyboard;

public class AltLoginScreen extends GuiScreen {
    // the text fields used for logging in
    private GuiTextField username, password;

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);

        buttonList.clear();

        double midX = width / 2.0;
        double midY = height / 2.0;

        username = new GuiTextField(mc.fontRenderer, (int) (midX - 100.0), (int) (midY - 80.0), 200, 20);
        password = new GuiTextField(mc.fontRenderer, (int) (midX - 100.0), (int) (midY - 50.0), 200, 20);

        buttonList.add(new GuiButton(69420, (int) (midX - 100.0), (int) (midY - 20.0), 200, 20, "Login"));
    }

    @Override
    public void updateScreen() {
        username.updateCursorCounter();
        password.updateCursorCounter();
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        drawDefaultBackground();
        super.drawScreen(par1, par2, par3);

        mc.fontRenderer.drawStringWithShadow(
                "Username & Password:",
                username.xPos,
                username.yPos - 12,
                -1);

        username.drawTextBox();
        password.drawTextBox();
    }

    @Override
    protected void mouseClicked(int par1, int par2, int par3) {
        super.mouseClicked(par1, par2, par3);

        username.mouseClicked(par1, par2, par3);
        password.mouseClicked(par1, par2, par3);
    }

    @Override
    protected void keyTyped(char par1, int par2) {
        super.keyTyped(par1, par2);

        username.textboxKeyTyped(par1, par2);
        password.textboxKeyTyped(par1, par2);
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    protected void actionPerformed(GuiButton par1GuiButton) {
        if (par1GuiButton.id == 69420) {
            String usrname = username.getText();
            if (usrname.isEmpty()) {
                return;
            }

            String pass = password.getText();

            if (pass.isEmpty()) {
                mc.session = new Session(pass, "");
                mc.displayGuiScreen(null);
            }

            else {
                // TODO: mojang login
            }
        }
    }
}
