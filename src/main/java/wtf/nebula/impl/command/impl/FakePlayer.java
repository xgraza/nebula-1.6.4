package wtf.nebula.impl.command.impl;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.world.WorldSettings;
import wtf.nebula.impl.command.Command;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class FakePlayer extends Command {
    public FakePlayer() {
        super(Arrays.asList("fakeplayer", "fp"), "Spawns a fake player");
    }

    private int fakePlayerId = -1;

    @Override
    public void execute(List<String> args) {
        if (fakePlayerId == -1) {
            EntityOtherPlayerMP fake = new EntityOtherPlayerMP(mc.theWorld, new GameProfile(UUID.randomUUID().toString(), "FakePlayer"));
            // fake.inventory.copyInventory(mc.thePlayer.inventory);

            fake.copyLocationAndAnglesFrom(mc.thePlayer);
            fake.posY = mc.thePlayer.boundingBox.minY;

            fake.setHealth(mc.thePlayer.getHealth());
            fake.setAbsorptionAmount(mc.thePlayer.getAbsorptionAmount());
            fake.setGameType(WorldSettings.GameType.SURVIVAL);
            fake.setEntityId(-133769420);

            fakePlayerId = fake.getEntityId();

            mc.theWorld.addEntityToWorld(fakePlayerId, fake);
        }

        else {
            Entity entity = mc.theWorld.getEntityByID(fakePlayerId);

            if (entity == null) {
                fakePlayerId = -1;
                return;
            }

            mc.theWorld.removePlayerEntityDangerously(entity);
            mc.theWorld.removeEntityFromWorld(fakePlayerId);

            fakePlayerId = -1;

            mc.thePlayer.noClip = false;
        }
    }
}
