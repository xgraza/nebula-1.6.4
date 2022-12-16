package wtf.nebula.client.impl.command.impl;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import wtf.nebula.client.impl.command.Command;
import wtf.nebula.client.impl.command.arg.CommandContext;

public class FakePlayer extends Command {
    public static int fakePlayerId = -1;

    private EntityOtherPlayerMP fakePlayer;

    public FakePlayer() {
        super(new String[]{"fakeplayer", "fake", "dummy", "dummyplayer"});
    }

    @Override
    public String dispatch(CommandContext ctx) {
        if (fakePlayer != null) {
            mc.theWorld.removeEntity(fakePlayer);
            mc.theWorld.removePlayerEntityDangerously(fakePlayer);
            fakePlayer = null;
            fakePlayerId = -1;
        } else {
            fakePlayer = new EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.getGameProfile());
            fakePlayer.setLocationAndAngles(mc.thePlayer.posX, mc.thePlayer.boundingBox.minY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
            fakePlayer.inventory.copyInventory(mc.thePlayer.inventory);

            mc.theWorld.spawnEntityInWorld(fakePlayer);
            fakePlayerId = fakePlayer.getEntityId();
        }

        return fakePlayer == null ? "Removed fake player" : "Created fake player";
    }
}
