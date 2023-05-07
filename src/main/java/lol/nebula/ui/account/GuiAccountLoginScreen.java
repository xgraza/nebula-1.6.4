package lol.nebula.ui.account;

import lol.nebula.Nebula;
import lol.nebula.account.Account;
import lol.nebula.util.math.MathUtils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Session;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class GuiAccountLoginScreen extends GuiScreen {

    private GuiButton random;
    private GuiAccountSlots slots;

    @Override
    public void initGui() {
        buttonList.clear();

        buttonList.add(new GuiButton(1, width / 2 - 154, height - 48, 73, 20, "Add"));
        buttonList.add(new GuiButton(2, width / 2 - 76, height - 48, 73, 20, "Login"));
        buttonList.add(new GuiButton(3, width / 2 + 78, height - 48, 73, 20, "Remove"));
        buttonList.add(new GuiButton(4, width / 2 - 76, height - 26, 149, 20, "Back"));
        buttonList.add(new GuiButton(5, width / 2, height - 48, 73, 20, "Direct Login"));
        buttonList.add(random = new GuiButton(6, width / 2 - 154, height - 26, 73, 20, "Random"));
        buttonList.add(new GuiButton(7, width / 2 + 78, height - 26, 73, 20, "Clear"));

        slots = new GuiAccountSlots(this);
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        slots.func_148128_a(par1, par2, par3);
        super.drawScreen(par1, par2, par3);

        Session currentSession = mc.getSession();
        if (currentSession != null) {
            mc.fontRenderer.drawStringWithShadow(currentSession.getUsername(), width - mc.fontRenderer.getStringWidth(currentSession.getUsername()) - 2, 2, 0xA0A0A0);
        }

        mc.fontRenderer.drawStringWithShadow(
                "Accounts: " + Nebula.getInstance().getAccounts().getAccountList().size(),
                2, 2, 0xA0A0A0);
    }

    @Override
    protected void actionPerformed(GuiButton p_146284_1_) {
        super.actionPerformed(p_146284_1_);

        if (p_146284_1_.id == 1) {
            mc.displayGuiScreen(new GuiAddAccountScreen(this));
        } else if (p_146284_1_.id == 2) {
            try {
                Nebula.getInstance().getAccounts().login(
                        Nebula.getInstance().getAccounts().getAccountList().get(slots.selected));
            } catch (IndexOutOfBoundsException ignored) {
            }
        } else if (p_146284_1_.id == 3) {

            if (slots.selected != -1 && !Nebula.getInstance().getAccounts().getAccountList().isEmpty()) {
                Account account = Nebula.getInstance().getAccounts().getAccountList().get(slots.selected);
                mc.displayGuiScreen(new GuiAccountConfirm(this, "Are you sure you want to delete \"" + account.getEmail() + "\"?", 20));
            }

        } else if (p_146284_1_.id == 4) {
            mc.displayGuiScreen(null);
        } else if (p_146284_1_.id == 5) {
            mc.displayGuiScreen(new GuiAccountDirectLogin(this));
        } else if (p_146284_1_.id == 6) {
            if (random.enabled) {
                List<Account> accounts = Nebula.getInstance().getAccounts().getAccountList();
                int rand = (int) MathUtils.random(0, accounts.size() - 1);

                slots.selected = rand;
                Nebula.getInstance().getAccounts().login(accounts.get(rand));
            }
        } else if (p_146284_1_.id == 7) {
            mc.displayGuiScreen(new GuiAccountConfirm(this, "Are you sure you want to clear all your accounts?", 10));
        }
    }

    @Override
    protected void keyTyped(char par1, int par2) {
        super.keyTyped(par1, par2);

        if (par2 == Keyboard.KEY_UP) {
            slots.selected--;
        } else if (par2 == Keyboard.KEY_DOWN) {
            slots.selected++;
        }

        slots.selected = MathHelper.clamp_int(slots.selected, 0, Nebula.getInstance().getAccounts().getAccountList().size() - 1);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        if (random != null) {
            random.enabled = !Nebula.getInstance().getAccounts().getAccountList().isEmpty();
        }
    }

    @Override
    public void confirmClicked(boolean par1, int par2) {
        if (par1) {
            switch (par2) {

                case 10: {
                    Nebula.getInstance().getAccounts().getAccountList().clear();
                    mc.displayGuiScreen(this);
                    break;
                }

                case 20: {
                    try {
                        Account account = Nebula.getInstance().getAccounts().getAccountList().get(slots.selected);
                        Nebula.getInstance().getAccounts().getAccountList().remove(account);
                    } catch (IndexOutOfBoundsException ignored) {
                    }
                    break;
                }
            }
        }

        mc.displayGuiScreen(this);
    }
}
