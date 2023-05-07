package lol.nebula.ui.account;

import lol.nebula.Nebula;
import lol.nebula.account.Account;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;

public class GuiAddAccountScreen extends GuiScreen {
    private final GuiAccountLoginScreen parent;

    private GuiTextField usernameBox;
    private GuiTextField passwordBox;

    private String errorString = null;

    public GuiAddAccountScreen(GuiAccountLoginScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        buttonList.add(new GuiButton(1, width / 2 - 100, height / 4 + 96 + 12, "Add"));
        buttonList.add(new GuiButton(2, width / 2 - 100, height / 4 + 96 + 36, "Back"));
        usernameBox = new GuiTextField(fontRendererObj, width / 2 - 100, 76, 200, 20);
        usernameBox.setFocused(true);
        passwordBox = new GuiTextField(fontRendererObj, width / 2 - 100, 116, 200, 20);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        drawDefaultBackground();
        super.drawScreen(par1, par2, par3);

        usernameBox.drawTextBox();
        passwordBox.drawTextBox();

        mc.fontRenderer.drawStringWithShadow(String.format("%s* \u00a77Username", usernameBox.getText().length() > 1 ? EnumChatFormatting.GREEN : EnumChatFormatting.RED), width / 2 - 109, (int) 63.0f, 0xA0A0A0);
        mc.fontRenderer.drawStringWithShadow("Password", width / 2 - 100, 103, 0xA0A0A0);

        if (errorString != null) {
            drawCenteredString(mc.fontRenderer, EnumChatFormatting.RED + errorString, width / 2, 20, 0xA0A0A0);
        }

        if (usernameBox.isFocused() || passwordBox.isFocused()) {
            errorString = null;
        }
    }

    @Override
    protected void mouseClicked(int par1, int par2, int par3) {
        usernameBox.mouseClicked(par1, par2, par3);
        passwordBox.mouseClicked(par1, par2, par3);
        super.mouseClicked(par1, par2, par3);
    }

    @Override
    protected void keyTyped(char par1, int par2) {
        if (par2 == Keyboard.KEY_ESCAPE) {
            mc.displayGuiScreen(parent);
        } else {
            usernameBox.textboxKeyTyped(par1, par2);
            passwordBox.textboxKeyTyped(par1, par2);

            if (par2 == Keyboard.KEY_TAB) {
                usernameBox.setFocused(!usernameBox.isFocused());
                passwordBox.setFocused(!passwordBox.isFocused());
            } else if (par2 == Keyboard.KEY_RETURN) {
                actionPerformed((GuiButton) buttonList.get(0));
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton p_146284_1_) {
        super.actionPerformed(p_146284_1_);

        if (p_146284_1_.id == 1) {
            errorString = null;

            if (usernameBox.getText().isEmpty() || usernameBox.getText().length() < 1) {
                errorString = "You must provide a username.";
            } else {
                Nebula.getInstance().getAccounts().getAccountList().add(new Account(
                        usernameBox.getText(), passwordBox.getText()));

                mc.displayGuiScreen(parent);
            }
        } else if (p_146284_1_.id == 2) {
            mc.displayGuiScreen(parent);
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        usernameBox.updateCursorCounter();
        passwordBox.updateCursorCounter();
    }
}
