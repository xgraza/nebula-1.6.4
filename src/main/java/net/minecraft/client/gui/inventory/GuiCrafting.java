package net.minecraft.client.gui.inventory;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class GuiCrafting extends GuiContainer
{
    private static final ResourceLocation field_147019_u = new ResourceLocation("textures/gui/container/crafting_table.png");
    private static final String __OBFID = "CL_00000750";

    public GuiCrafting(InventoryPlayer par1InventoryPlayer, World par2World, int par3, int par4, int par5)
    {
        super(new ContainerWorkbench(par1InventoryPlayer, par2World, par3, par4, par5));
    }

    protected void func_146979_b(int p_146979_1_, int p_146979_2_)
    {
        this.fontRenderer.drawString(I18n.format("container.crafting"), 28, 6, 4210752);
        this.fontRenderer.drawString(I18n.format("container.inventory"), 8, this.containerHeight - 96 + 2, 4210752);
    }

    protected void func_146976_a(float p_146976_1_, int p_146976_2_, int p_146976_3_)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(field_147019_u);
        int var4 = (this.width - this.containerWidth) / 2;
        int var5 = (this.height - this.containerHeight) / 2;
        this.drawTexturedModalRect(var4, var5, 0, 0, this.containerWidth, this.containerHeight);
    }
}
