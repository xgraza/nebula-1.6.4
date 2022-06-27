package net.minecraft.src;

import java.io.IOException;
import java.util.List;
import org.lwjgl.opengl.GL11;

class GuiScreenTemporaryResourcePackSelectSelectionList extends GuiSlot
{
    private final ResourcePackRepository field_110511_b;
    private ResourceLocation field_110513_h;

    final GuiScreenTemporaryResourcePackSelect field_110512_a;

    public GuiScreenTemporaryResourcePackSelectSelectionList(GuiScreenTemporaryResourcePackSelect par1GuiScreenTemporaryResourcePackSelect, ResourcePackRepository par2ResourcePackRepository)
    {
        super(GuiScreenTemporaryResourcePackSelect.func_110344_a(par1GuiScreenTemporaryResourcePackSelect), par1GuiScreenTemporaryResourcePackSelect.width, par1GuiScreenTemporaryResourcePackSelect.height, 32, par1GuiScreenTemporaryResourcePackSelect.height - 55 + 4, 36);
        this.field_110512_a = par1GuiScreenTemporaryResourcePackSelect;
        this.field_110511_b = par2ResourcePackRepository;
        par2ResourcePackRepository.updateRepositoryEntriesAll();
    }

    /**
     * Gets the size of the current slot list.
     */
    protected int getSize()
    {
        return 1 + this.field_110511_b.getRepositoryEntriesAll().size();
    }

    /**
     * the element in the slot that was clicked, boolean for wether it was double clicked or not
     */
    protected void elementClicked(int par1, boolean par2)
    {
        List var3 = this.field_110511_b.getRepositoryEntriesAll();

        try
        {
            if (par1 == 0)
            {
                throw new RuntimeException("This is so horrible ;D");
            }

            this.field_110511_b.setRepositoryEntries(new ResourcePackRepositoryEntry[] {(ResourcePackRepositoryEntry)var3.get(par1 - 1)});
            GuiScreenTemporaryResourcePackSelect.func_110341_b(this.field_110512_a).refreshResources();
        }
        catch (Exception var5)
        {
            this.field_110511_b.setRepositoryEntries(new ResourcePackRepositoryEntry[0]);
            GuiScreenTemporaryResourcePackSelect.func_110339_c(this.field_110512_a).refreshResources();
        }

        GuiScreenTemporaryResourcePackSelect.func_110345_d(this.field_110512_a).gameSettings.skin = this.field_110511_b.getResourcePackName();
        GuiScreenTemporaryResourcePackSelect.func_110334_e(this.field_110512_a).gameSettings.saveOptions();
    }

    /**
     * returns true if the element passed in is currently selected
     */
    protected boolean isSelected(int par1)
    {
        List var2 = this.field_110511_b.getRepositoryEntries();
        return par1 == 0 ? var2.isEmpty() : var2.contains(this.field_110511_b.getRepositoryEntriesAll().get(par1 - 1));
    }

    /**
     * return the height of the content being scrolled
     */
    protected int getContentHeight()
    {
        return this.getSize() * 36;
    }

    protected void drawBackground()
    {
        this.field_110512_a.drawDefaultBackground();
    }

    protected void drawSlot(int par1, int par2, int par3, int par4, Tessellator par5Tessellator)
    {
        TextureManager var6 = GuiScreenTemporaryResourcePackSelect.func_110340_f(this.field_110512_a).getTextureManager();

        if (par1 == 0)
        {
            try
            {
                ResourcePack var12 = this.field_110511_b.rprDefaultResourcePack;
                PackMetadataSection var13 = (PackMetadataSection)var12.getPackMetadata(this.field_110511_b.rprMetadataSerializer, "pack");

                if (this.field_110513_h == null)
                {
                    this.field_110513_h = var6.getDynamicTextureLocation("texturepackicon", new DynamicTexture(var12.getPackImage()));
                }

                var6.bindTexture(this.field_110513_h);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                par5Tessellator.startDrawingQuads();
                par5Tessellator.setColorOpaque_I(16777215);
                par5Tessellator.addVertexWithUV((double)par2, (double)(par3 + par4), 0.0D, 0.0D, 1.0D);
                par5Tessellator.addVertexWithUV((double)(par2 + 32), (double)(par3 + par4), 0.0D, 1.0D, 1.0D);
                par5Tessellator.addVertexWithUV((double)(par2 + 32), (double)par3, 0.0D, 1.0D, 0.0D);
                par5Tessellator.addVertexWithUV((double)par2, (double)par3, 0.0D, 0.0D, 0.0D);
                par5Tessellator.draw();
                this.field_110512_a.drawString(GuiScreenTemporaryResourcePackSelect.func_130017_g(this.field_110512_a), "Default", par2 + 32 + 2, par3 + 1, 16777215);
                this.field_110512_a.drawString(GuiScreenTemporaryResourcePackSelect.func_130016_h(this.field_110512_a), var13.getPackDescription(), par2 + 32 + 2, par3 + 12 + 10, 8421504);
            }
            catch (IOException var11)
            {
                ;
            }
        }
        else
        {
            ResourcePackRepositoryEntry var7 = (ResourcePackRepositoryEntry)this.field_110511_b.getRepositoryEntriesAll().get(par1 - 1);
            var7.bindTexturePackIcon(var6);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            par5Tessellator.startDrawingQuads();
            par5Tessellator.setColorOpaque_I(16777215);
            par5Tessellator.addVertexWithUV((double)par2, (double)(par3 + par4), 0.0D, 0.0D, 1.0D);
            par5Tessellator.addVertexWithUV((double)(par2 + 32), (double)(par3 + par4), 0.0D, 1.0D, 1.0D);
            par5Tessellator.addVertexWithUV((double)(par2 + 32), (double)par3, 0.0D, 1.0D, 0.0D);
            par5Tessellator.addVertexWithUV((double)par2, (double)par3, 0.0D, 0.0D, 0.0D);
            par5Tessellator.draw();
            String var8 = var7.getResourcePackName();

            if (var8.length() > 32)
            {
                var8 = var8.substring(0, 32).trim() + "...";
            }

            this.field_110512_a.drawString(GuiScreenTemporaryResourcePackSelect.func_110337_i(this.field_110512_a), var8, par2 + 32 + 2, par3 + 1, 16777215);
            List var9 = GuiScreenTemporaryResourcePackSelect.func_110335_j(this.field_110512_a).listFormattedStringToWidth(var7.getTexturePackDescription(), 183);

            for (int var10 = 0; var10 < 2 && var10 < var9.size(); ++var10)
            {
                this.field_110512_a.drawString(GuiScreenTemporaryResourcePackSelect.func_110338_k(this.field_110512_a), (String)var9.get(var10), par2 + 32 + 2, par3 + 12 + 10 * var10, 8421504);
            }
        }
    }

    static ResourcePackRepository func_110510_a(GuiScreenTemporaryResourcePackSelectSelectionList par0GuiScreenTemporaryResourcePackSelectSelectionList)
    {
        return par0GuiScreenTemporaryResourcePackSelectSelectionList.field_110511_b;
    }
}
