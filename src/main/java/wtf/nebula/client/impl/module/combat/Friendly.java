package wtf.nebula.client.impl.module.combat;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C02PacketUseEntity;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.core.Nebula;
import wtf.nebula.client.impl.event.impl.network.EventPacket;
import wtf.nebula.client.impl.event.impl.player.EventAttack;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;

public class Friendly extends ToggleableModule {
    private final Property<Boolean> pigmen = new Property<>(true, "Zombie Pigmen", "zombiepigmen", "pigmen", "cunts");
    private final Property<Boolean> friends = new Property<>(true, "Friends", "frens");
    private final Property<Boolean> infinites = new Property<>(false, "Infinites", "infs");

    public Friendly() {
        super("Friendly", new String[]{"noattack", "nointeract"}, ModuleCategory.COMBAT);
        offerProperties(pigmen, friends, infinites);

        setDrawn(false);
        setRunning(true); // automatically on
    }

    @EventListener
    public void onPacket(EventPacket event) {
        if (event.getPacket() instanceof C02PacketUseEntity) {

            ItemStack held = Nebula.getInstance().getInventoryManager().getHeld();
            if (held != null && held.stackSize < 0 && infinites.getValue()) {
                event.setCancelled(true);
            }
        }
    }

    @EventListener
    public void onAttack(EventAttack event) {
        if (event.getEntity() instanceof EntityPigZombie && pigmen.getValue()) {
            event.setCancelled(true);
        }

        if (event.getEntity() instanceof EntityPlayer
                && Nebula.getInstance().getFriendManager().isFriend((EntityPlayer) event.getEntity())
                && friends.getValue()) {

            event.setCancelled(true);
        }
    }
}
