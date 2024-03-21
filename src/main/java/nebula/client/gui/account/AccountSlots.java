package nebula.client.gui.account;

import nebula.client.Nebula;
import nebula.client.account.Account;
import nebula.client.util.render.PlayerSkinUtils;
import nebula.client.util.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;

import java.awt.*;

/**
 * @author Gavin
 * @since 08/13/23
 */
public class AccountSlots extends GuiSlot {

  private final GuiScreen parent;
  private int selected;

  public AccountSlots(GuiScreen parent) {
    super(Minecraft.getMinecraft(), parent.width, parent.height, 32, parent.height - 60, 27);
    this.parent = parent;
  }

  @Override
  protected int getSize() {
    return Nebula.INSTANCE.account.values().size();
  }

  @Override
  protected int getContentHeight() {
    return super.getContentHeight() + 10;
  }

  @Override
  protected void elementClicked(int var1, boolean var2, int var3, int var4) {
    if (var2) {
      Account account = Nebula.INSTANCE.account.get(var1);
      if (account != null) account.login();
    } else {
      selected = var1;
    }
  }

  @Override
  protected boolean isSelected(int var1) {
    return selected == var1;
  }

  @Override
  protected void drawBackground() {
    // fuck you
  }

  @Override
  protected void drawSlot(int index, int x, int y, int var4, Tessellator tess, int var6, int var7) {

    Account account = Nebula.INSTANCE.account.get(index);

    DynamicTexture textureId = PlayerSkinUtils.face(account.username(), 15);
    if (textureId != null) {
      RenderUtils.renderTexture(textureId.getGlTextureId(),
        x + 6,
        y + (22 / 2) - (15 / 2),
        15, 15, Color.white);
    }

    parent.drawCenteredString(mc.fontRenderer, account.username(),
      parent.width / 2, y + (22 / 2) - (mc.fontRenderer.FONT_HEIGHT / 2), -5592406);
  }

  public int selected() {
    return selected;
  }

  public void setSelected(int selected) {
    this.selected = selected;
  }
}