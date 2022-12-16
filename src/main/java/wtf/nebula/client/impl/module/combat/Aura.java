package wtf.nebula.client.impl.module.combat;

import me.bush.eventbus.annotation.EventListener;
import me.bush.eventbus.annotation.ListenerPriority;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.util.AxisAlignedBB;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.core.Nebula;
import wtf.nebula.client.impl.event.base.Era;
import wtf.nebula.client.impl.event.impl.move.EventMotionUpdate;
import wtf.nebula.client.impl.event.impl.render.EventRender3D;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;
import wtf.nebula.client.impl.module.active.Colors;
import wtf.nebula.client.impl.module.visuals.LogoutSpots;
import wtf.nebula.client.impl.module.world.FreeCamera;
import wtf.nebula.client.utils.client.MathUtils;
import wtf.nebula.client.utils.client.Timer;
import wtf.nebula.client.utils.player.PlayerUtils;
import wtf.nebula.client.utils.player.RotationUtils;
import wtf.nebula.client.utils.render.RenderEngine;
import wtf.nebula.client.utils.render.enums.Dimension;
import wtf.nebula.client.utils.render.renderers.impl.three.FilledBox;
import wtf.nebula.client.utils.world.EntityUtils;

import java.util.Comparator;

public class Aura extends ToggleableModule {
    private final Property<Mode> mode = new Property<>(Mode.SINGLE, "Mode", "m", "type");
    private final Property<Priority> priority = new Property<>(Priority.DISTANCE, "Priority", "p", "sort", "sorting");

    private final Property<Boolean> rotate = new Property<>(true, "Rotate", "rot", "face");

    public static final Property<Double> range = new Property<>(4.0, 1.0, 6.0, "Range", "reach", "dist", "distance");
    private final Property<Double> wallRange = new Property<>(3.0, 1.0, 6.0, "Wall Range", "wallsrange", "wallreach");

    private final Property<Integer> min = new Property<>(8, 1, 20, "Min APS", "minaps");
    private final Property<Integer> max = new Property<>(12, 1, 20, "Max APS", "maxaps");
    private final Property<Integer> deviation = new Property<>(6, 1, 16, "Deviation", "random", "rand");

    private final Property<Boolean> autoBlock = new Property<>(true, "Auto Block", "autoblock");
    private final Property<Boolean> keepSprint = new Property<>(true, "Keep Sprint", "keepsprint");

    private final Property<Boolean> players = new Property<>(true, "Players", "plrs");
    private final Property<Boolean> mobs = new Property<>(true, "Mobs", "hostile");
    private final Property<Boolean> passive = new Property<>(true, "Passive", "nice", "animals", "ambient");

    private final Property<Require> require = new Property<>(Require.WEAPON, "Require", "req");
    private final Property<Boolean> render = new Property<>(true, "Render", "draw");

    private final Timer timer = new Timer();
    public static EntityLivingBase target = null;
    public static boolean attacking = false;
    private boolean blocking = false;

    public Aura() {
        super("Aura", new String[]{"aura", "killaura", "forcefield"}, ModuleCategory.COMBAT);
        offerProperties(mode, priority, rotate, range, wallRange, min, max, deviation, autoBlock, keepSprint, players, mobs, passive, require, render);
    }

    @Override
    protected void onDisable() {
        super.onDisable();

        if (blocking) {
            mc.playerController.unblockSilent();
        }

        blocking = false;
        target = null;
        attacking = false;
    }

    @Override
    public String getTag() {
        return mode.getFixedValue();
    }

