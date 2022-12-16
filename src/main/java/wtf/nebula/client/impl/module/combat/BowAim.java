package wtf.nebula.client.impl.module.combat;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBow;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.core.Nebula;
import wtf.nebula.client.impl.event.base.Era;
import wtf.nebula.client.impl.event.impl.move.EventMotionUpdate;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;
import wtf.nebula.client.impl.module.visuals.Trajectories;
import wtf.nebula.client.impl.module.world.FreeCamera;
import wtf.nebula.client.utils.player.PlayerUtils;
import wtf.nebula.client.utils.world.EntityUtils;

import java.util.Comparator;

public class BowAim extends ToggleableModule {
    private final Property<Boolean> server = new Property<>(false, "Server", "silent");

    private final Property<Boolean> players = new Property<>(true, "Players", "plrs");
    private final Property<Boolean> mobs = new Property<>(true, "Mobs", "hostile");
    private final Property<Boolean> passive = new Property<>(true, "Passive", "nice", "animals", "ambient");

    public BowAim() {
        super("Bow Aim", new String[]{"bowaim", "autobowaim", "bowaimbot"}, ModuleCategory.COMBAT);
        offerProperties(server, players, mobs, passive);
    }

    @EventListener
    public void onMotionUpdate(EventMotionUpdate event) {
        if (PlayerUtils.isHolding(ItemBow.class) && mc.thePlayer.isUsingItem()) {
            EntityLivingBase target = (EntityLivingBase) mc.theWorld.loadedEntityList
                    .stream()
                    .filter((t) -> t instanceof EntityLivingBase && isValidTarget((EntityLivingBase) t))
                    .min(Comparator.comparingDouble((e) -> mc.thePlayer.getDistanceSqToEntity(e)))
                    .orElse(null);

            if (target != null && event.getEra().equals(Era.PRE)) {
                float[] rotations = rotationsTo(target, mc.thePlayer);
                if (rotations == null) {
                    return;
                }

                if (server.getValue()) {
                    Nebula.getInstance().getRotationManager().setRotations(rotations);
                } else {
                    mc.thePlayer.rotationYaw = rotations[0];
                    mc.thePlayer.rotationYawHead = rotations[0];
                    mc.thePlayer.rotationPitch = rotations[1];
                }
            }
        }
    }

    private boolean isValidTarget(EntityLivingBase e) {
        if (e == null || !e.isEntityAlive() || e.equals(mc.thePlayer)) {
            return false;
        }

        if (e instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) e;
            if (Nebula.getInstance().getFriendManager().isFriend(player)) {
                return false;
            }

            if (!players.getValue()) {
                return false;
            }
        }

        if (e instanceof EntityPigZombie) {
            return false;
        }

        if (EntityUtils.isMob(e) && !mobs.getValue()) {
            return false;
        }

        if (EntityUtils.isPassive(e) && !passive.getValue()) {
            return false;
        }

        // don't attack our camera guy
        return !Nebula.getInstance().getModuleManager().getModule(FreeCamera.class).isRunning() || e.getEntityId() != -133769420;
    }

    public static float[] rotationsTo(EntityLivingBase entity, EntityPlayer main) {
        double duration = (double) Trajectories.getArrowVelocity(main.getItemInUseDuration()) * 3.0;

        float pitch = (float) -Math.toDegrees(calculateArc(entity, main, duration));
        if (Float.isNaN(pitch)) {
            return null;
        }

        double diffX = entity.posX - entity.lastTickPosX;
        double diffZ = entity.posZ - entity.lastTickPosZ;

        double distance = main.getDistanceToEntity(entity);

        distance -= distance % 2.0;

        diffX = distance / 2.0 * diffX * (main.isSprinting() ? 1.3 : 1.1);
        diffZ = distance / 2.0 * diffZ * (main.isSprinting() ? 1.3 : 1.1);

        float yaw = (float) Math.toDegrees(Math.atan2(entity.posZ + diffZ - main.posZ, entity.posX + diffX - main.posX)) - 90.0f;

        return new float[]{ yaw, pitch };
    }

    private static float calculateArc(EntityLivingBase target, EntityLivingBase main, double duration) {
        double yArc = target.boundingBox.minY + (1.620 / 2.0) - main.posY;
        double dX = target.posX - main.posX;
        double dZ = target.posZ - main.posZ;

        double dirRoot = Math.sqrt(dX * dX + dZ * dZ);
        return calculateArc(duration, dirRoot, yArc);
    }

    private static float calculateArc(double duration, double dirRoot, double yArc) {
        double coefficient = 0.05000000074505806;
        double dirCoeff = coefficient * (dirRoot * dirRoot);

        yArc = 2.0 * yArc * (duration * duration);
        yArc = coefficient * (dirCoeff + yArc);
        yArc = Math.sqrt(duration * duration * duration * duration - yArc);

        duration = duration * duration - yArc;
        yArc = Math.atan2(duration * duration + yArc, coefficient * dirRoot);
        duration = Math.atan2(duration, coefficient * dirRoot);

        return (float) Math.min(yArc, duration);
    }
}
