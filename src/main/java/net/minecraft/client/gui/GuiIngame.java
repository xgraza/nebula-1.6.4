/*
 * Copyright (c) xgraza 2025
 */

package net.minecraft.client.gui;

import ez.nebula.client.impl.module.render.*;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.*;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.chunk.Chunk;
import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import ez.nebula.client.core.ClientConfig;
import ez.nebula.client.core.Nebula;
import ez.nebula.client.api.render.font.Fonts;
import ez.nebula.client.api.listener.EventBus;
import ez.nebula.client.api.listener.event.render.EventRender2D;
import ez.nebula.client.api.listener.event.render.EventRenderWaterEffects;
import ez.nebula.client.util.minecraft.player.PlayerUtil;
import ez.nebula.client.util.render.HeadDownloader;
import ez.nebula.client.util.render.RenderUtil;
import org.lwjgl.opengl.GLContext;

import java.awt.Color;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import static org.lwjgl.opengl.GL11.*;

public class GuiIngame extends Gui
{
    private static final ResourceLocation vignetteTexPath = new ResourceLocation("textures/misc/vignette.png");
    private static final ResourceLocation widgetsTexPath = new ResourceLocation("textures/gui/widgets.png");
    private static final ResourceLocation pumpkinBlurTexPath = new ResourceLocation("textures/misc/pumpkinblur.png");
    private static final RenderItem itemRenderer = new RenderItem();
    private final Random rand = new Random();
    private final Minecraft mc;

    /**
     * ChatGUI instance that retains all previous chat data
     */
    private final GuiNewChat persistantChatGUI;
    private int updateCounter;

    /**
     * The string specifying which record music is playing
     */
    private String recordPlaying = "";

    /**
     * How many ticks the record playing message will be displayed
     */
    private int recordPlayingUpFor;
    private boolean recordIsPlaying;

    /**
     * Previous frame vignette brightness (slowly changes by 1% each frame)
     */
    public float prevVignetteBrightness = 1.0F;

    /**
     * Remaining ticks the item highlight should be visible
     */
    private int remainingHighlightTicks;

    /**
     * The ItemStack that is currently being highlighted
     */
    private ItemStack highlightingItemStack;
    private static final String __OBFID = "CL_00000661";

    public GuiIngame(Minecraft par1Minecraft)
    {
        this.mc = par1Minecraft;
        this.persistantChatGUI = new GuiNewChat(par1Minecraft);
    }

