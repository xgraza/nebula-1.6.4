package ez.nebula.client.impl.module.render;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import org.lwjgl.opengl.GL11;
import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleInstance;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.setting.Setting;
import ez.nebula.client.util.minecraft.player.EntityUtil;

/**
 * @author xgraza
 * @since 03/17/25
 */
@ModuleManifest(name = "Chams",
        description = "Allows you to render entity models through walls",
        category = ModuleCategory.RENDER)
public final class ChamsModule extends Module
{
    @ModuleInstance
    public static ChamsModule INSTANCE;

    public final Setting<Mode> modeSetting = enumBuilder("Mode", Mode.TEXTURE)
            .setDescription("How to render entities through walls")
            .build();

    private final Setting<Boolean> playersSetting = builder("Players", true)
            .setDescription("If to render players through walls")
            .build();
    private final Setting<Boolean> hostileSetting = builder("Hostile Mobs", true)
            .setDescription("If to render hostile mobs through walls")
            .build();
    private final Setting<Boolean> passiveSetting = builder("Passive Mobs", true)
            .setDescription("If to render passive mobs through walls")
            .build();

    private final Setting<Boolean> chestsSetting = builder("Chests", true)
            .setDescription("If to render chests through walls")
            .build();
    private final Setting<Boolean> enderChestsSetting = builder("Ender Chests", true)
            .setDescription("If to render ender chests through walls")
            .build();
    private final Setting<Boolean> otherTileSetting = builder("Other Tile Entities", false)
            .setDescription("If to render all other tile entities through walls")
            .build();

    public void preEntityRender()
    {
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

    public boolean postEntityRender()
    {
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

    public boolean isTileEntityValid(final TileEntity entity)
    {
        if (entity instanceof TileEntityChest)
        {
            return chestsSetting.getValue();
        }
        if (entity instanceof TileEntityEnderChest)
        {
            return enderChestsSetting.getValue();
        }
        return otherTileSetting.getValue();
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
        return passiveSetting.getValue() || !EntityUtil.isEntityPassive(entity);
    }

    public enum Mode
    {
        TEXTURE, COLOR, XQZ, WIREFRAME
    }
}
