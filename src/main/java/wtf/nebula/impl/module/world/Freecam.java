package wtf.nebula.impl.module.world;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityOtherPlayerMP;
import net.minecraft.src.EnumGameType;
import net.minecraft.src.Packet10Flying;
import wtf.nebula.event.MotionEvent;
import wtf.nebula.event.PacketEvent;
import wtf.nebula.event.TickEvent;
import wtf.nebula.event.WorldUnloadEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;
import wtf.nebula.impl.value.Value;
import wtf.nebula.util.world.player.MotionUtil;

public class Freecam extends Module {
    public Freecam() {
        super("Freecam", ModuleCategory.WORLD);
    }

    public final Value<Double> speed = new Value<>("Speed", 0.5, 0.1, 5.0);
    public final Value<Boolean> antiKick = new Value<>("AntiKick", true);

    private int fakePlayerId = -1;

    @Override
    protected void onActivated() {
        super.onActivated();

        if (nullCheck()) {
            setState(false);
            return;
        }

        if (antiKick.getValue() && !mc.thePlayer.onGround) {
            sendChatMessage("You must be on ground to prevent NCP thinking you're flying!");
            setState(false);
            return;
        }

        EntityOtherPlayerMP fake = new EntityOtherPlayerMP(mc.theWorld, "You");
        fake.inventory.copyInventory(mc.thePlayer.inventory);

        fake.copyLocationAndAnglesFrom(mc.thePlayer);
        fake.posY = mc.thePlayer.boundingBox.minY;

        fake.setHealth(mc.thePlayer.getHealth());
        fake.setAbsorptionAmount(mc.thePlayer.getAbsorptionAmount());
        fake.setGameType(EnumGameType.SURVIVAL);
        fake.entityId = -133769420;

        fakePlayerId = fake.entityId;

        mc.theWorld.addEntityToWorld(fakePlayerId, fake);
    }

    @Override
    protected void onDeactivated() {
        super.onDeactivated();

        if (!nullCheck() && fakePlayerId != -1) {
            Entity entity = mc.theWorld.getEntityByID(fakePlayerId);

            mc.thePlayer.posX = entity.posX;
            mc.thePlayer.posY = entity.posY;
            mc.thePlayer.posZ = entity.posZ;

            mc.thePlayer.copyLocationAndAnglesFrom(entity);

            mc.theWorld.removePlayerEntityDangerously(entity);
            mc.theWorld.removeEntityFromWorld(fakePlayerId);

            fakePlayerId = -1;

            mc.thePlayer.noClip = false;
        }
    }

    @EventListener
    public void onWorldUnload(WorldUnloadEvent event) {

        // disable to prevent a kick
        setState(false);
    }

    @EventListener
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof Packet10Flying) {
            event.setCancelled(true);
        }
    }

    @EventListener
    public void onTick(TickEvent event) {

        // prevent the server from kicking you for not sending movement packets for too long
        if (mc.thePlayer.ticksExisted % 30 == 0 && antiKick.getValue()) {
            mc.thePlayer.sendQueue.addToSendQueueSilent(new Packet10Flying(true));
        }
    }

    @EventListener
    public void onMotion(MotionEvent event) {
        mc.thePlayer.noClip = true;

        double[] strafe = MotionUtil.strafe(speed.getValue());

        mc.thePlayer.motionX = strafe[0];
        mc.thePlayer.motionZ = strafe[1];

        if (!MotionUtil.isMoving()) {
            mc.thePlayer.motionX = 0.0;
            mc.thePlayer.motionZ = 0.0;
        }

        if (mc.gameSettings.keyBindJump.pressed) {
            mc.thePlayer.motionY = speed.getValue();
        }

        else if (mc.gameSettings.keyBindSneak.pressed) {
            mc.thePlayer.motionY = -speed.getValue();
        }

        else {
            mc.thePlayer.motionY = 0.0;
        }

        event.setX(mc.thePlayer.motionX);
        event.setY(mc.thePlayer.motionY);
        event.setZ(mc.thePlayer.motionZ);
    }
}