    /**
     * Render the ingame overlay with quick icon bar, ...
     */
    public void renderGameOverlay(float par1, boolean par2, int par3, int par4)
    {
        ScaledResolution var5 = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
        RenderUtil.GAME_RESOLUTION = var5;
        int width = var5.getScaledWidth();
        int height = var5.getScaledHeight();
        FontRenderer var8 = this.mc.fontRenderer;
        this.mc.entityRenderer.setupOverlayRendering();
        GL11.glEnable(GL11.GL_BLEND);

        if (Minecraft.isFancyGraphicsEnabled())
        {
            this.renderVignette(this.mc.thePlayer.getBrightness(par1), width, height);
        } else
        {
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        }

        ItemStack var9 = this.mc.thePlayer.inventory.armorItemInSlot(3);

        if (this.mc.gameSettings.thirdPersonView == 0 && var9 != null && var9.getItem() == Item.getItemFromBlock(Blocks.pumpkin))
        {
            this.renderPumpkinBlur(width, height);
        }

        if (!this.mc.thePlayer.isPotionActive(Potion.confusion))
        {
            float var10 = this.mc.thePlayer.prevTimeInPortal + (this.mc.thePlayer.timeInPortal - this.mc.thePlayer.prevTimeInPortal) * par1;

            if (var10 > 0.0F)
            {
                this.renderPortalOverlay(var10, width, height);
            }
        }

        int var11;
        int var12;
        int var13;

        if (!this.mc.playerController.enableEverythingIsScrewedUpMode())
        {
            glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(widgetsTexPath);
            this.zLevel = -90.0F;
            this.drawTexturedModalRect(width / 2 - 91, height - 22, 0, 0, 182, 22);
            this.drawTexturedModalRect(width / 2 - 91 - 1 + Nebula.INSTANCE.getInventoryManager().getSlot() * 20, height - 22 - 1, 0, 22, 24, 22);
            this.mc.getTextureManager().bindTexture(icons);
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(775, 769, 1, 0);
            this.drawTexturedModalRect(width / 2 - 7, height / 2 - 7, 0, 0, 16, 16);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            this.mc.mcProfiler.startSection("bossHealth");
            this.renderBossHealth();
            this.mc.mcProfiler.endSection();

            if (this.mc.playerController.shouldDrawHUD())
            {
                this.drawHUD(width, height);
            }

            this.mc.mcProfiler.startSection("actionBar");
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            RenderHelper.enableGUIStandardItemLighting();

            for (var11 = 0; var11 < 9; ++var11)
            {
                var12 = width / 2 - 90 + var11 * 20 + 2;
                var13 = height - 16 - 3;
                this.renderInventorySlot(var11, var12, var13, par1);
            }

            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            this.mc.mcProfiler.endSection();
            GL11.glDisable(GL11.GL_BLEND);
        }

        int var32;

        if (this.mc.thePlayer.getSleepTimer() > 0)
        {
            this.mc.mcProfiler.startSection("sleep");
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            var32 = this.mc.thePlayer.getSleepTimer();
            float var33 = (float) var32 / 100.0F;

            if (var33 > 1.0F)
            {
                var33 = 1.0F - (float) (var32 - 100) / 10.0F;
            }

            var12 = (int) (220.0F * var33) << 24 | 1052704;
            drawRect(0, 0, width, height, var12);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            this.mc.mcProfiler.endSection();
        }

        var32 = 16777215;
        glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        var11 = width / 2 - 91;
        int var14;
        int var15;
        int var16;
        int var17;
        float var34;
        short var35;

        if (this.mc.thePlayer.isRidingHorse())
        {
            this.mc.mcProfiler.startSection("jumpBar");
            this.mc.getTextureManager().bindTexture(Gui.icons);
            var34 = this.mc.thePlayer.getHorseJumpPower();
            var35 = 182;
            var14 = (int) (var34 * (float) (var35 + 1));
            var15 = height - 32 + 3;
            this.drawTexturedModalRect(var11, var15, 0, 84, var35, 5);

            if (var14 > 0)
            {
                this.drawTexturedModalRect(var11, var15, 0, 89, var14, 5);
            }

            this.mc.mcProfiler.endSection();
        } else if (this.mc.playerController.gameIsSurvivalOrAdventure())
        {
            this.mc.mcProfiler.startSection("expBar");
            this.mc.getTextureManager().bindTexture(Gui.icons);
            var12 = this.mc.thePlayer.xpBarCap();

            if (var12 > 0)
            {
                var35 = 182;
                var14 = (int) (this.mc.thePlayer.experience * (float) (var35 + 1));
                var15 = height - 32 + 3;
                this.drawTexturedModalRect(var11, var15, 0, 64, var35, 5);

                if (var14 > 0)
                {
                    this.drawTexturedModalRect(var11, var15, 0, 69, var14, 5);
                }
            }

            this.mc.mcProfiler.endSection();

            if (this.mc.thePlayer.experienceLevel > 0)
            {
                this.mc.mcProfiler.startSection("expLevel");
                boolean var37 = false;
                var14 = var37 ? 16777215 : 8453920;
                String var39 = "" + this.mc.thePlayer.experienceLevel;
                var16 = (width - var8.getStringWidth(var39)) / 2;
                var17 = height - 31 - 4;
                boolean var18 = false;
                var8.drawString(var39, var16 + 1, var17, 0);
                var8.drawString(var39, var16 - 1, var17, 0);
                var8.drawString(var39, var16, var17 + 1, 0);
                var8.drawString(var39, var16, var17 - 1, 0);
                var8.drawString(var39, var16, var17, var14);
                this.mc.mcProfiler.endSection();
            }
        }

        String var36;

        if (this.mc.gameSettings.heldItemTooltips)
        {
            this.mc.mcProfiler.startSection("toolHighlight");

            if (this.remainingHighlightTicks > 0 && this.highlightingItemStack != null)
            {
                var36 = this.highlightingItemStack.getDisplayName();
                var13 = (width - var8.getStringWidth(var36)) / 2;
                var14 = height - 59;

                if (!this.mc.playerController.shouldDrawHUD())
                {
                    var14 += 14;
                }

                var15 = (int) ((float) this.remainingHighlightTicks * 256.0F / 10.0F);

                if (var15 > 255)
                {
                    var15 = 255;
                }

                if (var15 > 0)
                {
                    GL11.glPushMatrix();
                    GL11.glEnable(GL11.GL_BLEND);
                    OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                    var8.drawStringWithShadow(var36, var13, var14, 16777215 + (var15 << 24));
                    GL11.glDisable(GL11.GL_BLEND);
                    GL11.glPopMatrix();
                }
            }

            this.mc.mcProfiler.endSection();
        }

        if (this.mc.isDemo())
        {
            this.mc.mcProfiler.startSection("demo");
            var36 = "";

            if (this.mc.theWorld.getTotalWorldTime() >= 120500L)
            {
                var36 = I18n.format("demo.demoExpired");
            } else
            {
                var36 = I18n.format("demo.remainingTime", StringUtils.ticksToElapsedTime((int) (120500L - this.mc.theWorld.getTotalWorldTime())));
            }

            var13 = var8.getStringWidth(var36);
            var8.drawStringWithShadow(var36, width - var13 - 10, 5, 16777215);
            this.mc.mcProfiler.endSection();
        }

        int var21;
        int var22;
        int var23;

        if (this.mc.gameSettings.showDebugInfo)
        {
            renderDebug(width);
        }

        if (this.recordPlayingUpFor > 0)
        {
            this.mc.mcProfiler.startSection("overlayMessage");
            var34 = (float) this.recordPlayingUpFor - par1;
            var13 = (int) (var34 * 255.0F / 20.0F);

            if (var13 > 255)
            {
                var13 = 255;
            }

            if (var13 > 8)
            {
                GL11.glPushMatrix();
                GL11.glTranslatef((float) (width / 2), (float) (height - 68), 0.0F);
                GL11.glEnable(GL11.GL_BLEND);
                OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                var14 = 16777215;

                if (this.recordIsPlaying)
                {
                    var14 = Color.HSBtoRGB(var34 / 50.0F, 0.7F, 0.6F) & 16777215;
                }

                var8.drawString(this.recordPlaying, -var8.getStringWidth(this.recordPlaying) / 2, -4, var14 + (var13 << 24 & -16777216));
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glPopMatrix();
            }

            this.mc.mcProfiler.endSection();
        }

        ScoreObjective var40 = this.mc.theWorld.getScoreboard().func_96539_a(1);

        if (var40 != null)
        {
            this.func_96136_a(var40, height, width, var8);
        }

        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, (float) (height - 48), 0.0F);
        this.persistantChatGUI.drawChat(this.updateCounter);
        GL11.glPopMatrix();
        var40 = this.mc.theWorld.getScoreboard().func_96539_a(0);

        if (this.mc.gameSettings.keyBindPlayerList.getIsKeyPressed() && (!this.mc.isIntegratedServerRunning() || this.mc.thePlayer.sendQueue.playerInfoList.size() > 1 || var40 != null))
        {
            renderPlayerList(width, var40);
        }

        glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.mcProfiler.startSection("nebulaRender2D");
        EventBus.dispatch(new EventRender2D(var5, par1));
        mc.mcProfiler.endSection();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
    }

    private void renderDebug(int var6)
    {
        if (BetterF3Module.INSTANCE.isToggled())
        {
            mc.mcProfiler.startSection("nebulaDebug");
            renderCustomDebug(var6);
            mc.mcProfiler.endSection();
            return;
        }
        this.mc.mcProfiler.startSection("debug");
        GL11.glPushMatrix();
        FontRenderer var8 = mc.fontRenderer;
        var8.drawStringWithShadow("Minecraft 1.7.2 (" + this.mc.debug + ")", 2, 2, 16777215);
        var8.drawStringWithShadow(this.mc.debugInfoRenders(), 2, 12, 16777215);
        var8.drawStringWithShadow(this.mc.getEntityDebug(), 2, 22, 16777215);
        var8.drawStringWithShadow(this.mc.debugInfoEntities(), 2, 32, 16777215);
        var8.drawStringWithShadow(this.mc.getWorldProviderName(), 2, 42, 16777215);
        long var38 = Runtime.getRuntime().maxMemory();
        long var41 = Runtime.getRuntime().totalMemory();
        long var43 = Runtime.getRuntime().freeMemory();
        long var45 = var41 - var43;
        String var20 = "Used memory: " + var45 * 100L / var38 + "% (" + var45 / 1024L / 1024L + "MB) of " + var38 / 1024L / 1024L + "MB";
        int var21 = 14737632;
        this.drawString(var8, var20, var6 - var8.getStringWidth(var20) - 2, 2, 14737632);
        var20 = "Allocated memory: " + var41 * 100L / var38 + "% (" + var41 / 1024L / 1024L + "MB)";
        this.drawString(var8, var20, var6 - var8.getStringWidth(var20) - 2, 12, 14737632);
        int var22 = MathHelper.floor_double(this.mc.thePlayer.posX);
        int var23 = MathHelper.floor_double(this.mc.thePlayer.posY);
        int var24 = MathHelper.floor_double(this.mc.thePlayer.posZ);
        this.drawString(var8, String.format("x: %.5f (%d) // c: %d (%d)", Double.valueOf(this.mc.thePlayer.posX), Integer.valueOf(var22), Integer.valueOf(var22 >> 4), Integer.valueOf(var22 & 15)), 2, 64, 14737632);
        this.drawString(var8, String.format("y: %.3f (feet pos, %.3f eyes pos)", Double.valueOf(this.mc.thePlayer.boundingBox.minY), Double.valueOf(this.mc.thePlayer.posY)), 2, 72, 14737632);
        this.drawString(var8, String.format("z: %.5f (%d) // c: %d (%d)", Double.valueOf(this.mc.thePlayer.posZ), Integer.valueOf(var24), Integer.valueOf(var24 >> 4), Integer.valueOf(var24 & 15)), 2, 80, 14737632);
        int var25 = MathHelper.floor_double((double) (this.mc.thePlayer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        this.drawString(var8, "f: " + var25 + " (" + Direction.directions[var25] + ") / " + MathHelper.wrapAngleTo180_float(this.mc.thePlayer.rotationYaw), 2, 88, 14737632);

        if (this.mc.theWorld != null && this.mc.theWorld.blockExists(var22, var23, var24))
        {
            Chunk var26 = this.mc.theWorld.getChunkFromBlockCoords(var22, var24);
            this.drawString(var8, "lc: " + (var26.getTopFilledSegment() + 15) + " b: " + var26.getBiomeGenForWorldCoords(var22 & 15, var24 & 15, this.mc.theWorld.getWorldChunkManager()).biomeName + " bl: " + var26.getSavedLightValue(EnumSkyBlock.Block, var22 & 15, var23, var24 & 15) + " sl: " + var26.getSavedLightValue(EnumSkyBlock.Sky, var22 & 15, var23, var24 & 15) + " rl: " + var26.getBlockLightValue(var22 & 15, var23, var24 & 15, 0), 2, 96, 14737632);
        }

        this.drawString(var8, String.format("ws: %.3f, fs: %.3f, g: %b, fl: %d", Float.valueOf(this.mc.thePlayer.capabilities.getWalkSpeed()), Float.valueOf(this.mc.thePlayer.capabilities.getFlySpeed()), Boolean.valueOf(this.mc.thePlayer.onGround), Integer.valueOf(this.mc.theWorld.getHeightValue(var22, var24))), 2, 104, 14737632);

        if (this.mc.entityRenderer != null && this.mc.entityRenderer.isShaderActive())
        {
            this.drawString(var8, String.format("shader: %s", this.mc.entityRenderer.getShaderGroup().getShaderGroupName()), 2, 112, 14737632);
        }

        GL11.glPopMatrix();
        this.mc.mcProfiler.endSection();
    }

    private void renderCustomDebug(int width)
    {
        glPushMatrix();

        final FontRenderer font = mc.fontRenderer;
        final int color = 16777215;

        int y = 2;

        font.drawStringWithShadow("Minecraft 1.7.2", 2, y, color);
        font.drawStringWithShadow("Nebula " + ClientConfig.FULL_VERSION, 2, y += 10, color);
        font.drawStringWithShadow("LWJGL " + Sys.getVersion(), 2, y += 10, color);
        font.drawStringWithShadow("OpenGL " + glGetString(GL_VERSION), 2, y += 10, color);

        font.drawStringWithShadow("FPS: " + Minecraft.debugFPS, 2, y += 18, color);
        font.drawStringWithShadow("TPS: " + Nebula.INSTANCE.getServerManager().getAverageTPS() + " [" + Nebula.INSTANCE.getServerManager().getCurrentTPS() + "]", 2, y += 10, color);
        font.drawStringWithShadow("Chunk Updates: " + WorldRenderer.chunksUpdated, 2, y += 10, color);

        font.drawStringWithShadow(String.format("X: %.5f", mc.thePlayer.posX), 2, y += 18, color);
        font.drawStringWithShadow(String.format("Y: %.5f", mc.thePlayer.boundingBox.minY), 2, y += 10, color);
        font.drawStringWithShadow(String.format("Pose: %.5f", mc.thePlayer.posY), 2, y += 10, color);
        font.drawStringWithShadow(String.format("Z: %.5f", mc.thePlayer.posZ), 2, y += 10, color);
        font.drawStringWithShadow(String.format("Ground: %s", mc.thePlayer.onGround), 2, y += 10, color);

        font.drawStringWithShadow(String.format("Direction: %s (yaw: %.3f, pitch: %.3f)", PlayerUtil.getFacing(), mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch), 2, y += 10, color);

        // other side
        y = 2;
        String text = String.format("Java Version: %s", System.getProperty("java.version", "NULL"));
        font.drawStringWithShadow(text, width - font.getStringWidth(text) - 2, y, color);
        text = String.format("Java Vendor: %s", System.getProperty("java.vendor", "NULL"));
        font.drawStringWithShadow(text, width - font.getStringWidth(text) - 2, y += 10, color);
        text = String.format("OS: %s", System.getProperty("os.name", "NULL"));
        font.drawStringWithShadow(text, width - font.getStringWidth(text) - 2, y += 18, color);
        text = String.format("Arch: %s", System.getProperty("os.arch", "NULL"));
        font.drawStringWithShadow(text, width - font.getStringWidth(text) - 2, y += 10, color);

        double totalMemory = Runtime.getRuntime().totalMemory() * 1E-6;
        double freeMemory = Runtime.getRuntime().freeMemory() * 1E-6;
        double maxMemory = Runtime.getRuntime().maxMemory() * 1E-6;

        text = String.format("Total: %.2fMB", maxMemory);
        font.drawStringWithShadow(text, width - font.getStringWidth(text) - 2, y += 18, color);
        text = String.format("Used: %.2fMB", freeMemory);
        font.drawStringWithShadow(text, width - font.getStringWidth(text) - 2, y += 10, color);
        text = String.format("Allocated: %.2fMB", totalMemory);
        font.drawStringWithShadow(text, width - font.getStringWidth(text) - 2, y += 10, color);
        text = String.format("Available Processors: %s", Runtime.getRuntime().availableProcessors());
        font.drawStringWithShadow(text, width - font.getStringWidth(text) - 2, y += 10, color);

        if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
        {
            y += 18;
            text = String.format("Hit Pos: %s, %s, %s", mc.objectMouseOver.blockX, mc.objectMouseOver.blockY, mc.objectMouseOver.blockZ);
            font.drawStringWithShadow(text, width - font.getStringWidth(text) - 2, y, color);
            text = String.format("Side: %s (%s)", EnumFacing.values()[mc.objectMouseOver.sideHit], mc.objectMouseOver.sideHit);
            font.drawStringWithShadow(text, width - font.getStringWidth(text) - 2, y += 10, color);

            final Block block = mc.theWorld.getBlock(mc.objectMouseOver.blockX, mc.objectMouseOver.blockY, mc.objectMouseOver.blockZ);
            text = String.format("Block: %s (%s)", block.getUnlocalizedName(), block.getClass().getSimpleName());
            font.drawStringWithShadow(text, width - font.getStringWidth(text) - 2, y += 10, color);
            text = String.format("Replaceable: %s", block.getMaterial().isReplaceable());
            font.drawStringWithShadow(text, width - font.getStringWidth(text) - 2, y += 10, color);
            text = String.format("Liquid: %s", block.getMaterial().isLiquid());
            font.drawStringWithShadow(text, width - font.getStringWidth(text) - 2, y += 10, color);
            text = String.format("Can Burn: %s", block.getMaterial().getCanBurn());
            font.drawStringWithShadow(text, width - font.getStringWidth(text) - 2, y += 10, color);
            text = String.format("Solid: %s", block.getMaterial().isSolid());
            font.drawStringWithShadow(text, width - font.getStringWidth(text) - 2, y += 10, color);
            text = String.format("Opaque: %s", block.getMaterial().isOpaque());
            font.drawStringWithShadow(text, width - font.getStringWidth(text) - 2, y += 10, color);
            text = String.format("Collidable: %s", block.isCollidable());
            font.drawStringWithShadow(text, width - font.getStringWidth(text) - 2, y += 10, color);
            text = String.format("Normal: %s", block.isNormalCube());
            font.drawStringWithShadow(text, width - font.getStringWidth(text) - 2, y += 10, color);
            text = String.format("Hardness: %s", block.blockHardness);
            font.drawStringWithShadow(text, width - font.getStringWidth(text) - 2, y += 10, color);
            text = String.format("Slipperiness: %s", block.slipperiness);
            font.drawStringWithShadow(text, width - font.getStringWidth(text) - 2, y += 10, color);
            text = String.format("Metadata: %s", mc.theWorld.getBlockMetadata(mc.objectMouseOver.blockX, mc.objectMouseOver.blockY, mc.objectMouseOver.blockZ));
            font.drawStringWithShadow(text, width - font.getStringWidth(text) - 2, y += 10, color);
        }

        if (mc.pointedEntity != null)
        {
            Entity entity = mc.pointedEntity;

            y += 18;
            text = String.format("UUID: %s", entity.getUniqueID());
            font.drawStringWithShadow(text, width - font.getStringWidth(text) - 2, y, color);
            text = String.format("Command Sender Name: %s", entity.getCommandSenderName());
            font.drawStringWithShadow(text, width - font.getStringWidth(text) - 2, y += 10, color);
            text = String.format("Entity ID: %s", entity.getEntityId());
            font.drawStringWithShadow(text, width - font.getStringWidth(text) - 2, y += 10, color);
            text = String.format("X: %.2f", entity.posX);
            font.drawStringWithShadow(text, width - font.getStringWidth(text) - 2, y += 10, color);
            text = String.format("X: %.2f", entity.posY);
            font.drawStringWithShadow(text, width - font.getStringWidth(text) - 2, y += 10, color);
            text = String.format("X: %.2f", entity.posZ);
            font.drawStringWithShadow(text, width - font.getStringWidth(text) - 2, y += 10, color);

            final DataWatcher watcher = entity.getDataWatcher();
            final List<DataWatcher.WatchableObject> watched = watcher.getAllWatched();
            if (!watched.isEmpty())
            {
                y += 18;
                text = (EnumChatFormatting.GRAY + EnumChatFormatting.UNDERLINE.toString() + "Watcher Data:");
                font.drawStringWithShadow(text, width - font.getStringWidth(text) - 2, y, color);
                for (final DataWatcher.WatchableObject obj : watched)
                {
                    text = String.format("%s(%s) %s%s", EnumChatFormatting.GRAY, obj.getDataValueId(), EnumChatFormatting.BLUE, obj.getObject());
                    font.drawStringWithShadow(text, width - font.getStringWidth(text) - 2, y += 10, color);
                }
            }
        }

        glPopMatrix();
    }

    private void renderPlayerList(final int screenWidth, final ScoreObjective objective)
    {
        if (ExtraTabModule.INSTANCE.isToggled() && ExtraTabModule.INSTANCE.customSetting.getValue())
        {
            mc.mcProfiler.startSection("nebulaPlayerList");
            renderCustomTabList(screenWidth);
            mc.mcProfiler.endSection();
            return;
        }
        this.mc.mcProfiler.startSection("playerList");
        NetHandlerPlayClient client = this.mc.thePlayer.sendQueue;
        List<GuiPlayerInfo> playerInfo = client.playerInfoList;
        FontRenderer fontRenderer = this.mc.fontRenderer;
        int maxPlayerCount = client.currentServerMaxPlayers;
        int var16 = maxPlayerCount;
        int rows;

        for (rows = 1; var16 > 20; var16 = (maxPlayerCount + rows - 1) / rows)
        {
            ++rows;
        }

        int widthPerSection = 300 / rows;

        if (widthPerSection > 150)
        {
            widthPerSection = 150;
        }

        int posX = (screenWidth - rows * widthPerSection) / 2;
        byte posY = 10;
        drawRect(posX - 1, posY - 1, posX + widthPerSection * rows, posY + 9 * var16, Integer.MIN_VALUE);

        for (int i = 0; i < maxPlayerCount; ++i)
        {
            int x = posX + i % rows * widthPerSection;
            int y = posY + i / rows * 9;
            drawRect(x, y, x + widthPerSection - 1, y + 8, 553648127);
            glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glEnable(GL11.GL_ALPHA_TEST);

            if (i < playerInfo.size())
            {
                GuiPlayerInfo info = playerInfo.get(i);

                int offset = 0;
                if (ExtraTabModule.INSTANCE.isToggled()
                        && ExtraTabModule.INSTANCE.showPlayerHeadSetting.getValue())
                {
                    final int texSize = 7;
                    final DynamicTexture texture = HeadDownloader.getOrDownloadTexture(info.name, texSize);
                    if (texture != null)
                    {
                        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                        glBindTexture(GL_TEXTURE_2D, texture.getGlTextureId());
                        glPushMatrix();
                        glBegin(GL_QUADS);
                        {
                            glTexCoord2d(0, 0);
                            glVertex2d(x + 1, y + 0.5);

                            glTexCoord2d(0, 1);
                            glVertex2d(x + 1, y + texSize + 0.5);

                            glTexCoord2d(1, 1);
                            glVertex2d(x + texSize + 1, y + texSize + 0.5);

                            glTexCoord2d(1, 0);
                            glVertex2d(x + texSize + 1, y + 0.5);
                        }
                        glEnd();
                        glPopMatrix();
                        offset = texSize + 2;
                    }
                }

                String name;
                if (ExtraTabModule.INSTANCE.isToggled()
                        && ExtraTabModule.INSTANCE.highlightFriendsSetting.getValue()
                        && (Nebula.INSTANCE.getFriendManager().isFriend(info.name)
                        || info.name.equals(mc.thePlayer.getCommandSenderName())))
                {
                    name = EnumChatFormatting.NEBULA_CLIENT_COLOR + info.name;
                } else
                {
                    final ScorePlayerTeam team = mc.theWorld.getScoreboard().getPlayersTeam(info.name);
                    name = ScorePlayerTeam.formatPlayerName(team, info.name);
                }
                name = NameProtectModule.INSTANCE.protect(name);
                fontRenderer.drawStringWithShadow(name, x + offset + 1, y, 16777215);

                if (objective != null)
                {
                    int var27 = x + fontRenderer.getStringWidth(name) + 5;
                    int var28 = x + widthPerSection - 12 - 5;

                    if (var28 - var27 > 5)
                    {
                        Score var29 = objective.getScoreboard().func_96529_a(info.name, objective);
                        String var30 = EnumChatFormatting.YELLOW + "" + var29.getScorePoints();
                        fontRenderer.drawStringWithShadow(var30, var28 - fontRenderer.getStringWidth(var30), y, 16777215);
                    }
                }

                glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

                if (ExtraTabModule.INSTANCE.isToggled()
                        && ExtraTabModule.INSTANCE.showBarsSetting.getValue())
                {
                    this.mc.getTextureManager().bindTexture(icons);

                    byte barIndex;
                    if (info.responseTime < 0)
                    {
                        barIndex = 5;
                    } else if (info.responseTime < 150)
                    {
                        barIndex = 0;
                    } else if (info.responseTime < 300)
                    {
                        barIndex = 1;
                    } else if (info.responseTime < 600)
                    {
                        barIndex = 2;
                    } else if (info.responseTime < 1000)
                    {
                        barIndex = 3;
                    } else
                    {
                        barIndex = 4;
                    }

                    // draw the response time
                    this.zLevel += 100.0F;
                    this.drawTexturedModalRect(x + widthPerSection - 12, y, 0, 176 + barIndex * 8, 10, 8);
                    this.zLevel -= 100.0F;
                }
            }
        }
        this.mc.mcProfiler.endSection();
    }

    private void renderCustomTabList(final int screenWidth)
    {
        final List<GuiPlayerInfo> playerInfo = mc.thePlayer.sendQueue.playerInfoList;

        // render 1 column for every 12 players
        final int column = 1 + ((playerInfo.size() - 1) / 12);
        final int sizePerItem = 115;
        int width = column * sizePerItem;
        int amount = Math.min(playerInfo.size(), 12);
        final int fontHeight = (int) Fonts.POPPINS.getFontHeight();

        int posX = (screenWidth - width) / 2;
        byte posY = 10;
        drawRect(posX - 1, posY - 1, posX + width, posY + fontHeight * amount, Integer.MIN_VALUE);

        for (int i = 0; i < playerInfo.size(); ++i)
        {
            final GuiPlayerInfo info = playerInfo.get(i);
            int x = (int) (posX + Math.floor(i / 12.0) * sizePerItem);
            int y = posY + (i % 12) * fontHeight;
            int offset = 0;

            GL11.glEnable(GL11.GL_ALPHA_TEST);

            drawRect(x, y, x + sizePerItem - 1, y + fontHeight - 1, 553648127);

            if (ExtraTabModule.INSTANCE.showPlayerHeadSetting.getValue())
            {
                final int texSize = fontHeight - 2;
                final DynamicTexture texture = HeadDownloader.getOrDownloadTexture(info.name, texSize);
                if (texture != null)
                {
                    glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                    glBindTexture(GL_TEXTURE_2D, texture.getGlTextureId());
                    glPushMatrix();
                    glBegin(GL_QUADS);
                    {
                        glTexCoord2d(0, 0);
                        glVertex2d(x + 1, y + 0.5);

                        glTexCoord2d(0, 1);
                        glVertex2d(x + 1, y + texSize + 0.5);

                        glTexCoord2d(1, 1);
                        glVertex2d(x + texSize + 1, y + texSize + 0.5);

                        glTexCoord2d(1, 0);
                        glVertex2d(x + texSize + 1, y + 0.5);
                    }
                    glEnd();
                    glPopMatrix();
                    offset = texSize + 2;
                }
            }

            String name;
            if (ExtraTabModule.INSTANCE.highlightFriendsSetting.getValue()
                    && (Nebula.INSTANCE.getFriendManager().isFriend(info.name)
                    || info.name.equals(mc.thePlayer.getCommandSenderName())))
            {
                name = EnumChatFormatting.NEBULA_CLIENT_COLOR + info.name;
            } else
            {
                final ScorePlayerTeam team = mc.theWorld.getScoreboard().getPlayersTeam(info.name);
                name = ScorePlayerTeam.formatPlayerName(team, info.name);
            }
            name = NameProtectModule.INSTANCE.protect(name);
            Fonts.POPPINS.drawStringShadow(name, x + 1 + offset, y - 1, 16777215);

            glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

            if (ExtraTabModule.INSTANCE.showBarsSetting.getValue())
            {
                this.mc.getTextureManager().bindTexture(icons);
                byte barIndex;
                if (info.responseTime < 0)
                {
                    barIndex = 5;
                } else if (info.responseTime < 150)
                {
                    barIndex = 0;
                } else if (info.responseTime < 300)
                {
                    barIndex = 1;
                } else if (info.responseTime < 600)
                {
                    barIndex = 2;
                } else if (info.responseTime < 1000)
                {
                    barIndex = 3;
                } else
                {
                    barIndex = 4;
                }
                // draw the response time
                this.zLevel += 100.0F;
                this.drawTexturedModalRect(x + sizePerItem - 12, y + 1, 0, 176 + barIndex * 8, 10, 8);
                this.zLevel -= 100.0F;
            }
        }
    }

    private void func_96136_a(ScoreObjective par1ScoreObjective, int par2, int par3, FontRenderer par4FontRenderer)
    {
        Scoreboard var5 = par1ScoreObjective.getScoreboard();
        Collection var6 = var5.func_96534_i(par1ScoreObjective);

        if (var6.size() <= 15)
        {
            int var7 = par4FontRenderer.getStringWidth(par1ScoreObjective.getDisplayName());
            String var11;

            for (Iterator var8 = var6.iterator(); var8.hasNext(); var7 = Math.max(var7, par4FontRenderer.getStringWidth(var11)))
            {
                Score var9 = (Score) var8.next();
                ScorePlayerTeam var10 = var5.getPlayersTeam(var9.getPlayerName());
                var11 = ScorePlayerTeam.formatPlayerName(var10, var9.getPlayerName()) + ": " + EnumChatFormatting.RED + var9.getScorePoints();
            }

            int var22 = var6.size() * par4FontRenderer.FONT_HEIGHT;
            int var23 = par2 / 2 + var22 / 3;
            byte var24 = 3;
            int var25 = par3 - var7 - var24;
            int var12 = 0;
            Iterator var13 = var6.iterator();

            while (var13.hasNext())
            {
                Score var14 = (Score) var13.next();
                ++var12;
                ScorePlayerTeam var15 = var5.getPlayersTeam(var14.getPlayerName());
                String var16 = ScorePlayerTeam.formatPlayerName(var15, var14.getPlayerName());
                String var17 = EnumChatFormatting.RED + "" + var14.getScorePoints();
                int var19 = var23 - var12 * par4FontRenderer.FONT_HEIGHT;
                int var20 = par3 - var24 + 2;
                drawRect(var25 - 2, var19, var20, var19 + par4FontRenderer.FONT_HEIGHT, 1342177280);
                par4FontRenderer.drawString(var16, var25, var19, 553648127);
                par4FontRenderer.drawString(var17, var20 - par4FontRenderer.getStringWidth(var17), var19, 553648127);

                if (var12 == var6.size())
                {
                    String var21 = par1ScoreObjective.getDisplayName();
                    drawRect(var25 - 2, var19 - par4FontRenderer.FONT_HEIGHT - 1, var20, var19 - 1, 1610612736);
                    drawRect(var25 - 2, var19 - 1, var20, var19, 1342177280);
                    par4FontRenderer.drawString(var21, var25 + var7 / 2 - par4FontRenderer.getStringWidth(var21) / 2, var19 - par4FontRenderer.FONT_HEIGHT, 553648127);
                }
            }
        }
    }

    private void drawHUD(int width, int height)
    {
        boolean var3 = this.mc.thePlayer.hurtResistantTime / 3 % 2 == 1;

        if (this.mc.thePlayer.hurtResistantTime < 10)
        {
            var3 = false;
        }

        int var4 = MathHelper.ceiling_float_int(this.mc.thePlayer.getHealth());
        int var5 = MathHelper.ceiling_float_int(this.mc.thePlayer.prevHealth);
        this.rand.setSeed(this.updateCounter * 312871L);
        boolean var6 = false;
        FoodStats var7 = this.mc.thePlayer.getFoodStats();
        int var8 = var7.getFoodLevel();
        int var9 = var7.getPrevFoodLevel();
        IAttributeInstance var10 = this.mc.thePlayer.getEntityAttribute(SharedMonsterAttributes.maxHealth);
        int var11 = width / 2 - 91;
        int var12 = width / 2 + 91;
        int var13 = height - 39;
        float var14 = (float) var10.getAttributeValue();
        float var15 = this.mc.thePlayer.getAbsorptionAmount();
        int var16 = MathHelper.ceiling_float_int((var14 + var15) / 2.0F / 10.0F);
        int var17 = Math.max(10 - (var16 - 2), 3);
        int var18 = var13 - (var16 - 1) * var17 - 10;
        float var19 = var15;
        int var20 = this.mc.thePlayer.getTotalArmorValue();
        int var21 = -1;

        if (this.mc.thePlayer.isPotionActive(Potion.regeneration))
        {
            var21 = this.updateCounter % MathHelper.ceiling_float_int(var14 + 5.0F);
        }

        this.mc.mcProfiler.startSection("armor");
        int var22;
        int var23;

        for (var22 = 0; var22 < 10; ++var22)
        {
            if (var20 > 0)
            {
                var23 = var11 + var22 * 8;

                if (var22 * 2 + 1 < var20)
                {
                    this.drawTexturedModalRect(var23, var18, 34, 9, 9, 9);
                }

                if (var22 * 2 + 1 == var20)
                {
                    this.drawTexturedModalRect(var23, var18, 25, 9, 9, 9);
                }

                if (var22 * 2 + 1 > var20)
                {
                    this.drawTexturedModalRect(var23, var18, 16, 9, 9, 9);
                }
            }
        }

        this.mc.mcProfiler.endStartSection("health");
        int var25;
        int var26;
        int var27;

        for (var22 = MathHelper.ceiling_float_int((var14 + var15) / 2.0F) - 1; var22 >= 0; --var22)
        {
            var23 = 16;

            if (this.mc.thePlayer.isPotionActive(Potion.poison))
            {
                var23 += 36;
            } else if (this.mc.thePlayer.isPotionActive(Potion.wither))
            {
                var23 += 72;
            }

            byte var24 = 0;

            if (var3)
            {
                var24 = 1;
            }

            var25 = MathHelper.ceiling_float_int((float) (var22 + 1) / 10.0F) - 1;
            var26 = var11 + var22 % 10 * 8;
            var27 = var13 - var25 * var17;

            if (var4 <= 4)
            {
                var27 += this.rand.nextInt(2);
            }

            if (var22 == var21)
            {
                var27 -= 2;
            }

            byte var28 = 0;

            if (this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled())
            {
                var28 = 5;
            }

            this.drawTexturedModalRect(var26, var27, 16 + var24 * 9, 9 * var28, 9, 9);

            if (var3)
            {
                if (var22 * 2 + 1 < var5)
                {
                    this.drawTexturedModalRect(var26, var27, var23 + 54, 9 * var28, 9, 9);
                }

                if (var22 * 2 + 1 == var5)
                {
                    this.drawTexturedModalRect(var26, var27, var23 + 63, 9 * var28, 9, 9);
                }
            }

            if (var19 > 0.0F)
            {
                if (var19 == var15 && var15 % 2.0F == 1.0F)
                {
                    this.drawTexturedModalRect(var26, var27, var23 + 153, 9 * var28, 9, 9);
                } else
                {
                    this.drawTexturedModalRect(var26, var27, var23 + 144, 9 * var28, 9, 9);
                }

                var19 -= 2.0F;
            } else
            {
                if (var22 * 2 + 1 < var4)
                {
                    this.drawTexturedModalRect(var26, var27, var23 + 36, 9 * var28, 9, 9);
                }

                if (var22 * 2 + 1 == var4)
                {
                    this.drawTexturedModalRect(var26, var27, var23 + 45, 9 * var28, 9, 9);
                }
            }
        }

        Entity var34 = this.mc.thePlayer.ridingEntity;
        int var35;

        if (var34 == null)
        {
            this.mc.mcProfiler.endStartSection("food");

            int saturation = 0;
            if (AppleSkinModule.INSTANCE.isToggled())
            {
                saturation = (int) (mc.thePlayer.getFoodStats().getSaturationLevel() / 2.0f);
            }

            for (var23 = 0; var23 < 10; ++var23)
            {
                var35 = var13;
                var25 = 16;
                byte var36 = 0;

                glColor4f(1, 1, 1, 1);
                if (saturation > var23)
                {
                    RenderUtil.setGLColor(AppleSkinModule.INSTANCE.colorSetting.getValue().getRGB());
                } else
                {
                    glColor4f(1, 1, 1, 1);
                }

                if (this.mc.thePlayer.isPotionActive(Potion.hunger))
                {
                    var25 += 36;
                    var36 = 13;
                }

                if (this.mc.thePlayer.getFoodStats().getSaturationLevel() <= 0.0F && this.updateCounter % (var8 * 3 + 1) == 0)
                {
                    var35 = var13 + (this.rand.nextInt(3) - 1);
                }

                if (var6)
                {
                    var36 = 1;
                }

                var27 = var12 - var23 * 8 - 9;
                this.drawTexturedModalRect(var27, var35, 16 + var36 * 9, 27, 9, 9);

                if (var6)
                {
                    if (var23 * 2 + 1 < var9)
                    {
                        this.drawTexturedModalRect(var27, var35, var25 + 54, 27, 9, 9);
                    }

                    if (var23 * 2 + 1 == var9)
                    {
                        this.drawTexturedModalRect(var27, var35, var25 + 63, 27, 9, 9);
                    }
                }

                if (var23 * 2 + 1 < var8)
                {
                    this.drawTexturedModalRect(var27, var35, var25 + 36, 27, 9, 9);
                }

                if (var23 * 2 + 1 == var8)
                {
                    this.drawTexturedModalRect(var27, var35, var25 + 45, 27, 9, 9);
                }
            }
        } else if (var34 instanceof EntityLivingBase)
        {
            this.mc.mcProfiler.endStartSection("mountHealth");
            EntityLivingBase var37 = (EntityLivingBase) var34;
            var35 = (int) Math.ceil(var37.getHealth());
            float var38 = var37.getMaxHealth();
            var26 = (int) (var38 + 0.5F) / 2;

            if (var26 > 30)
            {
                var26 = 30;
            }

            var27 = var13;

            for (int var39 = 0; var26 > 0; var39 += 20)
            {
                int var29 = Math.min(var26, 10);
                var26 -= var29;

                for (int var30 = 0; var30 < var29; ++var30)
                {
                    byte var31 = 52;
                    byte var32 = 0;

                    if (var6)
                    {
                        var32 = 1;
                    }

                    int var33 = var12 - var30 * 8 - 9;
                    this.drawTexturedModalRect(var33, var27, var31 + var32 * 9, 9, 9, 9);

                    if (var30 * 2 + 1 + var39 < var35)
                    {
                        this.drawTexturedModalRect(var33, var27, var31 + 36, 9, 9, 9);
                    }

                    if (var30 * 2 + 1 + var39 == var35)
                    {
                        this.drawTexturedModalRect(var33, var27, var31 + 45, 9, 9, 9);
                    }
                }

                var27 -= 10;
            }
        }

        this.mc.mcProfiler.endStartSection("air");



        if (this.mc.thePlayer.isInsideOfMaterial(Material.water) && !EventBus.dispatch(new EventRenderWaterEffects()))
        {
            var23 = this.mc.thePlayer.getAir();
            var35 = MathHelper.ceiling_double_int((double) (var23 - 2) * 10.0D / 300.0D);
            var25 = MathHelper.ceiling_double_int((double) var23 * 10.0D / 300.0D) - var35;

            for (var26 = 0; var26 < var35 + var25; ++var26)
            {
                if (var26 < var35)
                {
                    this.drawTexturedModalRect(var12 - var26 * 8 - 9, var18, 16, 18, 9, 9);
                } else
                {
                    this.drawTexturedModalRect(var12 - var26 * 8 - 9, var18, 25, 18, 9, 9);
                }
            }
        }

        this.mc.mcProfiler.endSection();
    }

    /**
     * Renders dragon's (boss) health on the HUD
     */
    private void renderBossHealth()
    {
        if (BossStatus.bossName != null && BossStatus.statusBarTime > 0)
        {
            --BossStatus.statusBarTime;
            FontRenderer var1 = this.mc.fontRenderer;
            ScaledResolution var2 = new ScaledResolution(this.mc.gameSettings, this.mc.displayWidth, this.mc.displayHeight);
            int var3 = var2.getScaledWidth();
            short var4 = 182;
            int var5 = var3 / 2 - var4 / 2;
            int var6 = (int) (BossStatus.healthScale * (float) (var4 + 1));
            byte var7 = 12;
            this.drawTexturedModalRect(var5, var7, 0, 74, var4, 5);
            this.drawTexturedModalRect(var5, var7, 0, 74, var4, 5);

            if (var6 > 0)
            {
                this.drawTexturedModalRect(var5, var7, 0, 79, var6, 5);
            }

            String var8 = BossStatus.bossName;
            var1.drawStringWithShadow(var8, var3 / 2 - var1.getStringWidth(var8) / 2, var7 - 10, 16777215);
            glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(icons);
        }
    }

    private void renderPumpkinBlur(int par1, int par2)
    {
        if (NoRenderModule.INSTANCE.isToggled()
                && NoRenderModule.INSTANCE.pumpkinSetting.getValue())
        {
            return;
        }
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        this.mc.getTextureManager().bindTexture(pumpkinBlurTexPath);
        Tessellator var3 = Tessellator.instance;
        var3.startDrawingQuads();
        var3.addVertexWithUV(0.0D, par2, -90.0D, 0.0D, 1.0D);
        var3.addVertexWithUV(par1, par2, -90.0D, 1.0D, 1.0D);
        var3.addVertexWithUV(par1, 0.0D, -90.0D, 1.0D, 0.0D);
        var3.addVertexWithUV(0.0D, 0.0D, -90.0D, 0.0D, 0.0D);
        var3.draw();
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * Renders the vignette. Args: vignetteBrightness, width, height
     */
    private void renderVignette(float par1, int par2, int par3)
    {
        par1 = 1.0F - par1;

        if (par1 < 0.0F)
        {
            par1 = 0.0F;
        }

        if (par1 > 1.0F)
        {
            par1 = 1.0F;
        }

        this.prevVignetteBrightness = (float) ((double) this.prevVignetteBrightness + (double) (par1 - this.prevVignetteBrightness) * 0.01D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        OpenGlHelper.glBlendFunc(0, 769, 1, 0);
        glColor4f(this.prevVignetteBrightness, this.prevVignetteBrightness, this.prevVignetteBrightness, 1.0F);
        this.mc.getTextureManager().bindTexture(vignetteTexPath);
        Tessellator var4 = Tessellator.instance;
        var4.startDrawingQuads();
        var4.addVertexWithUV(0.0D, par3, -90.0D, 0.0D, 1.0D);
        var4.addVertexWithUV(par2, par3, -90.0D, 1.0D, 1.0D);
        var4.addVertexWithUV(par2, 0.0D, -90.0D, 1.0D, 0.0D);
        var4.addVertexWithUV(0.0D, 0.0D, -90.0D, 0.0D, 0.0D);
        var4.draw();
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
    }

    private void renderPortalOverlay(float par1, int par2, int par3)
    {
        if (NoRenderModule.INSTANCE.isToggled()
                && NoRenderModule.INSTANCE.portalSetting.getValue())
        {
            return;
        }
        if (par1 < 1.0F)
        {
            par1 *= par1;
            par1 *= par1;
            par1 = par1 * 0.8F + 0.2F;
        }

        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        glColor4f(1.0F, 1.0F, 1.0F, par1);
        IIcon var4 = Blocks.portal.getBlockTextureFromSide(1);
        this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        float var5 = var4.getMinU();
        float var6 = var4.getMinV();
        float var7 = var4.getMaxU();
        float var8 = var4.getMaxV();
        Tessellator var9 = Tessellator.instance;
        var9.startDrawingQuads();
        var9.addVertexWithUV(0.0D, par3, -90.0D, var5, var8);
        var9.addVertexWithUV(par2, par3, -90.0D, var7, var8);
        var9.addVertexWithUV(par2, 0.0D, -90.0D, var7, var6);
        var9.addVertexWithUV(0.0D, 0.0D, -90.0D, var5, var6);
        var9.draw();
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * Renders the specified item of the inventory slot at the specified location. Args: slot, x, y, partialTick
     */
    private void renderInventorySlot(int par1, int par2, int par3, float par4)
    {
        ItemStack var5 = this.mc.thePlayer.inventory.mainInventory[par1];

        if (var5 != null)
        {
            float var6 = (float) var5.animationsToGo - par4;

            if (var6 > 0.0F)
            {
                GL11.glPushMatrix();
                float var7 = 1.0F + var6 / 5.0F;
                GL11.glTranslatef((float) (par2 + 8), (float) (par3 + 12), 0.0F);
                GL11.glScalef(1.0F / var7, (var7 + 1.0F) / 2.0F, 1.0F);
                GL11.glTranslatef((float) (-(par2 + 8)), (float) (-(par3 + 12)), 0.0F);
            }

            itemRenderer.renderItemAndEffectIntoGUI(this.mc.fontRenderer, this.mc.getTextureManager(), var5, par2, par3);

            if (var6 > 0.0F)
            {
                GL11.glPopMatrix();
            }

            itemRenderer.renderItemOverlayIntoGUI(this.mc.fontRenderer, this.mc.getTextureManager(), var5, par2, par3);
        }
    }

    /**
     * The update tick for the ingame UI
     */
    public void updateTick()
    {
        if (this.recordPlayingUpFor > 0)
        {
            --this.recordPlayingUpFor;
        }

        ++this.updateCounter;

        if (this.mc.thePlayer != null)
        {
            ItemStack var1 = this.mc.thePlayer.inventory.getCurrentItem();

            if (var1 == null)
            {
                this.remainingHighlightTicks = 0;
            } else if (this.highlightingItemStack != null && var1.getItem() == this.highlightingItemStack.getItem() && ItemStack.areItemStackTagsEqual(var1, this.highlightingItemStack) && (var1.isItemStackDamageable() || var1.getItemDamage() == this.highlightingItemStack.getItemDamage()))
            {
                if (this.remainingHighlightTicks > 0)
                {
                    --this.remainingHighlightTicks;
                }
            } else
            {
                this.remainingHighlightTicks = 40;
            }

            this.highlightingItemStack = var1;
        }
    }

    public void setRecordPlayingMessage(String par1Str)
    {
        this.func_110326_a("Now playing: " + par1Str, true);
    }

    public void func_110326_a(String par1Str, boolean par2)
    {
        this.recordPlaying = par1Str;
        this.recordPlayingUpFor = 60;
        this.recordIsPlaying = par2;
    }

    public GuiNewChat getChatGui()
    {
        return this.persistantChatGUI;
    }

    public int getUpdateCounter()
    {
        return this.updateCounter;
    }
}
