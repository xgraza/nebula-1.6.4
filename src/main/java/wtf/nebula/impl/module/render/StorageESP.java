package wtf.nebula.impl.module.render;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.tileentity.*;
import net.minecraft.util.AxisAlignedBB;
import wtf.nebula.event.RenderWorldEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;
import wtf.nebula.util.render.ColorUtil;
import wtf.nebula.util.render.RenderUtil;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class StorageESP extends Module {
    public StorageESP() {
        super("StorageESP", ModuleCategory.RENDER);
    }

    @EventListener
    public void onRenderWorld(RenderWorldEvent event) {
        for (Object obj : mc.theWorld.tileEntities) {
            if (!(obj instanceof TileEntity)) {
                continue;
            }

            TileEntity tileEntity = (TileEntity) obj;

            Color c = null;

            if (tileEntity instanceof TileEntityChest) {
                c = new Color(82, 60, 48);
            }

            else if (tileEntity instanceof TileEntityEnderChest) {
                c = new Color(104, 67, 134);
            }

            else if (tileEntity instanceof TileEntityFurnace) {
                c = new Color(77, 70, 69);
            }

            else if (tileEntity instanceof TileEntityBeacon) {
                c = new Color(91, 96, 178);
            }

            if (c == null) {
                continue;
            }

            int color = c.getRGB();
            if (color == -1) {
                continue;
            }

            glPushMatrix();

            glDisable(GL_ALPHA_TEST);
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

            ColorUtil.setColor(color);

            glDisable(GL_TEXTURE_2D);
            glDisable(GL_DEPTH_TEST);

            glEnable(GL_LINE_SMOOTH);
            glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);

            glLineWidth(1.5f);

            AxisAlignedBB box = AxisAlignedBB.getBoundingBox(
                    tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord,
                    tileEntity.xCoord + 1, tileEntity.yCoord + 1, tileEntity.zCoord + 1)
                    .offset(-RenderManager.renderPosX, -RenderManager.renderPosY, -RenderManager.renderPosZ);

            //mc.renderGlobal.drawOutlinedBoundingBox(box);

            RenderGlobal.drawOutlinedBoundingBox(box, color);
            ColorUtil.setColor(ColorUtil.addAlpha(color, 120));
            RenderUtil.drawFilledBoundingBox(box);

            glLineWidth(1.0f);
            glDisable(GL_LINE_SMOOTH);

            glEnable(GL_DEPTH_TEST);
            glEnable(GL_TEXTURE_2D);
            glDisable(GL_BLEND);

            glEnable(GL_ALPHA_TEST);

            glPopMatrix();
        }
    }
}
