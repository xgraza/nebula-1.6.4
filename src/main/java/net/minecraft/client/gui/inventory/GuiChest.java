package net.minecraft.client.gui.inventory;

import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiChest extends GuiContainer
{
    private static final ResourceLocation field_147017_u = new ResourceLocation("textures/gui/container/generic_54.png");
    private final IInventory field_147016_v;
    private final IInventory field_147015_w;
    private final int field_147018_x;
    private static final String __OBFID = "CL_00000749";

    public GuiChest(IInventory par1IInventory, IInventory par2IInventory)
    {
        super(new ContainerChest(par1IInventory, par2IInventory));
        this.field_147016_v = par1IInventory;
        this.field_147015_w = par2IInventory;
        this.allowUserInput = false;
        short var3 = 222;
        int var4 = var3 - 108;
        this.field_147018_x = par2IInventory.getSizeInventory() / 9;
        this.containerHeight = var4 + this.field_147018_x * 18;
    }

    protected void func_146979_b(int p_146979_1_, int p_146979_2_)
    {
        this.fontRenderer.drawString(this.field_147015_w.isInventoryNameLocalized() ? this.field_147015_w.getInventoryName() : I18n.format(this.field_147015_w.getInventoryName()), 8, 6, 4210752);
        this.fontRenderer.drawString(this.field_147016_v.isInventoryNameLocalized() ? this.field_147016_v.getInventoryName() : I18n.format(this.field_147016_v.getInventoryName()), 8, this.containerHeight - 96 + 2, 4210752);
    }

    protected void func_146976_a(float p_146976_1_, int p_146976_2_, int p_146976_3_)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(field_147017_u);
        int var4 = (this.width - this.containerWidth) / 2;
        int var5 = (this.height - this.containerHeight) / 2;
        this.drawTexturedModalRect(var4, var5, 0, 0, this.containerWidth, this.field_147018_x * 18 + 17);
        this.drawTexturedModalRect(var4, var5 + this.field_147018_x * 18 + 17, 0, 126, this.containerWidth, 96);
    }
}
