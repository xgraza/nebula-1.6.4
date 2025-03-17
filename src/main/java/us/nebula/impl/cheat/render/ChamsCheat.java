package us.nebula.impl.cheat.render;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatInstance;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.api.value.Setting;
import us.nebula.util.player.EntityUtil;

/**
 * @author xgraza
 * @since 03/17/25
 */
@CheatManifest(name = "Chams",
        description = "Allows you to render entities through walls",
        category = CheatCategory.RENDER)
public final class ChamsCheat extends Cheat
{
    @CheatInstance
    public static ChamsCheat INSTANCE;

    public final Setting<Mode> modeSetting = new Setting<>(
            "Mode", Mode.TEXTURE);

    private final Setting<Boolean> playersSetting = new Setting<>(
            "Players", true);
    private final Setting<Boolean> hostileSetting = new Setting<>(
            "Hostile Mobs", true);
    private final Setting<Boolean> passiveSetting = new Setting<>(
            "Passive Mobs", true);

    public void preEntityRender(final EntityLivingBase entity)
    {
        if (!isEntityValid(entity))
        {
            return;
        }

        if (modeSetting.getValue() != Mode.XQZ && modeSetting.getValue() != Mode.WIREFRAME)
        {
            GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
            GL11.glPolygonOffset(1.0f, -1000000.0f);
        }

        switch (modeSetting.getValue())
        {
            case COLOR:
            {
                GL11.glColor4f(1.0f, 0.3f, 0.3f, 0.8f);
                break;
            }
            case XQZ:
            {
                GL11.glDepthMask(false);
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                GL11.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
                break;
            }
            case WIREFRAME:
            {
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glDepthMask(false);
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                GL11.glLineWidth(1.5f);
                GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
                break;
            }
            case TEXTURE:
            {
                break;
            }
        }
    }

    public boolean postEntityRender(final EntityLivingBase entity)
    {
        if (!isEntityValid(entity))
        {
            return false;
        }

        switch (modeSetting.getValue())
        {
            case WIREFRAME:
            {
                GL11.glLineWidth(1.0f);
                GL11.glPopAttrib();
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
                GL11.glDepthMask(true);
                break;
            }
            case XQZ:
            {
                GL11.glEnable(GL11.GL_DEPTH_TEST);
                GL11.glDepthMask(true);
                GL11.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
                return true;
            }
            case COLOR:
            case TEXTURE:
            {
                GL11.glPolygonOffset(1.0f, 1000000.0f);
                GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
                break;
            }
        }
        return false;
    }

    public boolean isEntityValid(final Entity entity)
    {
        if (!playersSetting.getValue() && entity instanceof EntityPlayer)
        {
            return false;
        }
        if (!hostileSetting.getValue() && EntityUtil.isEntityHostile(entity))
        {
            return false;
        }
        if (!passiveSetting.getValue() && EntityUtil.isEntityPassive(entity))
        {
            return false;
        }
        return true;
    }

    public enum Mode
    {
        TEXTURE, COLOR, XQZ, WIREFRAME
    }
}
