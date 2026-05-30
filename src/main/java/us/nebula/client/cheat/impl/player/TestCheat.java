package us.nebula.client.cheat.impl.player;

import net.minecraft.src.BlockPos;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import org.lwjgl.input.Keyboard;
import us.nebula.client.listener.EventListener;
import us.nebula.client.listener.Subscribe;
import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.listener.event.input.EventKey;
import us.nebula.client.listener.event.render.EventRender3D;
import us.nebula.client.util.player.ChatUtil;
import us.nebula.client.util.player.PlayerUtil;
import us.nebula.client.util.render.RenderUtil;

@CheatManifest(name = "Test", category = CheatCategory.PLAYER)
public final class TestCheat extends Cheat
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
