package us.nebula.client.cheat.impl.player;

import net.minecraft.init.Items;
import net.minecraft.src.BlockPos;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import org.lwjgl.input.Keyboard;
import us.nebula.client.Nebula;
import us.nebula.client.interaction.InteractionManager;
import us.nebula.client.listener.EventListener;
import us.nebula.client.listener.Subscribe;
import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.listener.event.game.EventUpdate;
import us.nebula.client.listener.event.input.EventKey;
import us.nebula.client.listener.event.player.EventAttackBlock;
import us.nebula.client.listener.event.render.EventRender3D;
import us.nebula.client.util.player.ChatUtil;
import us.nebula.client.util.player.InventoryUtil;
import us.nebula.client.util.render.RenderUtil;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

@CheatManifest(name = "Test", category = CheatCategory.PLAYER)
public final class TestCheat extends Cheat
{
    @Subscribe
    private final EventListener<EventAttackBlock> eventAttackBlockEventListener = event ->
    {
        final int slot = InventoryUtil.getSlot(0, 9, (stack) -> stack.getItem() == Items.water_bucket);
        if (slot == -1)
        {
            return;
        }

        event.cancel();

        final BlockPos pos = new BlockPos(event.getX(), event.getY(), event.getZ());
        Nebula.INSTANCE.getInventoryManager().setSlot(slot);
        InteractionManager.INSTANCE.rightClickBlock(pos, EnumFacing.faceList[event.getSide()], false);
        InteractionManager.INSTANCE.rightClickBlock(pos, EnumFacing.faceList[event.getSide()], false);
        Nebula.INSTANCE.getInventoryManager().syncSlot();
    };
}
