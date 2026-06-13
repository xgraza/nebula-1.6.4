/*
 * Copyright (c) xgraza 2025
 */

package net.minecraft.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.event.ClickEvent;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;

public class ScreenShotHelper
{
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

    /**
     * A buffer to hold pixel values returned by OpenGL.
     */
    private static IntBuffer pixelBuffer;

    /**
     * The built-up array that contains all the pixel values returned by OpenGL.
     */
    private static int[] pixelValues;

    private static volatile boolean screenshotting;

    /**
     * Saves a screenshot in the game directory with a time-stamped filename.  Args: gameDirectory,
     * requestedWidthInPixels, requestedHeightInPixels, frameBuffer
     */
    public static void saveScreenshot(File dataDir, int width, int height, Framebuffer frameBuffer)
    {
        final File screenshotDirectory = new File(dataDir, "screenshots");
        if (!screenshotDirectory.exists() && !screenshotDirectory.mkdir())
        {
            throw new RuntimeException("Failed to create screenshots directory at "
                    + screenshotDirectory.getAbsolutePath());
        }

        screenshotting = true;

        if (OpenGlHelper.isFramebufferEnabled())
        {
            width = frameBuffer.framebufferTextureWidth;
            height = frameBuffer.framebufferTextureHeight;
        }

        final int bufferSize = width * height;
        if (pixelBuffer == null || pixelBuffer.capacity() < bufferSize)
        {
            pixelBuffer = BufferUtils.createIntBuffer(bufferSize);
            pixelValues = new int[bufferSize];
        }

        GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        pixelBuffer.clear();

        if (OpenGlHelper.isFramebufferEnabled())
        {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, frameBuffer.framebufferTexture);
            GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer);
        } else
        {
            GL11.glReadPixels(0, 0, width, height, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer);
        }

        pixelBuffer.get(pixelValues);
        TextureUtil.func_147953_a(pixelValues, width, height);

        new ScreenshotThread(
                (component)
                        -> Minecraft.getMinecraft().ingameGUI.getChatGui().printChatMessage(component),
                screenshotDirectory, frameBuffer, width, height).start();
    }

    /**
     * Creates a unique PNG file in the given directory named by a timestamp.  Handles cases where the timestamp alone
     * is not enough to create a uniquely named file, though it still might suffer from an unlikely race condition where
     * the filename was unique when this method was called, but another process or thread created a file at the same
     * path immediately after this method returned.
     */
    private static File getTimestampedPNGFileForDirectory(File par0File)
    {
        String var2 = dateFormat.format(new Date());
        int var3 = 1;

        while (true)
        {
            File var1 = new File(par0File, var2 + (var3 == 1 ? "" : "_" + var3) + ".png");

            if (!var1.exists())
            {
                return var1;
            }

            ++var3;
        }
    }

    public static boolean isScreenshotting()
    {
        return screenshotting;
    }

    private static final class ScreenshotThread extends Thread
    {
        private final Consumer<IChatComponent> callback;
        private final File dataDirectory;
        private final Framebuffer fb;
        private final int width;
        private final int height;

        private ScreenshotThread(final Consumer<IChatComponent> callback, final File dataDirectory, final Framebuffer framebuffer, final int width, final int height)
        {
            this.callback = callback;
            this.dataDirectory = dataDirectory;
            this.fb = framebuffer;
            this.width = width;
            this.height = height;
        }

        @Override
        public void run()
        {
            BufferedImage var7;

            if (OpenGlHelper.isFramebufferEnabled())
            {
                var7 = new BufferedImage(fb.framebufferWidth, fb.framebufferHeight, 1);
                int var8 = fb.framebufferTextureHeight - fb.framebufferHeight;

                for (int var9 = var8; var9 < fb.framebufferTextureHeight; ++var9)
                {
                    for (int var10 = 0; var10 < fb.framebufferWidth; ++var10)
                    {
                        var7.setRGB(var10, var9 - var8, pixelValues[var9 * fb.framebufferTextureWidth + var10]);
                    }
                }
            } else
            {
                var7 = new BufferedImage(width, height, 1);
                var7.setRGB(0, 0, width, height, pixelValues, 0, width);
            }
            File var12 = getTimestampedPNGFileForDirectory(dataDirectory);
            try
            {
                ImageIO.write(var7, "png", var12);
            } catch (IOException e)
            {
                screenshotting = false;
                callback.accept(new ChatComponentTranslation("screenshot.failure", e.getMessage()));
                return;
            }
            ChatComponentText var13 = new ChatComponentText(var12.getName());
            var13.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, var12.getAbsolutePath()));
            var13.getChatStyle().setUnderlined(Boolean.TRUE);
            callback.accept(new ChatComponentTranslation("screenshot.success", var13));

            screenshotting = false;
        }
    }
}
