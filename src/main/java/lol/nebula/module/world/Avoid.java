package lol.nebula.module.world;

import com.google.common.collect.Lists;
import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.world.EventCollisionBox;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.setting.Setting;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCactus;
import net.minecraft.block.BlockEndPortal;
import net.minecraft.block.BlockFire;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;

import java.util.List;

/**
 * @author aesthetical
 * @since 04/30/23
 */
public class Avoid extends Module {

    private static final List<Block> WHITELIST = Lists.newArrayList(Blocks.cactus, Blocks.fire, Blocks.end_portal);
    private static final AxisAlignedBB FULL_AABB = new AxisAlignedBB(0, 0, 0, 1, 1, 1);

    private final Setting<Boolean> cactus = new Setting<>(true, "Cacti");
    private final Setting<Boolean> endPortals = new Setting<>(true, "End Portals");
    private final Setting<Boolean> fire = new Setting<>(false, "Fire");

    public Avoid() {
        super("Avoid", "Prevents you from walking into blocks", ModuleCategory.WORLD);
    }

    @Listener
    public void onCollisionBox(EventCollisionBox event) {
        // if we are null or the player colliding with this block is null, return
        if (mc.thePlayer == null || !mc.thePlayer.equals(event.getEntity())) return;

        // if the block is not whitelisted, return
        if (!WHITELIST.contains(event.getBlock())) return;

        // if the block is a cactus, end portal, or fire and we don't care about those blocks, return
        if ((event.getBlock() instanceof BlockCactus && !cactus.getValue())
                || (event.getBlock() instanceof BlockEndPortal && !endPortals.getValue())
                || (event.getBlock() instanceof BlockFire && !fire.getValue())) return;

        // set the aabb to a full block
        event.setAabb(FULL_AABB.copy().offset(event.getX(), event.getY(), event.getZ()));
    }
}
