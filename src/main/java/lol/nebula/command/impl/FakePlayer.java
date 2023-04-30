package lol.nebula.command.impl;

import lol.nebula.command.Command;
import net.minecraft.client.entity.EntityOtherPlayerMP;

/**
 * @author aesthetical
 * @since 04/29/23
 */
public class FakePlayer extends Command {

    private static final int FAKE_ID = -133769;

    private EntityOtherPlayerMP fake;

    public FakePlayer() {
        super(new String[]{"fakeplayer", "fake"}, "Spawns in a fake player");
    }

    @Override
    public String dispatch(String[] args) {

        if (fake == null) {
            fake = new EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.getGameProfile());
            fake.copyLocationAndAnglesFrom(mc.thePlayer);
            fake.inventory.copyInventory(mc.thePlayer.inventory);
            fake.setEntityId(FAKE_ID);

            mc.theWorld.spawnEntityInWorld(fake);

            return "Spawned in fake successfully";
        } else {
            mc.theWorld.removeEntityFromWorld(FAKE_ID);
            mc.theWorld.removePlayerEntityDangerously(fake);

            fake = null;

            return "Removed fake player successfully";
        }
    }
}
