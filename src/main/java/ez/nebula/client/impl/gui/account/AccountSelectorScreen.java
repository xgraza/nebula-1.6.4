package ez.nebula.client.impl.gui.account;

import ez.nebula.client.Nebula;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.util.EnumChatFormatting;
import ez.nebula.client.util.render.font.Fonts;
import ez.nebula.client.api.manager.account.Account;

import java.util.List;

/**
 * @author xgraza
 * @since 04/03/25
 */
public final class AccountSelectorScreen extends GuiScreen
{
    private AccountSlotsElement accountSlotsElement;

    @Override
    public void initGui()
    {
        super.initGui();

        accountSlotsElement = new AccountSlotsElement(mc, this);

        int middleX = width / 2;
        buttonList.add(new GuiButton(0, middleX - 101, height - 56, 100, 20, "Create"));
        buttonList.add(new GuiButton(1, middleX + 1, height - 56, 100, 20, "Delete"));
        buttonList.add(new GuiButton(2, middleX - 100, height - 32, "Back"));
    }

    @Override
    protected void actionPerformed(final GuiButton button)
    {
        switch (button.id)
        {
            case 0:
            {
                mc.displayGuiScreen(new AccountCreateScreen());
                break;
            }
            case 1:
            {
                final List<Account> accountList = Nebula.ACCOUNTS.getAll();
                final int selected = accountSlotsElement.getSelected();
                if (selected <= accountList.size() - 1 && selected >= 0)
                {
                    final Account account = accountList.get(selected);
                    mc.displayGuiScreen(new GuiYesNo(this,
                            String.format("Do you want to delete the account %s%s%s?",
                                    EnumChatFormatting.RED,
                                    account.getUsername(),
                                    EnumChatFormatting.RESET),
                            "You will need to re-create it if you want it back.",
                            selected));
                }
                break;
            }
            case 2:
            {
                mc.displayGuiScreen(null);
                break;
            }
        }
    }

    @Override
    public void confirmClicked(final boolean affirmative, final int index)
    {
        if (affirmative && !Nebula.ACCOUNTS.getAll().isEmpty())
        {
            final Account account = Nebula.ACCOUNTS.getAll().get(index);
            if (account != null)
            {
                Nebula.ACCOUNTS.removeAccount(account);
            }
        }
        mc.displayGuiScreen(this);
    }

    @Override
    public void drawScreen(int par1, int par2, float par3)
    {
        drawDefaultBackground();
        accountSlotsElement.drawScreen(par1, par2, par3);
        super.drawScreen(par1, par2, par3);

        Fonts.POPPINS.drawStringShadow(String.format("Session: %s%s",
                        EnumChatFormatting.GREEN,
                        mc.getSession().getUsername()),
                2.0, 2.0, -1);
    }
}
