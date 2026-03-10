package net.minecraft.client;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MinecraftError;
import org.lwjgl.opengl.GL11;

public class LoadingScreenRenderer implements IProgressUpdate
{
    /**
     * A reference to the Minecraft object.
     */
    private final Minecraft mc;

    /**
     * The text currently displayed (i.e. the argument to the last call to printText or func_73722_d)
     */
    private String currentlyDisplayedText = "";
    private String workingMessage = "";

    private long time = Minecraft.getSystemTime();
    private boolean field_73724_e;
    private final ScaledResolution resolution;
    private final Framebuffer fb;

    public LoadingScreenRenderer(Minecraft client)
    {
        this.mc = client;
        this.resolution = new ScaledResolution(client.gameSettings, client.displayWidth, client.displayHeight);
        this.fb = new Framebuffer(resolution.getScaledWidth(), resolution.getScaledHeight(), false);
        this.fb.setFramebufferFilter(9728);
    }

    /**
     * this string, followed by "working..." and then the "% complete" are the 3 lines shown. This resets progress to 0,
     * and the WorkingString to "working...".
     */
    public void resetProgressAndMessage(String par1Str)
    {
        this.field_73724_e = false;
        this.printText(par1Str);
    }

    /**
     * "Saving level", or the loading,or downloading equivelent
     */
    public void displayProgressMessage(String par1Str)
    {
        this.field_73724_e = true;
        this.printText(par1Str);
    }

    public void printText(String par1Str)
    {
        this.currentlyDisplayedText = par1Str;

        if (!this.mc.running)
        {
            if (!this.field_73724_e)
            {
                throw new MinecraftError();
            }
        } else
        {
            GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glLoadIdentity();

            if (OpenGlHelper.isFramebufferEnabled())
            {
                int var2 = this.resolution.getScaleFactor();
                GL11.glOrtho(0.0D, this.resolution.getScaledWidth() * var2, this.resolution.getScaledHeight() * var2, 0.0D, 100.0D, 300.0D);
            } else
            {
                ScaledResolution var3 = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
                GL11.glOrtho(0.0D, var3.getScaledWidth_double(), var3.getScaledHeight_double(), 0.0D, 100.0D, 300.0D);
            }

            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glLoadIdentity();
            GL11.glTranslatef(0.0F, 0.0F, -200.0F);
        }
    }

    /**
     * This is called with "Working..." by resetProgressAndMessage
     */
    public void resetProgresAndWorkingMessage(String par1Str)
    {
        if (!this.mc.running)
        {
            if (!this.field_73724_e)
            {
                throw new MinecraftError();
            }
        } else
        {
            this.time = 0L;
            this.workingMessage = par1Str;
            this.setLoadingProgress(-1);
            this.time = 0L;
        }
    }

    /**
     * Updates the progress bar on the loading screen to the specified amount. Args: loadProgress
     */
    public void setLoadingProgress(int loadProgress)
    {
        if (!this.mc.running)
        {
            if (!this.field_73724_e)
            {
                throw new MinecraftError();
            }
        } else
        {
            long var2 = Minecraft.getSystemTime();

            if (var2 - this.time >= 100L)
            {
                this.time = var2;
                ScaledResolution var4 = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
                int var5 = var4.getScaleFactor();
                int var6 = var4.getScaledWidth();
                int var7 = var4.getScaledHeight();

                if (OpenGlHelper.isFramebufferEnabled())
                {
                    this.fb.framebufferClear();
                } else
                {
                    GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
                }

                this.fb.bindFramebuffer(true);
                GL11.glMatrixMode(GL11.GL_PROJECTION);
                GL11.glLoadIdentity();

                if (OpenGlHelper.isFramebufferEnabled())
                {
                    GL11.glOrtho(0.0D, var6, var7, 0.0D, 100.0D, 300.0D);
                } else
                {
                    GL11.glOrtho(0.0D, var4.getScaledWidth_double(), var4.getScaledHeight_double(), 0.0D, 100.0D, 300.0D);
                }

                GL11.glMatrixMode(GL11.GL_MODELVIEW);
                GL11.glLoadIdentity();
                GL11.glTranslatef(0.0F, 0.0F, -200.0F);

                if (!OpenGlHelper.isFramebufferEnabled())
                {
                    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
                }

                Tessellator var8 = Tessellator.instance;
                this.mc.getTextureManager().bindTexture(Gui.optionsBackground);
                float var9 = 32.0F;
                var8.startDrawingQuads();
                var8.setColorOpaque_I(4210752);
                var8.addVertexWithUV(0.0D, var7, 0.0D, 0.0D, (float) var7 / var9);
                var8.addVertexWithUV(var6, var7, 0.0D, (float) var6 / var9, (float) var7 / var9);
                var8.addVertexWithUV(var6, 0.0D, 0.0D, (float) var6 / var9, 0.0D);
                var8.addVertexWithUV(0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
                var8.draw();

                if (loadProgress >= 0)
                {
                    byte var10 = 100;
                    byte var11 = 2;
                    int var12 = var6 / 2 - var10 / 2;
                    int var13 = var7 / 2 + 16;
                    GL11.glDisable(GL11.GL_TEXTURE_2D);
                    var8.startDrawingQuads();
                    var8.setColorOpaque_I(8421504);
                    var8.addVertex(var12, var13, 0.0D);
                    var8.addVertex(var12, var13 + var11, 0.0D);
                    var8.addVertex(var12 + var10, var13 + var11, 0.0D);
                    var8.addVertex(var12 + var10, var13, 0.0D);
                    var8.setColorOpaque_I(8454016);
                    var8.addVertex(var12, var13, 0.0D);
                    var8.addVertex(var12, var13 + var11, 0.0D);
                    var8.addVertex(var12 + loadProgress, var13 + var11, 0.0D);
                    var8.addVertex(var12 + loadProgress, var13, 0.0D);
                    var8.draw();
                    GL11.glEnable(GL11.GL_TEXTURE_2D);
                }

                GL11.glEnable(GL11.GL_BLEND);
                OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                this.mc.fontRenderer.drawStringWithShadow(this.currentlyDisplayedText, (var6 - this.mc.fontRenderer.getStringWidth(this.currentlyDisplayedText)) / 2, var7 / 2 - 4 - 16, 16777215);
                this.mc.fontRenderer.drawStringWithShadow(this.workingMessage, (var6 - this.mc.fontRenderer.getStringWidth(this.workingMessage)) / 2, var7 / 2 - 4 + 8, 16777215);
                this.fb.unbindFramebuffer();

                if (OpenGlHelper.isFramebufferEnabled())
                {
                    this.fb.framebufferRender(var6 * var5, var7 * var5);
                }

                this.mc.updateDisplay();

                try
                {
                    Thread.yield();
                } catch (Exception ignored)
                {
                }
            }
        }
    }

    public void func_146586_a()
    {
    }
}
