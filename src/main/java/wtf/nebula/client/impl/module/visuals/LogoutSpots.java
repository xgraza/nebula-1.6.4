package wtf.nebula.client.impl.module.visuals;

import com.mojang.authlib.GameProfile;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.core.Nebula;
import wtf.nebula.client.impl.event.impl.network.EventPlayerConnection;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LogoutSpots extends ToggleableModule {
    private final Property<Boolean> friends = new Property<>(true, "Friends", "logfriends");
    private final Property<Boolean> chat = new Property<>(true, "Notify", "chat");

    public static final Map<String, EntityOtherPlayerMP> fakePlayerMap = new ConcurrentHashMap<>();

    public LogoutSpots() {
        super("Logout Spots", new String[]{"logoutspots", "logspots"}, ModuleCategory.VISUALS);
        offerProperties(friends, chat);
    }

    @Override
    protected void onDisable() {
        super.onDisable();

        if (!isNull()) {
            fakePlayerMap.forEach((name, fake) -> {
                mc.theWorld.removeEntity(fake);
                mc.theWorld.removePlayerEntityDangerously(fake);

                fakePlayerMap.remove(name);
            });
        }

        fakePlayerMap.clear();
    }

    @EventListener
    public void onPlayerConnection(EventPlayerConnection event) {
        if (event.getAction().equals(EventPlayerConnection.Action.JOIN)) {
            EntityOtherPlayerMP fake = fakePlayerMap.getOrDefault(event.getUsername(), null);
            if (fake != null) {

                if (chat.getValue()) {
                    print(event.getUsername() + " logged back in!");
                }

                mc.theWorld.removeEntity(fake);
                mc.theWorld.removePlayerEntityDangerously(fake);

                fakePlayerMap.remove(event.getUsername());
            }
        } else {

            if (Nebula.getInstance().getFriendManager().isFriend(event.getUsername()) && !friends.getValue()) {
                return;
            }


            EntityPlayer entity = mc.theWorld.getPlayerEntityByName(event.getUsername());
            if (entity != null) {
                if (entity.equals(mc.thePlayer) || entity.getCommandSenderName().equals(mc.session.getUsername())) {
                    return;
                }

                EntityOtherPlayerMP fake = new EntityOtherPlayerMP(mc.theWorld, new GameProfile("", event.getUsername()));
                fake.setLocationAndAngles(entity.posX, entity.boundingBox.minY, entity.posZ, entity.rotationYaw, entity.rotationPitch);
                fake.setHealth(entity.getHealth());
                fake.setAbsorptionAmount(entity.getAbsorptionAmount());

                fake.hurtTime = entity.hurtTime;
                fake.maxHurtResistantTime = entity.maxHurtResistantTime;
                fake.hurtResistantTime = entity.hurtResistantTime;
                fake.maxHurtTime = entity.maxHurtTime;
                fake.swingProgress = entity.swingProgress;
                fake.swingProgressInt = entity.swingProgressInt;
                fake.limbSwingAmount = entity.limbSwingAmount;
                fake.limbSwing = entity.limbSwing;
                fake.isSwingInProgress = entity.isSwingInProgress;
                fake.prevLimbSwingAmount = entity.prevLimbSwingAmount;
                fake.prevSwingProgress = entity.prevSwingProgress;

                fakePlayerMap.put(event.getUsername(), fake);
                mc.theWorld.spawnEntityInWorld(fake);

                if (chat.getValue()) {
                    print(event.getUsername() + " has logged out at XYZ: " + entity.posX + ", " + entity.posY + ", " + entity.posZ + ".");
                }
            }
        }
    }

    public static boolean isFake(int id) {
        return fakePlayerMap.values().stream().anyMatch((e) -> e.getEntityId() == id);
    }
}
