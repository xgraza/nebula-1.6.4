package net.minecraft.src;

import java.util.Iterator;
import org.lwjgl.opengl.GL11;

public class MapItemRenderer
{
    private static final ResourceLocation field_111277_a = new ResourceLocation("textures/map/map_icons.png");
    private final DynamicTexture bufferedImage;
    private int[] intArray = new int[16384];
    private GameSettings gameSettings;
    private final ResourceLocation field_111276_e;

    public MapItemRenderer(GameSettings par1GameSettings, TextureManager par2TextureManager)
    {
        this.gameSettings = par1GameSettings;
        this.bufferedImage = new DynamicTexture(128, 128);
        this.field_111276_e = par2TextureManager.getDynamicTextureLocation("map", this.bufferedImage);
        this.intArray = this.bufferedImage.getTextureData();

        for (int var4 = 0; var4 < this.intArray.length; ++var4)
        {
            this.intArray[var4] = 0;
        }
    }

    public void renderMap(EntityPlayer par1EntityPlayer, TextureManager par2TextureManager, MapData par3MapData)
    {
        for (int var4 = 0; var4 < 16384; ++var4)
        {
            byte var5 = par3MapData.colors[var4];

            if (var5 / 4 == 0)
            {
                this.intArray[var4] = (var4 + var4 / 128 & 1) * 8 + 16 << 24;
            }
            else
            {
                int var6 = MapColor.mapColorArray[var5 / 4].colorValue;
                int var7 = var5 & 3;
                short var8 = 220;

                if (var7 == 2)
                {
                    var8 = 255;
                }

                if (var7 == 0)
                {
                    var8 = 180;
                }

                int var9 = (var6 >> 16 & 255) * var8 / 255;
                int var10 = (var6 >> 8 & 255) * var8 / 255;
                int var11 = (var6 & 255) * var8 / 255;
                this.intArray[var4] = -16777216 | var9 << 16 | var10 << 8 | var11;
            }
        }

        this.bufferedImage.updateDynamicTexture();
        byte var15 = 0;
        byte var16 = 0;
        Tessellator var17 = Tessellator.instance;
        float var18 = 0.0F;
        par2TextureManager.bindTexture(this.field_111276_e);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        var17.startDrawingQuads();
        var17.addVertexWithUV((double)((float)(var15 + 0) + var18), (double)((float)(var16 + 128) - var18), -0.009999999776482582D, 0.0D, 1.0D);
        var17.addVertexWithUV((double)((float)(var15 + 128) - var18), (double)((float)(var16 + 128) - var18), -0.009999999776482582D, 1.0D, 1.0D);
        var17.addVertexWithUV((double)((float)(var15 + 128) - var18), (double)((float)(var16 + 0) + var18), -0.009999999776482582D, 1.0D, 0.0D);
        var17.addVertexWithUV((double)((float)(var15 + 0) + var18), (double)((float)(var16 + 0) + var18), -0.009999999776482582D, 0.0D, 0.0D);
        var17.draw();
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        par2TextureManager.bindTexture(field_111277_a);
        int var19 = 0;

        for (Iterator var20 = par3MapData.playersVisibleOnMap.values().iterator(); var20.hasNext(); ++var19)
        {
            MapCoord var21 = (MapCoord)var20.next();
            GL11.glPushMatrix();
            GL11.glTranslatef((float)var15 + (float)var21.centerX / 2.0F + 64.0F, (float)var16 + (float)var21.centerZ / 2.0F + 64.0F, -0.02F);
            GL11.glRotatef((float)(var21.iconRotation * 360) / 16.0F, 0.0F, 0.0F, 1.0F);
            GL11.glScalef(4.0F, 4.0F, 3.0F);
            GL11.glTranslatef(-0.125F, 0.125F, 0.0F);
            float var22 = (float)(var21.iconSize % 4 + 0) / 4.0F;
            float var12 = (float)(var21.iconSize / 4 + 0) / 4.0F;
            float var13 = (float)(var21.iconSize % 4 + 1) / 4.0F;
            float var14 = (float)(var21.iconSize / 4 + 1) / 4.0F;
            var17.startDrawingQuads();
            var17.addVertexWithUV(-1.0D, 1.0D, (double)((float)var19 * 0.001F), (double)var22, (double)var12);
            var17.addVertexWithUV(1.0D, 1.0D, (double)((float)var19 * 0.001F), (double)var13, (double)var12);
            var17.addVertexWithUV(1.0D, -1.0D, (double)((float)var19 * 0.001F), (double)var13, (double)var14);
            var17.addVertexWithUV(-1.0D, -1.0D, (double)((float)var19 * 0.001F), (double)var22, (double)var14);
            var17.draw();
            GL11.glPopMatrix();
        }

        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, 0.0F, -0.04F);
        GL11.glScalef(1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }
}
