package nebula.client.gui.account;

import nebula.client.Nebula;
import nebula.client.account.Account;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

/**
 * @author Gavin
 * @since 08/13/23
 */
public class AccountManagerScreen extends GuiScreen {

  private final GuiScreen parent;
  private AccountSlots accountSlots;

  public AccountManagerScreen(GuiScreen parent) {
    this.parent = parent;
  }

  @Override
  public void initGui() {
    buttonList.clear();

    buttonList.add(new GuiButton(0, width / 2 - 100, height - 30, "Back"));
    buttonList.add(new GuiButton(1, width / 2 - 100, height - 53, 100, 20, "Add"));
    buttonList.add(new GuiButton(2, width / 2, height - 53, 100, 20, "Delete"));

    accountSlots = new AccountSlots(this);
  }

  @Override
  public void drawScreen(int mouseX, int mouseY, float tickDelta) {
    drawDefaultBackground();
    accountSlots.drawScreen(mouseX, mouseY, tickDelta);
    super.drawScreen(mouseX, mouseY, tickDelta);

    mc.fontRenderer.drawStringWithShadow("Logged in as " + mc.getSession().getUsername(),
      4, 4, 0xAAAAAA);

    drawCenteredString(mc.fontRenderer,
      "Account Manager [" + Nebula.INSTANCE.account.values().size() + "]",
      width / 2, 12, 0xAAAAAA);
  }

  @Override
  protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    super.mouseClicked(mouseX, mouseY, mouseButton);
  }

  @Override
  protected void actionPerformed(GuiButton button) {
    super.actionPerformed(button);

    switch (button.id) {
      case 0 -> mc.displayGuiScreen(parent);
      case 1 -> mc.displayGuiScreen(new AccountAddScreen(this));
      case 2 -> {
        Account account = Nebula.INSTANCE.account.get(accountSlots.selected());
        if (account != null) {
          Nebula.INSTANCE.account.remove(account);
        }
      }
    }
  }
}
