package wtf.nebula.client.impl.module.movement;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.init.Blocks;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.impl.event.impl.client.EventTick;
import wtf.nebula.client.impl.event.impl.move.EventSafeWalk;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;
import wtf.nebula.client.utils.player.PlayerUtils;
import wtf.nebula.client.utils.world.WorldUtils;

public class SafeWalk extends ToggleableModule {
    private final Property<Boolean> eagle = new Property<>(false, "Eagle");

    private boolean eagling = false;

    public SafeWalk() {
        super("Safe Walk", new String[]{"safewalk", "sneak"}, ModuleCategory.MOVEMENT);
        offerProperties(eagle);
    }

    @Override
    protected void onDisable() {
        super.onDisable();

        if (!isNull() && eagling) {
            mc.gameSettings.keyBindSneak.pressed = false;
        }

        eagling = false;
    }

    @EventListener
    public void onTick(EventTick event) {
        if (eagle.getValue()) {
            eagling = WorldUtils.getBlock(PlayerUtils.getPosUnder()) == Blocks.air && mc.thePlayer.onGround;
            mc.gameSettings.keyBindSneak.pressed = eagling;
        } else {
            if (eagling) {
                mc.gameSettings.keyBindSneak.pressed = false;
                eagling = false;
            }
        }
    }

    @EventListener
    public void onSafewalk(EventSafeWalk event) {
        event.setCancelled(true);
    }
}
