package nebula.client.gui.account;

import nebula.client.Nebula;
import nebula.client.account.Account;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;

import static org.lwjgl.input.Keyboard.KEY_RETURN;

/**
 * @author Gavin
 * @since 08/13/23
 */
public class AccountAddScreen extends GuiScreen {

  private final GuiScreen parent;
  private GuiTextField usernameField;

  public AccountAddScreen(GuiScreen parent) {
    this.parent = parent;
  }

  @Override
  public void initGui() {
    buttonList.clear();

    buttonList.add(new GuiButton(0, width / 2 - 100, height / 2 - 47, "Back"));
    buttonList.add(new GuiButton(1, width / 2 - 100, height / 2 - 70, "Add"));

    usernameField = new GuiTextField(mc.fontRenderer, width / 2 - 100, height / 2 - 100, 200, 20);
    usernameField.setFocused(true);

    Keyboard.enableRepeatEvents(true);
  }

  @Override
  public void updateScreen() {
    super.updateScreen();
    usernameField.updateCursorCounter();
  }

  @Override
  public void drawScreen(int par1, int par2, float par3) {
    drawDefaultBackground();
    super.drawScreen(par1, par2, par3);
    usernameField.drawTextBox();
    mc.fontRenderer.drawStringWithShadow("Username:",
      width / 2 - 100, height / 2 - 114, -1);
  }

  @Override
  protected void mouseClicked(int par1, int par2, int par3) {
    super.mouseClicked(par1, par2, par3);
    usernameField.mouseClicked(par1, par2, par3);
  }

  @Override
  protected void keyTyped(char par1, int par2) {
    super.keyTyped(par1, par2);
    usernameField.textboxKeyTyped(par1, par2);

    if (par2 == KEY_RETURN) {
      actionPerformed((GuiButton) buttonList.get(1));
    }
  }

  @Override
  protected void actionPerformed(GuiButton p_146284_1_) {
    super.actionPerformed(p_146284_1_);

    switch (p_146284_1_.id) {
      case 0 -> mc.displayGuiScreen(parent);
      case 1 -> {
        String text = usernameField.getText()
          .trim()
          .replaceAll("\\s+", "");
        if (text.isEmpty()) return;

        if (text.toLowerCase().startsWith("aesthetical")) {
          Sys.alert("", "kys");
          return;
        }

        Nebula.INSTANCE.account.add(new Account(text));
        mc.displayGuiScreen(parent);
      }
    }
  }

  @Override
  public void onGuiClosed() {
    super.onGuiClosed();
    Keyboard.enableRepeatEvents(false);
  }
}
