package wtf.nebula.client.impl.account.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.Tessellator;
import wtf.nebula.client.core.Nebula;
import wtf.nebula.client.impl.account.Account;
import wtf.nebula.client.impl.account.AccountType;
import wtf.nebula.client.utils.client.Wrapper;

public class GuiAccountSlots extends GuiSlot implements Wrapper {
    private final GuiAccountLoginScreen parent;
    public int selected = 0;

    public GuiAccountSlots(GuiAccountLoginScreen parent) {
        super(Minecraft.getMinecraft(), parent.width, parent.height, 32, parent.height - 60, 27);
        this.parent = parent;
        this.selected = 0;
    }

    @Override
    protected int getContentHeight() {
        return getSize() * 40;
    }

    @Override
    protected int getSize() {
        return Nebula.getInstance().getAccountManager().getAlts().size();
    }

    @Override
    protected void elementClicked(int var1, boolean var2, int var3, int var4) {
        if (var2) {
            Account account = Nebula.getInstance().getAccountManager().getAlts().get(var1);
            Nebula.getInstance().getAccountManager().login(account);
        } else {
            selected = var1;
        }
    }

    public int getSelected() {
        return selected;
    }

    @Override
    protected boolean isSelected(int var1) {
        return selected == var1;
    }

    @Override
    protected void drawBackground() {
        parent.drawDefaultBackground();
    }

    @Override
    protected void drawSlot(int var1, int var2, int var3, int var4, Tessellator var5, int var6, int var7) {

        Account account = Nebula.getInstance().getAccountManager().getAlts().get(var1);

        parent.drawCenteredString(mc.fontRenderer, account.getEmail(), parent.width / 2, var3 + 2, -5592406);
        parent.drawCenteredString(mc.fontRenderer, account.getAltType().equals(AccountType.PREMIUM) ? account.getPassword().replaceAll("(?s).", "*") : "N/A", parent.width / 2, var3 + 15, -5592406);
    }
}
