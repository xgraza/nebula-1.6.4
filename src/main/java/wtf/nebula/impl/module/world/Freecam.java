package wtf.nebula.impl.module.world;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.src.*;
import org.lwjgl.input.Keyboard;
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

    private int fakePlayerId = -1;
    private double minY = 0.0;

    @Override
    protected void onActivated() {
        super.onActivated();

        if (nullCheck()) {
            setState(false);
            return;
        }

        EntityOtherPlayerMP fake = new EntityOtherPlayerMP(mc.theWorld, "You");
        fake.inventory.copyInventory(mc.thePlayer.inventory);
        fake.copyLocationAndAnglesFrom(mc.thePlayer);
        fake.setHealth(mc.thePlayer.getHealth());
        fake.setAbsorptionAmount(mc.thePlayer.getAbsorptionAmount());
        fake.setGameType(EnumGameType.SURVIVAL);
        fake.entityId = -133769420;

        fakePlayerId = fake.entityId;
        minY = mc.thePlayer.boundingBox.minY;

        mc.theWorld.addEntityToWorld(fakePlayerId, fake);
    }

    @Override
    protected void onDeactivated() {
        super.onDeactivated();

        if (!nullCheck() && fakePlayerId != -1) {
            Entity entity = mc.theWorld.getEntityByID(fakePlayerId);

            mc.thePlayer.posX = entity.posX;
            mc.thePlayer.boundingBox.minY = minY;
            mc.thePlayer.posY = entity.posY;
            mc.thePlayer.posZ = entity.posZ;

            mc.theWorld.removePlayerEntityDangerously(entity);
            mc.theWorld.removeEntityFromWorld(fakePlayerId);

            fakePlayerId = -1;
            minY = 0.0;

            mc.thePlayer.noClip = false;
        }
    }

    @EventListener
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof Packet10Flying) {
            event.setCancelled(true);
        }
    }

    @EventListener
    public void onMotion(MotionEvent event) {
        mc.thePlayer.noClip = true;
    }

    @EventListener
    public void onTick(TickEvent event) {
        mc.thePlayer.noClip = true;

        double[] strafe = MotionUtil.strafe(speed.getValue());

        mc.thePlayer.motionX = strafe[0];
        mc.thePlayer.motionZ = strafe[1];

        if (!MotionUtil.isMoving()) {
            mc.thePlayer.motionX = 0.0;
            mc.thePlayer.motionZ = 0.0;
        }

        if (mc.gameSettings.keyBindJump.pressed) {
            mc.thePlayer.motionY = speed.getValue() / 10.0;
        }

        else if (mc.gameSettings.keyBindSneak.pressed) {
            mc.thePlayer.motionY = -(speed.getValue() / 10.0);
        }

        else {
            mc.thePlayer.motionY = 0.0;
        }
    }
}
