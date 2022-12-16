package wtf.nebula.client.impl.module.movement;

import net.minecraft.entity.EntityLivingBase;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;
import wtf.nebula.client.impl.module.combat.Aura;
import wtf.nebula.client.utils.player.MoveUtils;

public class TargetStrafe extends ToggleableModule {
    public static TargetStrafe INSTANCE;
    private static int dir = 1;

    private static final Property<Double> range = new Property<>(2.0, 1.0, 6.0, "Range", "distance", "dist");
    private static final Property<Double> reduction = new Property<>(0.1, 0.0, 1.0, "Reduction");

    public TargetStrafe() {
        super("Target Strafe", new String[]{"targetstrafe", "strafe"}, ModuleCategory.MOVEMENT);
        offerProperties(range, reduction);
        INSTANCE = this;
    }

    public static double[] calcStrafe(double moveSpeed) {

        if (Aura.target != null && Aura.attacking && INSTANCE.isRunning()) {

            double playerSpeed = moveSpeed * (1.0 - (reduction.getValue() / 10.0));

            if (mc.thePlayer.isCollidedHorizontally) {
                dir = -dir;
            }

            EntityLivingBase target = Aura.target;

            double degree = Math.atan2(mc.thePlayer.posZ - target.posZ, mc.thePlayer.posX - target.posX);
            degree += (playerSpeed / mc.thePlayer.getDistanceToEntity(target)) * (double) dir;

            double x = target.posX + range.getValue() * Math.cos(degree);
            double z = target.posZ + range.getValue() * Math.sin(degree);

            float yaw = (float) (Math.atan2(z - mc.thePlayer.posZ, x - mc.thePlayer.posX) * 180.0 / Math.PI - 90.0);

            double rad = Math.toRadians(yaw);
            double motionX = playerSpeed * -Math.sin(rad);
            double motionZ = playerSpeed * Math.cos(rad);

            return new double[] { motionX, motionZ };
        } else {
            return MoveUtils.calcStrafe(moveSpeed);
        }
    }

    public static boolean isMoving() {

        //  this error occurs sometimes?:

        // java.lang.NullPointerException: Ticking entity
        //	at wtf.nebula.client.impl.module.movement.TargetStrafe.isMoving(TargetStrafe.java:54)
        //	at wtf.nebula.client.impl.module.movement.Speed.onMotion(Speed.java:67)
        //	at me.bush.eventbus.handler.handlers.LambdaHandler.invoke(LambdaHandler.java:71)
        //	at me.bush.eventbus.bus.EventBus.post(EventBus.java:141)
        //	at net.minecraft.entity.Entity.moveEntity(Entity.java:474)
        //	at net.minecraft.entity.EntityLivingBase.moveEntityWithHeading(EntityLivingBase.java:1450)
        //	at net.minecraft.entity.player.EntityPlayer.moveEntityWithHeading(EntityPlayer.java:1507)
        //	at net.minecraft.entity.EntityLivingBase.onLivingUpdate(EntityLivingBase.java:1768)
        //	at net.minecraft.entity.player.EntityPlayer.onLivingUpdate(EntityPlayer.java:497)
        //	at net.minecraft.client.entity.EntityPlayerSP.onLivingUpdate(EntityPlayerSP.java:291)
        //	at net.minecraft.entity.EntityLivingBase.onUpdate(EntityLivingBase.java:1565)
        //	at net.minecraft.entity.player.EntityPlayer.onUpdate(EntityPlayer.java:250)
        //	at net.minecraft.client.entity.EntityClientPlayerMP.onUpdate(EntityClientPlayerMP.java:75)
        //	at net.minecraft.world.World.updateEntityWithOptionalForce(World.java:1877)
        //	at net.minecraft.world.World.updateEntity(World.java:1850)
        //	at net.minecraft.world.World.updateEntities(World.java:1722)
        //	at net.minecraft.client.Minecraft.runTick(Minecraft.java:1952)
        //	at net.minecraft.client.Minecraft.runGameLoop(Minecraft.java:908)
        //	at net.minecraft.client.Minecraft.run(Minecraft.java:827)
        //	at net.minecraft.client.main.Main.main(Main.java:109)
        //	at Start.main(Start.java:9)

        if (mc.theWorld == null || mc.thePlayer == null) {
            return false;
        }

        return (INSTANCE.isRunning() && Aura.target != null) || MoveUtils.isMoving();
    }
}
