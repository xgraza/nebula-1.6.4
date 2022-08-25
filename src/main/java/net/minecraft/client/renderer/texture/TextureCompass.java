package net.minecraft.client.renderer.texture;

import net.minecraft.client.Minecraft;
import net.minecraft.src.Config;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import shadersmod.client.ShadersTex;

public class TextureCompass extends TextureAtlasSprite
{
    public double currentAngle;
    public double angleDelta;

    public TextureCompass(String par1Str)
    {
        super(par1Str);
    }

    public void updateAnimation()
    {
        Minecraft var1 = Minecraft.getMinecraft();

        if (var1.theWorld != null && var1.thePlayer != null)
        {
            this.updateCompass(var1.theWorld, var1.thePlayer.posX, var1.thePlayer.posZ, (double)var1.thePlayer.rotationYaw, false, false);
        }
        else
        {
            this.updateCompass((World)null, 0.0D, 0.0D, 0.0D, true, false);
        }
    }

    public void updateCompass(World par1World, double par2, double par4, double par6, boolean par8, boolean par9)
    {
        if (!this.framesTextureData.isEmpty())
        {
            double var10 = 0.0D;

            if (par1World != null && !par8)
            {
                ChunkCoordinates var18 = par1World.getSpawnPoint();
                double var13 = (double)var18.posX - par2;
                double var15 = (double)var18.posZ - par4;
                par6 %= 360.0D;
                var10 = -((par6 - 90.0D) * Math.PI / 180.0D - Math.atan2(var15, var13));

                if (!par1World.provider.isSurfaceWorld())
                {
                    var10 = Math.random() * Math.PI * 2.0D;
                }
            }

            if (par9)
            {
                this.currentAngle = var10;
            }
            else
            {
                double var181;

                for (var181 = var10 - this.currentAngle; var181 < -Math.PI; var181 += (Math.PI * 2D))
                {
                    ;
                }

                while (var181 >= Math.PI)
                {
                    var181 -= (Math.PI * 2D);
                }

                if (var181 < -1.0D)
                {
                    var181 = -1.0D;
                }

                if (var181 > 1.0D)
                {
                    var181 = 1.0D;
                }

                this.angleDelta += var181 * 0.1D;
                this.angleDelta *= 0.8D;
                this.currentAngle += this.angleDelta;
            }

            int var182;

            for (var182 = (int)((this.currentAngle / (Math.PI * 2D) + 1.0D) * (double)this.framesTextureData.size()) % this.framesTextureData.size(); var182 < 0; var182 = (var182 + this.framesTextureData.size()) % this.framesTextureData.size())
            {
                ;
            }

            if (var182 != this.frameCounter)
            {
                this.frameCounter = var182;

                if (Config.isShaders())
                {
                    ShadersTex.uploadTexSub((int[][])((int[][])this.framesTextureData.get(this.frameCounter)), this.width, this.height, this.originX, this.originY, false, false);
                }
                else
                {
                    TextureUtil.uploadTextureMipmap((int[][])((int[][])this.framesTextureData.get(this.frameCounter)), this.width, this.height, this.originX, this.originY, false, false);
                }
            }
        }
    }
}
