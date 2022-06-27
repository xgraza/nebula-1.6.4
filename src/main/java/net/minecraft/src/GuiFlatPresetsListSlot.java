package net.minecraft.src;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

class GuiFlatPresetsListSlot extends GuiSlot
{
    public int field_82459_a;

    final GuiFlatPresets flatPresetsGui;

    public GuiFlatPresetsListSlot(GuiFlatPresets par1GuiFlatPresets)
    {
        super(par1GuiFlatPresets.mc, par1GuiFlatPresets.width, par1GuiFlatPresets.height, 80, par1GuiFlatPresets.height - 37, 24);
        this.flatPresetsGui = par1GuiFlatPresets;
        this.field_82459_a = -1;
    }

    private void func_82457_a(int par1, int par2, int par3)
    {
        this.func_82456_d(par1 + 1, par2 + 1);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.enableGUIStandardItemLighting();
        GuiFlatPresets.getPresetIconRenderer().renderItemIntoGUI(this.flatPresetsGui.fontRenderer, this.flatPresetsGui.mc.getTextureManager(), new ItemStack(par3, 1, 0), par1 + 2, par2 + 2);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
    }

    private void func_82456_d(int par1, int par2)
    {
        this.func_82455_b(par1, par2, 0, 0);
    }

    private void func_82455_b(int par1, int par2, int par3, int par4)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.flatPresetsGui.mc.getTextureManager().bindTexture(Gui.statIcons);
        float var5 = 0.0078125F;
        float var6 = 0.0078125F;
        boolean var7 = true;
        boolean var8 = true;
        Tessellator var9 = Tessellator.instance;
        var9.startDrawingQuads();
        var9.addVertexWithUV((double)(par1 + 0), (double)(par2 + 18), (double)this.flatPresetsGui.zLevel, (double)((float)(par3 + 0) * 0.0078125F), (double)((float)(par4 + 18) * 0.0078125F));
        var9.addVertexWithUV((double)(par1 + 18), (double)(par2 + 18), (double)this.flatPresetsGui.zLevel, (double)((float)(par3 + 18) * 0.0078125F), (double)((float)(par4 + 18) * 0.0078125F));
        var9.addVertexWithUV((double)(par1 + 18), (double)(par2 + 0), (double)this.flatPresetsGui.zLevel, (double)((float)(par3 + 18) * 0.0078125F), (double)((float)(par4 + 0) * 0.0078125F));
        var9.addVertexWithUV((double)(par1 + 0), (double)(par2 + 0), (double)this.flatPresetsGui.zLevel, (double)((float)(par3 + 0) * 0.0078125F), (double)((float)(par4 + 0) * 0.0078125F));
        var9.draw();
    }

    /**
     * Gets the size of the current slot list.
     */
    protected int getSize()
    {
        return GuiFlatPresets.getPresets().size();
    }

    /**
     * the element in the slot that was clicked, boolean for wether it was double clicked or not
     */
    protected void elementClicked(int par1, boolean par2)
    {
        this.field_82459_a = par1;
        this.flatPresetsGui.func_82296_g();
        GuiFlatPresets.func_82298_b(this.flatPresetsGui).setText(((GuiFlatPresetsItem)GuiFlatPresets.getPresets().get(GuiFlatPresets.func_82292_a(this.flatPresetsGui).field_82459_a)).presetData);
    }

    /**
     * returns true if the element passed in is currently selected
     */
    protected boolean isSelected(int par1)
    {
        return par1 == this.field_82459_a;
    }

    protected void drawBackground() {}

    protected void drawSlot(int par1, int par2, int par3, int par4, Tessellator par5Tessellator)
    {
        GuiFlatPresetsItem var6 = (GuiFlatPresetsItem)GuiFlatPresets.getPresets().get(par1);
        this.func_82457_a(par2, par3, var6.iconId);
        this.flatPresetsGui.fontRenderer.drawString(var6.presetName, par2 + 18 + 5, par3 + 6, 16777215);
    }
}
