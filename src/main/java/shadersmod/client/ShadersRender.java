package shadersmod.client;

import java.nio.IntBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.culling.Frustrum;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.src.Config;
import net.minecraft.src.GlStateManager;
import net.minecraft.src.Reflector;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class ShadersRender
{
    private static final ResourceLocation END_PORTAL_TEXTURE = new ResourceLocation("textures/entity/end_portal.png");

    public static void setFrustrumPosition(Frustrum frustrum, double x, double y, double z)
    {
        frustrum.setPosition(x, y, z);
    }

    public static void beginTerrainSolid()
    {
        if (Shaders.isRenderingWorld)
        {
            Shaders.fogEnabled = true;
            Shaders.useProgram(7);
        }
    }

    public static void beginTerrainCutoutMipped()
    {
        if (Shaders.isRenderingWorld)
        {
            Shaders.useProgram(7);
        }
    }

    public static void beginTerrainCutout()
    {
        if (Shaders.isRenderingWorld)
        {
            Shaders.useProgram(7);
        }
    }

    public static void endTerrain()
    {
        if (Shaders.isRenderingWorld)
        {
            Shaders.useProgram(3);
        }
    }

    public static void beginTranslucent()
    {
        if (Shaders.isRenderingWorld)
        {
            if (Shaders.usedDepthBuffers >= 2)
            {
                GlStateManager.setActiveTexture(33995);
                Shaders.checkGLError("pre copy depth");
                GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 0, 0, Shaders.renderWidth, Shaders.renderHeight);
                Shaders.checkGLError("copy depth");
                GlStateManager.setActiveTexture(33984);
            }

            Shaders.useProgram(12);
        }
    }

    public static void endTranslucent()
    {
        if (Shaders.isRenderingWorld)
        {
            Shaders.useProgram(3);
        }
    }

    public static void renderHand0(EntityRenderer er, float par1, int par2)
    {
        if (!Shaders.isShadowPass)
        {
            boolean blockTranslucentMain = Shaders.isItemToRenderMainTranslucent();

            if (!blockTranslucentMain)
            {
                Shaders.readCenterDepth();
                Shaders.beginHand(false);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                er.renderHand(par1, par2, true, false, false);
                Shaders.endHand();
                Shaders.setHandRenderedMain(true);
            }
        }
    }

    public static void renderHand1(EntityRenderer er, float par1, int par2)
    {
        if (!Shaders.isShadowPass && !Shaders.isHandRenderedMain())
        {
            Shaders.readCenterDepth();
            GlStateManager.enableBlend();
            Shaders.beginHand(true);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            er.renderHand(par1, par2, true, false, true);
            Shaders.endHand();
            Shaders.setHandRenderedMain(true);
        }
    }

    public static void renderItemFP(ItemRenderer itemRenderer, float par1, boolean renderTranslucent)
    {
        Shaders.setRenderingFirstPersonHand(true);
        GlStateManager.depthMask(true);

        if (renderTranslucent)
        {
            GlStateManager.depthFunc(519);
            GL11.glPushMatrix();
            IntBuffer drawBuffers = Shaders.activeDrawBuffers;
            Shaders.setDrawBuffers(Shaders.drawBuffersNone);
            Shaders.renderItemKeepDepthMask = true;
            itemRenderer.renderItemInFirstPerson(par1);
            Shaders.renderItemKeepDepthMask = false;
            Shaders.setDrawBuffers(drawBuffers);
            GL11.glPopMatrix();
        }

        GlStateManager.depthFunc(515);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        itemRenderer.renderItemInFirstPerson(par1);
        Shaders.setRenderingFirstPersonHand(false);
    }

    public static void renderFPOverlay(EntityRenderer er, float par1, int par2)
    {
        if (!Shaders.isShadowPass)
        {
            Shaders.beginFPOverlay();
            er.renderHand(par1, par2, false, true, false);
            Shaders.endFPOverlay();
        }
    }

    public static void beginBlockDamage()
    {
        if (Shaders.isRenderingWorld)
        {
            Shaders.useProgram(11);

            if (Shaders.programsID[11] == Shaders.programsID[7])
            {
                Shaders.setDrawBuffers(Shaders.drawBuffersColorAtt0);
                GlStateManager.depthMask(false);
            }
        }
    }

    public static void endBlockDamage()
    {
        if (Shaders.isRenderingWorld)
        {
            GlStateManager.depthMask(true);
            Shaders.useProgram(3);
        }
    }

    public static void renderShadowMap(EntityRenderer entityRenderer, int pass, float partialTicks, long finishTimeNano)
    {
        if (Shaders.usedShadowDepthBuffers > 0 && --Shaders.shadowPassCounter <= 0)
        {
            Minecraft mc = Minecraft.getMinecraft();
            mc.mcProfiler.endStartSection("shadow pass");
            RenderGlobal renderGlobal = mc.renderGlobal;
            Shaders.isShadowPass = true;
            Shaders.shadowPassCounter = Shaders.shadowPassInterval;
            Shaders.preShadowPassThirdPersonView = mc.gameSettings.thirdPersonView;
            mc.gameSettings.thirdPersonView = 1;
            Shaders.checkGLError("pre shadow");
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glPushMatrix();
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glPushMatrix();
            mc.mcProfiler.endStartSection("shadow clear");
            EXTFramebufferObject.glBindFramebufferEXT(36160, Shaders.sfb);
            Shaders.checkGLError("shadow bind sfb");
            Shaders.useProgram(30);
            mc.mcProfiler.endStartSection("shadow camera");
            entityRenderer.setupCameraTransform(partialTicks, 2);
            Shaders.setCameraShadow(partialTicks);
            ActiveRenderInfo.updateRenderInfo(mc.thePlayer, mc.gameSettings.thirdPersonView == 2);
            Shaders.checkGLError("shadow camera");
            GL20.glDrawBuffers(Shaders.sfbDrawBuffers);
            Shaders.checkGLError("shadow drawbuffers");
            GL11.glReadBuffer(0);
            Shaders.checkGLError("shadow readbuffer");
            EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36096, 3553, Shaders.sfbDepthTextures.get(0), 0);

            if (Shaders.usedShadowColorBuffers != 0)
            {
                EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064, 3553, Shaders.sfbColorTextures.get(0), 0);
            }

            Shaders.checkFramebufferStatus("shadow fb");
            GL11.glClearColor(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glClear(Shaders.usedShadowColorBuffers != 0 ? GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT : GL11.GL_DEPTH_BUFFER_BIT);
            Shaders.checkGLError("shadow clear");
            mc.mcProfiler.endStartSection("shadow frustum");
            ClippingHelper clippingHelper = ClippingHelperShadow.getInstance();
            mc.mcProfiler.endStartSection("shadow culling");
            Frustrum frustum = new Frustrum(clippingHelper);
            EntityLivingBase viewEntity = mc.renderViewEntity;
            double viewPosX = viewEntity.lastTickPosX + (viewEntity.posX - viewEntity.lastTickPosX) * (double)partialTicks;
            double viewPosY = viewEntity.lastTickPosY + (viewEntity.posY - viewEntity.lastTickPosY) * (double)partialTicks;
            double viewPosZ = viewEntity.lastTickPosZ + (viewEntity.posZ - viewEntity.lastTickPosZ) * (double)partialTicks;
            frustum.setPosition(viewPosX, viewPosY, viewPosZ);
            GlStateManager.shadeModel(7425);
            GlStateManager.enableDepth();
            GlStateManager.depthFunc(515);
            GlStateManager.depthMask(true);
            GlStateManager.colorMask(true, true, true, true);
            GlStateManager.disableCull();
            mc.mcProfiler.endStartSection("shadow prepareterrain");
            mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            mc.mcProfiler.endStartSection("shadow setupterrain");
            boolean frameCount = false;
            int var19 = entityRenderer.frameCount;
            entityRenderer.frameCount = var19 + 1;
            WorldRenderer[] worldRenderers = renderGlobal.worldRenderers;

            for (int i = 0; i < worldRenderers.length; ++i)
            {
                if (!worldRenderers[i].skipAllRenderPasses())
                {
                    worldRenderers[i].isInFrustum = true;
                }
            }

            mc.mcProfiler.endStartSection("shadow updatechunks");
            mc.mcProfiler.endStartSection("shadow terrain");
            GlStateManager.matrixMode(5888);
            GlStateManager.pushMatrix();
            GlStateManager.enableAlpha();
            renderGlobal.renderAllSortedRenderers(0, (double)partialTicks);
            Shaders.checkGLError("shadow terrain cutoutmipped");
            GlStateManager.shadeModel(7424);
            GlStateManager.alphaFunc(516, 0.1F);
            GlStateManager.matrixMode(5888);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            mc.mcProfiler.endStartSection("shadow entities");

            if (Reflector.ForgeHooksClient_setRenderPass.exists())
            {
                Reflector.callVoid(Reflector.ForgeHooksClient_setRenderPass, new Object[] {Integer.valueOf(0)});
            }

            RenderHelper.enableStandardItemLighting();
            renderGlobal.renderEntities(viewEntity, frustum, partialTicks);
            RenderHelper.disableStandardItemLighting();
            Shaders.checkGLError("shadow entities");
            GlStateManager.matrixMode(5888);
            GlStateManager.popMatrix();
            GlStateManager.depthMask(true);
            GlStateManager.disableBlend();
            GlStateManager.enableCull();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.alphaFunc(516, 0.1F);

            if (Shaders.usedShadowDepthBuffers >= 2)
            {
                GlStateManager.setActiveTexture(33989);
                Shaders.checkGLError("pre copy shadow depth");
                GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 0, 0, Shaders.shadowMapWidth, Shaders.shadowMapHeight);
                Shaders.checkGLError("copy shadow depth");
                GlStateManager.setActiveTexture(33984);
            }

            GlStateManager.disableBlend();
            GlStateManager.depthMask(true);
            mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            GlStateManager.shadeModel(7425);
            Shaders.checkGLError("shadow pre-translucent");
            GL20.glDrawBuffers(Shaders.sfbDrawBuffers);
            Shaders.checkGLError("shadow drawbuffers pre-translucent");
            Shaders.checkFramebufferStatus("shadow pre-translucent");

            if (Shaders.isRenderShadowTranslucent())
            {
                mc.mcProfiler.endStartSection("shadow translucent");
                renderGlobal.renderAllSortedRenderers(1, (double)partialTicks);
                Shaders.checkGLError("shadow translucent");
            }

            if (Reflector.ForgeHooksClient_setRenderPass.exists())
            {
                RenderHelper.enableStandardItemLighting();
                Reflector.call(Reflector.ForgeHooksClient_setRenderPass, new Object[] {Integer.valueOf(1)});
                renderGlobal.renderEntities(viewEntity, frustum, partialTicks);
                Reflector.call(Reflector.ForgeHooksClient_setRenderPass, new Object[] {Integer.valueOf(-1)});
                RenderHelper.disableStandardItemLighting();
                Shaders.checkGLError("shadow entities 1");
            }

            GlStateManager.shadeModel(7424);
            GlStateManager.depthMask(true);
            GlStateManager.enableCull();
            GlStateManager.disableBlend();
            GL11.glFlush();
            Shaders.checkGLError("shadow flush");
            Shaders.isShadowPass = false;
            mc.gameSettings.thirdPersonView = Shaders.preShadowPassThirdPersonView;
            mc.mcProfiler.endStartSection("shadow postprocess");

            if (Shaders.hasGlGenMipmap)
            {
                if (Shaders.usedShadowDepthBuffers >= 1)
                {
                    if (Shaders.shadowMipmapEnabled[0])
                    {
                        GlStateManager.setActiveTexture(33988);
                        GlStateManager.bindTexture(Shaders.sfbDepthTextures.get(0));
                        GL30.glGenerateMipmap(3553);
                        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, Shaders.shadowFilterNearest[0] ? GL11.GL_NEAREST_MIPMAP_NEAREST : GL11.GL_LINEAR_MIPMAP_LINEAR);
                    }

                    if (Shaders.usedShadowDepthBuffers >= 2 && Shaders.shadowMipmapEnabled[1])
                    {
                        GlStateManager.setActiveTexture(33989);
                        GlStateManager.bindTexture(Shaders.sfbDepthTextures.get(1));
                        GL30.glGenerateMipmap(3553);
                        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, Shaders.shadowFilterNearest[1] ? GL11.GL_NEAREST_MIPMAP_NEAREST : GL11.GL_LINEAR_MIPMAP_LINEAR);
                    }

                    GlStateManager.setActiveTexture(33984);
                }

                if (Shaders.usedShadowColorBuffers >= 1)
                {
                    if (Shaders.shadowColorMipmapEnabled[0])
                    {
                        GlStateManager.setActiveTexture(33997);
                        GlStateManager.bindTexture(Shaders.sfbColorTextures.get(0));
                        GL30.glGenerateMipmap(3553);
                        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, Shaders.shadowColorFilterNearest[0] ? GL11.GL_NEAREST_MIPMAP_NEAREST : GL11.GL_LINEAR_MIPMAP_LINEAR);
                    }

                    if (Shaders.usedShadowColorBuffers >= 2 && Shaders.shadowColorMipmapEnabled[1])
                    {
                        GlStateManager.setActiveTexture(33998);
                        GlStateManager.bindTexture(Shaders.sfbColorTextures.get(1));
                        GL30.glGenerateMipmap(3553);
                        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, Shaders.shadowColorFilterNearest[1] ? GL11.GL_NEAREST_MIPMAP_NEAREST : GL11.GL_LINEAR_MIPMAP_LINEAR);
                    }

                    GlStateManager.setActiveTexture(33984);
                }
            }

            Shaders.checkGLError("shadow postprocess");
            EXTFramebufferObject.glBindFramebufferEXT(36160, Shaders.dfb);
            GL11.glViewport(0, 0, Shaders.renderWidth, Shaders.renderHeight);
            Shaders.activeDrawBuffers = null;
            mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            Shaders.useProgram(7);
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glPopMatrix();
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glPopMatrix();
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            Shaders.checkGLError("shadow end");
        }
    }

    public static void beaconBeamBegin()
    {
        Shaders.useProgram(14);
    }

    public static void beaconBeamStartQuad1() {}

    public static void beaconBeamStartQuad2() {}

    public static void beaconBeamDraw1() {}

    public static void beaconBeamDraw2()
    {
        GlStateManager.disableBlend();
    }

    public static void renderEnchantedGlintBegin()
    {
        Shaders.useProgram(17);
    }

    public static void renderEnchantedGlintEnd()
    {
        if (Shaders.isRenderingWorld)
        {
            if (Shaders.isRenderingFirstPersonHand() && Shaders.isRenderBothHands())
            {
                Shaders.useProgram(19);
            }
            else
            {
                Shaders.useProgram(16);
            }
        }
        else
        {
            Shaders.useProgram(0);
        }
    }

    public static boolean renderEndPortal(TileEntityEndPortal te, double x, double y, double z, float partialTicks, int destroyStage, float offset)
    {
        if (!Shaders.isShadowPass && Shaders.programsID[Shaders.activeProgram] == 0)
        {
            return false;
        }
        else
        {
            GlStateManager.disableLighting();
            Config.getTextureManager().bindTexture(END_PORTAL_TEXTURE);
            Tessellator vertexbuffer = Tessellator.instance;
            vertexbuffer.startDrawing(7);
            float col = 0.5F;
            float r = col * 0.15F;
            float g = col * 0.3F;
            float b = col * 0.4F;
            float u0 = 0.0F;
            float u1 = 0.2F;
            float du = (float)(System.currentTimeMillis() % 100000L) / 100000.0F;
            short lu = 240;
            vertexbuffer.setColorRGBA_F(r, g, b, 1.0F);
            vertexbuffer.setBrightness(lu << 16 | lu);
            vertexbuffer.addVertexWithUV(x, y + (double)offset, z + 1.0D, (double)(u0 + du), (double)(u0 + du));
            vertexbuffer.addVertexWithUV(x + 1.0D, y + (double)offset, z + 1.0D, (double)(u0 + du), (double)(u1 + du));
            vertexbuffer.addVertexWithUV(x + 1.0D, y + (double)offset, z, (double)(u1 + du), (double)(u1 + du));
            vertexbuffer.addVertexWithUV(x, y + (double)offset, z, (double)(u1 + du), (double)(u0 + du));
            vertexbuffer.draw();
            GlStateManager.enableLighting();
            return true;
        }
    }
}
