package net.minecraft.src;

import java.nio.IntBuffer;
import net.minecraft.client.renderer.OpenGlHelper;
import org.lwjgl.opengl.GL11;

public class GlStateManager
{
    public static int activeTextureUnit = 0;
    public static int GL_FRAMEBUFFER = 36160;
    public static int GL_RENDERBUFFER = 36161;
    public static int GL_COLOR_ATTACHMENT0 = 36064;
    public static int GL_DEPTH_ATTACHMENT = 36096;

    public static int getActiveTextureUnit()
    {
        return activeTextureUnit;
    }

    public static void bindTexture(int tex)
    {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex);
    }

    public static void deleteTexture(int tex)
    {
        if (tex != 0)
        {
            GL11.glDeleteTextures(tex);
        }
    }

    public static void deleteTextures(IntBuffer buf)
    {
        buf.rewind();

        while (buf.position() < buf.limit())
        {
            int texId = buf.get();
            deleteTexture(texId);
        }

        buf.rewind();
    }

    public static void setActiveTexture(int tex)
    {
        OpenGlHelper.setActiveTexture(tex);
    }

    public static void colorMask(boolean red, boolean green, boolean blue, boolean alpha)
    {
        GL11.glColorMask(red, green, blue, alpha);
    }

    public static void enableCull()
    {
        GL11.glEnable(GL11.GL_CULL_FACE);
    }

    public static void disableCull()
    {
        GL11.glDisable(GL11.GL_CULL_FACE);
    }

    public static void enableDepth()
    {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    public static void disableDepth()
    {
        GL11.glDisable(GL11.GL_DEPTH_TEST);
    }

    public static void enableTexture2D()
    {
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    public static void disableTexture2D()
    {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
    }

    public static void enableAlpha()
    {
        GL11.glEnable(GL11.GL_ALPHA_TEST);
    }

    public static void disableAlpha()
    {
        GL11.glDisable(GL11.GL_ALPHA_TEST);
    }

    public static void enableBlend()
    {
        GL11.glEnable(GL11.GL_BLEND);
    }

    public static void disableBlend()
    {
        GL11.glDisable(GL11.GL_BLEND);
    }

    public static void depthFunc(int func)
    {
        GL11.glDepthFunc(func);
    }

    public static void depthMask(boolean mask)
    {
        GL11.glDepthMask(mask);
    }

    public static void enableLighting()
    {
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    public static void disableLighting()
    {
        GL11.glDisable(GL11.GL_LIGHTING);
    }

    public static void color(float p_179131_0_, float p_179131_1_, float p_179131_2_, float p_179131_3_)
    {
        GL11.glColor4f(p_179131_0_, p_179131_1_, p_179131_2_, p_179131_3_);
    }

    public static void color(float p_179124_0_, float p_179124_1_, float p_179124_2_)
    {
        color(p_179124_0_, p_179124_1_, p_179124_2_, 1.0F);
    }

    public static void clearColor(float red, float green, float blue, float alpha)
    {
        GL11.glClearColor(red, green, blue, alpha);
    }

    public static void alphaFunc(int func, float ref)
    {
        GL11.glAlphaFunc(func, ref);
    }

    public static void blendFunc(int sfactor, int dfactor)
    {
        GL11.glBlendFunc(sfactor, dfactor);
    }

    public static void setFog(int p_179093_0_)
    {
        GL11.glFogi(GL11.GL_FOG_MODE, p_179093_0_);
    }

    public static void setFogDensity(float p_179095_0_)
    {
        GL11.glFogf(GL11.GL_FOG_DENSITY, p_179095_0_);
    }

    public static void setFogStart(float p_179102_0_)
    {
        GL11.glFogf(GL11.GL_FOG_START, p_179102_0_);
    }

    public static void setFogEnd(float p_179153_0_)
    {
        GL11.glFogf(GL11.GL_FOG_END, p_179153_0_);
    }

    public static void shadeModel(int p_179103_0_)
    {
        GL11.glShadeModel(p_179103_0_);
    }

    public static void clear(int p_179086_0_)
    {
        GL11.glClear(p_179086_0_);
    }

    public static void matrixMode(int p_179128_0_)
    {
        GL11.glMatrixMode(p_179128_0_);
    }

    public static void loadIdentity()
    {
        GL11.glLoadIdentity();
    }

    public static void pushMatrix()
    {
        GL11.glPushMatrix();
    }

    public static void popMatrix()
    {
        GL11.glPopMatrix();
    }

    public static void tryBlendFuncSeparate(int p_179120_0_, int p_179120_1_, int p_179120_2_, int p_179120_3_)
    {
        OpenGlHelper.glBlendFunc(p_179120_0_, p_179120_1_, p_179120_2_, p_179120_3_);
    }

    public static void rotate(float p_179114_0_, float p_179114_1_, float p_179114_2_, float p_179114_3_)
    {
        GL11.glRotatef(p_179114_0_, p_179114_1_, p_179114_2_, p_179114_3_);
    }
}
