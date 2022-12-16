package wtf.nebula.client.impl.module.world;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.impl.event.impl.world.EventAddBox;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;

public class Avoid extends ToggleableModule {
    private static final AxisAlignedBB FULL_BB = new AxisAlignedBB(0, 0, 0, 1, 1, 1);

    private final Property<Boolean> cacti = new Property<>(true, "Cacti", "cactus");
    private final Property<Boolean> endPortals = new Property<>(false, "End Portals", "endportals", "entranceportal");
    private final Property<Boolean> fire = new Property<>(true, "Fire", "fireblock");

    public Avoid() {
        super("Avoid", new String[]{"avoider"}, ModuleCategory.WORLD);
        offerProperties(cacti, endPortals, fire);
    }

    @EventListener
    public void onAddBoundingBox(EventAddBox event) {
        if (isAboveBlock(event.getY())) {
            return;
        }

        if (event.getBlock().equals(Blocks.cactus) && cacti.getValue()) {
            event.setBox(FULL_BB.copy().offset(event.getX(), event.getY(), event.getZ()));
            event.setCancelled(true);
        }

        if (event.getBlock().equals(Blocks.end_portal) && endPortals.getValue()) {
            event.setBox(FULL_BB.copy().offset(event.getX(), event.getY(), event.getZ()));
            event.setCancelled(true);
        }

        if (event.getBlock().equals(Blocks.fire) && fire.getValue()) {
            event.setBox(FULL_BB.copy().offset(event.getX(), event.getY(), event.getZ()));
            event.setCancelled(true);
        }
    }

    private boolean isAboveBlock(int y) {
        if (isNull()) {
            return false;
        }

        double yPos = (double) y + 1.0;
        return mc.thePlayer.boundingBox.minY >= yPos;
    }
}
