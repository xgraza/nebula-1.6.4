package wtf.nebula.impl.command.impl;

import net.minecraft.src.Entity;
import net.minecraft.src.EntityOtherPlayerMP;
import net.minecraft.src.EnumGameType;
import wtf.nebula.impl.command.Command;

import java.util.Arrays;
import java.util.List;

public class FakePlayer extends Command {
    public FakePlayer() {
        super(Arrays.asList("fakeplayer", "fp"), "Spawns a fake player");
    }

    private int fakePlayerId = -1;

    @Override
    public void execute(List<String> args) {
        if (fakePlayerId == -1) {
            EntityOtherPlayerMP fake = new EntityOtherPlayerMP(mc.theWorld, "Fakeplayer");
            // fake.inventory.copyInventory(mc.thePlayer.inventory);

            fake.copyLocationAndAnglesFrom(mc.thePlayer);
            fake.posY = mc.thePlayer.boundingBox.minY;

            fake.setHealth(mc.thePlayer.getHealth());
            fake.setAbsorptionAmount(mc.thePlayer.getAbsorptionAmount());
            fake.setGameType(EnumGameType.SURVIVAL);
            fake.entityId = -133769420;

            fakePlayerId = fake.entityId;

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
