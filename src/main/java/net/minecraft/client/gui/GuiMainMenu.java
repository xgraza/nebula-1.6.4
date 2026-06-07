package net.minecraft.client.gui;

import ez.nebula.client.impl.module.render.ClickGUIModule;
import ez.nebula.client.impl.module.render.HUDModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.SplashTextProvider;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.demo.DemoWorldServer;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opencl.CL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;
import ez.nebula.client.BuildConfig;
import ez.nebula.client.core.ClientConfig;
import ez.nebula.client.core.Environment;
import ez.nebula.client.impl.gui.account.AccountSelectorScreen;
import ez.nebula.client.api.render.font.Fonts;

import java.awt.Desktop;
import java.net.URI;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class GuiMainMenu extends GuiScreen
{
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * The RNG used by the Main Menu Screen.
     */
    private static final Random RNG = new Random();

    private static final ResourceLocation SPLASH_TEXT_RESOURCE = new ResourceLocation("texts/splashes.txt");
    private static final ResourceLocation TITLE_TEXTURES_RESOURCE = new ResourceLocation("textures/gui/title/minecraft.png");
    /**
     * An array of all the paths to the panorama pictures.
     */
    private static final ResourceLocation[] PANORAMA_TEXTURES_RESOURCE = new ResourceLocation[]{
            new ResourceLocation("textures/gui/title/background/panorama_0.png"),
            new ResourceLocation("textures/gui/title/background/panorama_1.png"),
            new ResourceLocation("textures/gui/title/background/panorama_2.png"),
            new ResourceLocation("textures/gui/title/background/panorama_3.png"),
            new ResourceLocation("textures/gui/title/background/panorama_4.png"),
            new ResourceLocation("textures/gui/title/background/panorama_5.png")
    };

    private static final String COPYRIGHT_TEXT = "Copyright Mojang AB. Do not distribute!";

    /**
     * Counts the number of screen updates.
     */
    private final float updateCounter;

    /**
     * The splash message.
     */
    private String splashText;

    /**
     * Timer used to rotate the panorama, increases every tick.
     */
    private int panoramaTimer;

    private final Object field_104025_t = new Object();
    private String field_92025_p;
    private String field_146972_A;
    private String field_104024_v;
    public static final String field_96138_a = "Please click " + EnumChatFormatting.UNDERLINE + "here" + EnumChatFormatting.RESET + " for more information.";
    private int field_92024_r;
    private int field_92022_t;
    private int field_92021_u;
    private int field_92020_v;
    private int field_92019_w;
    private ResourceLocation backgroundResource;

    static
    {
        SplashTextProvider.addSplashTextProvider(SPLASH_TEXT_RESOURCE);
    }

    public GuiMainMenu()
    {
        field_146972_A = field_96138_a;

        updateCounter = RNG.nextFloat();
        field_92025_p = "";

        if (!OpenGlHelper.openGL21)
        {
            field_92025_p = "Old graphics card detected; this may prevent you from";
            field_146972_A = "playing in the far future as OpenGL 2.1 will be required.";
            field_104024_v = "https://help.mojang.com/customer/portal/articles/325948?ref=game";
        }
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        ++panoramaTimer;
    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char typedChar, int keyCode)
    {
        final ClickGUIModule module = ClickGUIModule.INSTANCE;
        if (module == null || module.getKey().isMouseBind() || module.getKey().getKeyCode() != keyCode)
        {
            return;
        }
        module.toggle();
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        allowUserInput = true;
        setSplashText();
        backgroundResource = mc.getTextureManager().getDynamicTextureLocation(
                "background", new DynamicTexture(256, 256));

        int y = height / 4 + 48;

        if (mc.isDemo())
        {
            addDemoButtons(y);
        } else
        {
            addSingleplayerMultiplayerButtons(y);
        }

        buttonList.add(new GuiButton(0, width / 2 - 100, y + 72 + 12, 98, 20, I18n.format("menu.options")));
        buttonList.add(new GuiButton(4, width / 2 + 2, y + 72 + 12, 98, 20, I18n.format("menu.quit")));
        buttonList.add(new GuiButtonLanguage(5, width / 2 - 124, y + 72 + 12));

        synchronized (field_104025_t)
        {
            int field_92023_s = fontRenderer.getStringWidth(field_92025_p);
            field_92024_r = fontRenderer.getStringWidth(field_146972_A);
            int var5 = Math.max(field_92023_s, field_92024_r);
            field_92022_t = (width - var5) / 2;
            field_92021_u = buttonList.get(0).yPosition - 24;
            field_92020_v = field_92022_t + var5;
            field_92019_w = field_92021_u + 24;
        }
    }

    private void setSplashText()
    {
        splashText = SplashTextProvider.getRandomSplashText(ClientConfig.USE_CUSTOM_SPLASH_TEXT ? "nebula" : "minecraft");
        final Calendar calender = Calendar.getInstance();
        calender.setTime(new Date());

        int month = calender.get(Calendar.MONTH) + 1;
        int day = calender.get(Calendar.DATE);

        if (month == 11 && day == 9)
        {
            splashText = "Happy birthday, ez!";
        } else if (month == 6 && day == 1)
        {
            splashText = "Happy birthday, Notch!";
        } else if (month == 12 && day == 24)
        {
            splashText = "Merry X-mas!";
        } else if (month == 1 && day == 1)
        {
            splashText = "Happy new year!";
        } else if (month == 10 && day == 31)
        {
            splashText = "OOoooOOOoooo! Spooky!";
        }
    }

    /**
     * Adds Singleplayer and Multiplayer buttons on Main Menu for players who have bought the game.
     */
    private void addSingleplayerMultiplayerButtons(int y)
    {
        buttonList.add(new GuiButton(1, width / 2 - 100, y, I18n.format("menu.singleplayer")));
        buttonList.add(new GuiButton(2, width / 2 - 100, y + 24, I18n.format("menu.multiplayer")));
        buttonList.add(new GuiButton(69, width / 2 - 100, y + 24 * 2, "Account Manager"));

        buttonList.add(new GuiButton(420, 4, 4, 65, 20, "alfheim.pw"));
    }

    /**
     * Adds Demo buttons on Main Menu for players who are playing Demo.
     */
    private void addDemoButtons(int y)
    {
        buttonList.add(new GuiButton(11, width / 2 - 100, y, I18n.format("menu.playdemo")));
        GuiButton buttonResetDemo;
        buttonList.add(buttonResetDemo = new GuiButton(12, width / 2 - 100, y + 24, I18n.format("menu.resetdemo")));

        if (mc.getSaveLoader().getWorldInfo("Demo_World") == null)
        {
            buttonResetDemo.enabled = false;
        }
    }

    protected void actionPerformed(GuiButton button)
    {
        switch (button.id)
        {
            case 0:
                mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
                break;
            case 1:
                mc.displayGuiScreen(new GuiSelectWorld(this));
                break;
            case 2:
                mc.displayGuiScreen(new GuiMultiplayer(this));
                break;
            case 3:
                break;
            case 4:
                mc.shutdown();
                break;
            case 5:
                mc.displayGuiScreen(new GuiLanguage(this, mc.gameSettings, mc.getLanguageManager()));
                break;
            case 11:
                mc.launchIntegratedServer("Demo_World", "Demo_World", DemoWorldServer.demoWorldSettings);
                break;
            case 12:
            {
                WorldInfo demoWorld = mc.getSaveLoader().getWorldInfo("Demo_World");
                if (demoWorld != null)
                {
                    mc.displayGuiScreen(GuiSelectWorld.func_146623_a(
                            this, demoWorld.getWorldName(), 12));
                }
                break;
            }
            case 69:
                mc.displayGuiScreen(new AccountSelectorScreen());
                break;
            case 420:
            {
                final ServerAddress address = ServerAddress.resolveAddress("alfheim.pw");
                mc.displayGuiScreen(new GuiConnecting(this, mc, address.getIP(), address.getPort()));
                break;
            }
        }
    }

    public void confirmClicked(boolean par1, int buttonID)
    {
        if (par1 && buttonID == 12)
        {
            ISaveFormat saveLoader = mc.getSaveLoader();
            saveLoader.flushCache();
            saveLoader.deleteWorldDirectory("Demo_World");
            mc.displayGuiScreen(this);
        } else if (buttonID == 13)
        {
            if (par1)
            {
                try
                {
                    Desktop.getDesktop().browse(new URI(field_104024_v));
                } catch (Throwable var5)
                {
                    LOGGER.error("Couldn't open link", var5);
                }
            }

            mc.displayGuiScreen(this);
        }
    }

    /**
     * Draws the main menu panorama
     */
    private void drawPanorama(int par1, int par2, float par3)
    {
        Tessellator var4 = Tessellator.instance;
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        Project.gluPerspective(120.0F, 1.0F, 0.05F, 10.0F);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDepthMask(false);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        byte var5 = 8;

        for (int var6 = 0; var6 < var5 * var5; ++var6)
        {
            GL11.glPushMatrix();
            float var7 = ((float) (var6 % var5) / (float) var5 - 0.5F) / 64.0F;
            float var8 = ((float) (var6 / var5) / (float) var5 - 0.5F) / 64.0F;
            float var9 = 0.0F;
            GL11.glTranslatef(var7, var8, var9);
            GL11.glRotatef(MathHelper.sin(((float) panoramaTimer + par3) / 400.0F) * 25.0F + 20.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(-((float) panoramaTimer + par3) * 0.1F, 0.0F, 1.0F, 0.0F);

            for (int var10 = 0; var10 < 6; ++var10)
            {
                GL11.glPushMatrix();

                if (var10 == 1)
                {
                    GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
                }

                if (var10 == 2)
                {
                    GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
                }

                if (var10 == 3)
                {
                    GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
                }

                if (var10 == 4)
                {
                    GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
                }

                if (var10 == 5)
                {
                    GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                }

                mc.getTextureManager().bindTexture(PANORAMA_TEXTURES_RESOURCE[var10]);
                var4.startDrawingQuads();
                var4.setColorRGBA_I(16777215, 255 / (var6 + 1));
                float var11 = 0.0F;
                var4.addVertexWithUV(-1.0D, -1.0D, 1.0D, 0.0F + var11, 0.0F + var11);
                var4.addVertexWithUV(1.0D, -1.0D, 1.0D, 1.0F - var11, 0.0F + var11);
                var4.addVertexWithUV(1.0D, 1.0D, 1.0D, 1.0F - var11, 1.0F - var11);
                var4.addVertexWithUV(-1.0D, 1.0D, 1.0D, 0.0F + var11, 1.0F - var11);
                var4.draw();
                GL11.glPopMatrix();
            }

            GL11.glPopMatrix();
            GL11.glColorMask(true, true, true, false);
        }

        var4.setTranslation(0.0D, 0.0D, 0.0D);
        GL11.glColorMask(true, true, true, true);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPopMatrix();
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    /**
     * Rotate and blurs the skybox view in the main menu
     */
    private void rotateAndBlurSkybox(float par1)
    {
        mc.getTextureManager().bindTexture(backgroundResource);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 0, 0, 256, 256);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColorMask(true, true, true, false);
        Tessellator var2 = Tessellator.instance;
        var2.startDrawingQuads();
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        byte var3 = 3;

        for (int var4 = 0; var4 < var3; ++var4)
        {
            var2.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F / (float) (var4 + 1));
            int var5 = width;
            int var6 = height;
            float var7 = (float) (var4 - var3 / 2) / 256.0F;
            var2.addVertexWithUV(var5, var6, zLevel, 0.0F + var7, 1.0D);
            var2.addVertexWithUV(var5, 0.0D, zLevel, 1.0F + var7, 1.0D);
            var2.addVertexWithUV(0.0D, 0.0D, zLevel, 1.0F + var7, 0.0D);
            var2.addVertexWithUV(0.0D, var6, zLevel, 0.0F + var7, 0.0D);
        }

        var2.draw();
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glColorMask(true, true, true, true);
    }

    /**
     * Renders the skybox in the main menu
     */
    private void renderSkybox(int par1, int par2, float par3)
    {
        mc.getFramebuffer().unbindFramebuffer();
        GL11.glViewport(0, 0, 256, 256);
        drawPanorama(par1, par2, par3);
        rotateAndBlurSkybox(par3);
        rotateAndBlurSkybox(par3);
        rotateAndBlurSkybox(par3);
        rotateAndBlurSkybox(par3);
        rotateAndBlurSkybox(par3);
        rotateAndBlurSkybox(par3);
        rotateAndBlurSkybox(par3);
        mc.getFramebuffer().bindFramebuffer(true);
        GL11.glViewport(0, 0, mc.displayWidth, mc.displayHeight);
        Tessellator var4 = Tessellator.instance;
        var4.startDrawingQuads();
        float var5 = width > height ? 120.0F / (float) width : 120.0F / (float) height;
        float var6 = (float) height * var5 / 256.0F;
        float var7 = (float) width * var5 / 256.0F;
        var4.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F);
        int var8 = width;
        int var9 = height;
        var4.addVertexWithUV(0.0D, var9, zLevel, 0.5F - var6, 0.5F + var7);
        var4.addVertexWithUV(var8, var9, zLevel, 0.5F - var6, 0.5F - var7);
        var4.addVertexWithUV(var8, 0.0D, zLevel, 0.5F + var6, 0.5F - var7);
        var4.addVertexWithUV(0.0D, 0.0D, zLevel, 0.5F + var6, 0.5F + var7);
        var4.draw();
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        renderSkybox(mouseX, mouseY, partialTicks);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        Tessellator var4 = Tessellator.instance;
        short var5 = 274;
        int var6 = width / 2 - var5 / 2;
        byte var7 = 30;
        drawGradientRect(0, 0, width, height, -2130706433, 16777215);
        drawGradientRect(0, 0, width, height, 0, Integer.MIN_VALUE);
        mc.getTextureManager().bindTexture(TITLE_TEXTURES_RESOURCE);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        if ((double) updateCounter < 1.0E-4D)
        {
            drawTexturedModalRect(var6, var7, 0, 0, 99, 44);
            drawTexturedModalRect(var6 + 99, var7, 129, 0, 27, 44);
            drawTexturedModalRect(var6 + 99 + 26, var7, 126, 0, 3, 44);
            drawTexturedModalRect(var6 + 99 + 26 + 3, var7, 99, 0, 26, 44);
            drawTexturedModalRect(var6 + 155, var7, 0, 45, 155, 44);
        } else
        {
            drawTexturedModalRect(var6, var7, 0, 0, 155, 44);
            drawTexturedModalRect(var6 + 155, var7, 0, 45, 155, 44);
        }

        var4.setColorOpaque_I(-1);
        GL11.glPushMatrix();
        GL11.glTranslatef((float) (width / 2 + 90), 70.0F, 0.0F);
        GL11.glRotatef(-20.0F, 0.0F, 0.0F, 1.0F);
        float var8 = 1.8F - MathHelper.abs(MathHelper.sin((float) (Minecraft.getSystemTime() % 1000L) / 1000.0F * (float) Math.PI * 2.0F) * 0.1F);
        var8 = var8 * 100.0F / (float) (fontRenderer.getStringWidth(splashText) + 32);
        GL11.glScalef(var8, var8, var8);
        drawCenteredString(fontRenderer, splashText, 0, -8, -256);
        GL11.glPopMatrix();
        drawString(fontRenderer, "Minecraft 1.7.2" + (mc.isDemo() ? " Demo" : ""), 2, height - 10, -1);
        drawString(fontRenderer, COPYRIGHT_TEXT, width - fontRenderer.getStringWidth(COPYRIGHT_TEXT) - 2, height - 10, -1);

        if (field_92025_p != null && !field_92025_p.isEmpty())
        {
            drawRect(field_92022_t - 2, field_92021_u - 2, field_92020_v + 2, field_92019_w - 1, 1428160512);
            drawString(fontRenderer, field_92025_p, field_92022_t, field_92021_u, -1);
            drawString(fontRenderer, field_146972_A, (width - field_92024_r) / 2, buttonList.get(0).yPosition - 12, -1);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);

        if (BuildConfig.ENV == Environment.PRIVATE)
        {
            final String text = "Nebula A.S.S version - please don't distribute!";
            Fonts.POPPINS.drawStringShadow(text, width - Fonts.POPPINS.getStringWidth(text) - 2, 0, HUDModule.INSTANCE.getBaseColor(10));
        }
    }

    /**
     * Called when the mouse is clicked.
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        synchronized (field_104025_t)
        {
            if (!field_92025_p.isEmpty() && mouseX >= field_92022_t && mouseX <= field_92020_v && mouseY >= field_92021_u && mouseY <= field_92019_w)
            {
                GuiConfirmOpenLink var5 = new GuiConfirmOpenLink(this, field_104024_v, 13, true);
                var5.func_146358_g();
                mc.displayGuiScreen(var5);
            }
        }
    }
}