    @EventListener
    public void onTick(EventMotionUpdate event) {
        if (!isValidTarget(target) || mode.getValue().equals(Mode.SWITCH)) {
            target = (EntityLivingBase) mc.theWorld.loadedEntityList
                    .stream()
                    .filter((t) -> t instanceof EntityLivingBase && isValidTarget((EntityLivingBase) t))
                    .min(Comparator.comparingDouble((e) -> {
                        double v = 0.0;

                        switch (priority.getValue()) {
                            case HEALTH: {
                                v = ((EntityLivingBase) e).getHealth();
                                break;
                            }

                            case DISTANCE: {
                                v = mc.thePlayer.getDistanceSqToEntity(e);
                                break;
                            }

                            case ANGLE: {
                                v = mc.thePlayer.rotationYaw - RotationUtils.calcAngles(e)[0];
                                break;
                            }
                        }

                        return v;
                    }))
                    .orElse(null);
        }

        if (target == null) {
            attacking = false;
            return;
        }

        if (!require.getValue().equals(Require.NONE)) {

            if (require.getValue().equals(Require.WEAPON)) {
                if (!PlayerUtils.isHolding(ItemSword.class)) {
                    attacking = false;
                    return;
                }
            } else if (require.getValue().equals(Require.SWITCH)) {
                int slot = -1;
                float dmg = 0.0f;

                for (int i = 0; i < 9; ++i) {
                    ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
                    if (stack == null || !(stack.getItem() instanceof ItemSword)) {
                        continue;
                    }

                    ItemSword sword = (ItemSword) stack.getItem();
                    if (sword.func_150931_i() > dmg) {
                        dmg = sword.func_150931_i();
                        slot = i;
                    }
                }

                if (slot == -1) {
                    attacking = false;
                    return;
                }

                mc.thePlayer.inventory.currentItem = slot;
            }

        }

        if (rotate.getValue()) {
            float[] angles = RotationUtils.calcAngles(target);
            Nebula.getInstance().getRotationManager().setRotations(angles);
        }

        if (event.getEra().equals(Era.PRE)) {

            int cps = MathUtils.random(min.getValue(), max.getValue())
                    - MathUtils.random(0, deviation.getValue())
                    + MathUtils.random(0, deviation.getValue());

            if (cps <= 0) {
                cps = MathUtils.random(min.getValue(), max.getValue());
            }

            if (autoBlock.getValue() && blocking && PlayerUtils.isHolding(ItemSword.class)) {
                blocking = false;
                mc.playerController.unblockSilent();
            }

            attacking = timer.hasPassed(1000L / cps, true);
            if (attacking) {
                mc.thePlayer.swingItem();

                if (keepSprint.getValue()) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
                } else {
                    mc.playerController.attackEntity(mc.thePlayer, target);
                }
            }
        } else {
            if (autoBlock.getValue() && !blocking && PlayerUtils.isHolding(ItemSword.class)) {
                blocking = true;
                mc.playerController.blockSword();
            }
        }
    }

    @EventListener(priority = ListenerPriority.HIGHEST)
    public void onRenderWorld(EventRender3D event) {
        if (target != null && render.getValue()) {
            double x = MathUtils.interpolate(target.posX, target.lastTickPosX, event.getPartialTicks()) - RenderManager.renderPosX;
            double y = MathUtils.interpolate(target.posY, target.lastTickPosY, event.getPartialTicks()) - RenderManager.renderPosY;
            double z = MathUtils.interpolate(target.posZ, target.lastTickPosZ, event.getPartialTicks()) - RenderManager.renderPosZ;

            float halfWidth = target.width / 2.0f;
            AxisAlignedBB renderBox = AxisAlignedBB.getBoundingBox(
                    x - halfWidth, y, z - halfWidth,
                    x + halfWidth, y + target.height, z + halfWidth
            );

            RenderEngine.of(Dimension.THREE).add(new FilledBox(renderBox, Colors.getClientColor(120))).render();
        }
    }

    private boolean isValidTarget(EntityLivingBase e) {
        if (e == null || !e.isEntityAlive() || e.equals(mc.thePlayer)) {
            return false;
        }

        if (LogoutSpots.isFake(e.getEntityId())) {
            return false;
        }

        if (e instanceof EntityPigZombie) {
            // TODO; see if a packet tells us that the pigmen is mad at us? it does not update client-side
            return false;
        }

        if (e instanceof EntityPlayer && Nebula.getInstance().getFriendManager().isFriend((EntityPlayer) e)) {
            return false;
        }

        // don't attack our camera guy
        if (Nebula.getInstance().getModuleManager().getModule(FreeCamera.class).isRunning() && e.getEntityId() == -133769420) {
            return false;
        }

        if (e instanceof EntityPlayer && !players.getValue()) {
            return false;
        }

        if (EntityUtils.isMob(e) && !mobs.getValue()) {
            return false;
        }

        if (EntityUtils.isPassive(e) && !passive.getValue()) {
            return false;
        }

        double dist = mc.thePlayer.getDistanceSqToEntity(e);
        double r = mc.thePlayer.canEntityBeSeen(e) ? range.getValue() : wallRange.getValue();

        return !(dist > r * r);
    }

    public enum Mode {
        SINGLE, SWITCH
    }

    public enum Priority {
        DISTANCE, HEALTH, ANGLE
    }

    public enum Require {
        NONE, SWITCH, WEAPON
    }
}
