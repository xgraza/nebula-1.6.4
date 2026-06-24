package ez.nebula.client.impl.module.player;

import ez.nebula.client.api.DebugFeature;
import ez.nebula.client.api.listener.event.network.EventPacket;
import ez.nebula.client.api.listener.event.player.EventMove;
import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.util.minecraft.player.MoveUtil;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
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
    @Subscribe
    private final EventListener<EventPacket.Outbound> outboundEventListener = event ->
    {
        if (event.getPacket() instanceof C08PacketPlayerBlockPlacement)
        {
            ((C08PacketPlayerBlockPlacement) event.getPacket()).setStack(null);
        }
    };
}
