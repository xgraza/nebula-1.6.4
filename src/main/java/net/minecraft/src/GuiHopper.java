package net.minecraft.src;

import org.lwjgl.opengl.GL11;

public class GuiHopper extends GuiContainer
{
    private static final ResourceLocation hopperGuiTextures = new ResourceLocation("textures/gui/container/hopper.png");
    private IInventory field_94081_r;
    private IInventory field_94080_s;

    public GuiHopper(InventoryPlayer par1InventoryPlayer, IInventory par2IInventory)
    {
        super(new ContainerHopper(par1InventoryPlayer, par2IInventory));
        this.field_94081_r = par1InventoryPlayer;
        this.field_94080_s = par2IInventory;
        this.allowUserInput = false;
        this.ySize = 133;
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    protected void drawGuiContainerForegroundLayer(int par1, int par2)
    {
        this.fontRenderer.drawString(this.field_94080_s.isInvNameLocalized() ? this.field_94080_s.getInvName() : I18n.getString(this.field_94080_s.getInvName()), 8, 6, 4210752);
        this.fontRenderer.drawString(this.field_94081_r.isInvNameLocalized() ? this.field_94081_r.getInvName() : I18n.getString(this.field_94081_r.getInvName()), 8, this.ySize - 96 + 2, 4210752);
    }

    /**
     * Draw the background layer for the GuiContainer (everything behind the items)
     */
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(hopperGuiTextures);
        int var4 = (this.width - this.xSize) / 2;
        int var5 = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(var4, var5, 0, 0, this.xSize, this.ySize);
    }
}
