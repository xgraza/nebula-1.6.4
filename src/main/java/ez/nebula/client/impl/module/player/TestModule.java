package ez.nebula.client.impl.module.player;

import ez.nebula.client.api.DebugFeature;
import ez.nebula.client.api.manager.module.Module;
import net.minecraft.src.BlockPos;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import org.lwjgl.input.Keyboard;
import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.listener.event.input.EventKey;
import ez.nebula.client.api.listener.event.render.EventRender3D;
import ez.nebula.client.util.minecraft.player.ChatUtil;
import ez.nebula.client.util.minecraft.player.PlayerUtil;
import ez.nebula.client.util.render.RenderUtil;

@DebugFeature
@ModuleManifest(name = "Test", category = ModuleCategory.PLAYER)
public final class TestModule extends Module
{
    private int facing;

    @Override public void onDisable()
    {
        super.onDisable();
        facing = 0;
    }

    @Subscribe
    private final EventListener<EventKey> keyEventListener = event ->
    {
        if (event.getKeyCode() == Keyboard.KEY_Y)
        {
            facing++;
            if (facing >= EnumFacing.values().length)
            {
                facing = 0;
            }
            ChatUtil.sendNebula("Facing: %s", EnumFacing.faceList[facing]);
        }
    };

    @Subscribe
    private final EventListener<EventRender3D> render3DEventListener = event ->
    {
        final BlockPos pos = PlayerUtil.getOrigin().offset(PlayerUtil.getFacing());
        final AxisAlignedBB bb = new AxisAlignedBB(pos);
        final int mask = RenderUtil.calculateFaceMask(EnumFacing.faceList[facing]);
        //RenderUtil.filledBox3D(bb, mask, 0x80FF0000);
        RenderUtil.renderOutlinedAABB(bb, 1.5f, mask, 0x80FF0000);
    };
}
