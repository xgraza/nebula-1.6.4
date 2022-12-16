package wtf.nebula.client.impl.module.combat;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.server.S0EPacketSpawnObject;
import net.minecraft.network.play.server.S13PacketDestroyEntities;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.core.Nebula;
import wtf.nebula.client.impl.event.base.Era;
import wtf.nebula.client.impl.event.impl.move.EventMotionUpdate;
import wtf.nebula.client.impl.event.impl.network.EventPacket;
import wtf.nebula.client.impl.event.impl.render.EventRender3D;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;
import wtf.nebula.client.impl.module.active.Colors;
import wtf.nebula.client.utils.client.Timer;
import wtf.nebula.client.utils.player.PlayerUtils;
import wtf.nebula.client.utils.player.RotationUtils;
import wtf.nebula.client.utils.render.RenderEngine;
import wtf.nebula.client.utils.render.enums.Dimension;
import wtf.nebula.client.utils.render.renderers.impl.three.FilledBox;
import wtf.nebula.client.utils.world.DamageUtils;
import wtf.nebula.client.utils.world.WorldUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AutoCrystal extends ToggleableModule {

    private final Property<Float> range = new Property<>(4.0f, 1.0f, 6.0f, "Range", "r", "dist");
    private final Property<Float> wallRange = new Property<>(2.5f, 1.0f, 6.0f, "Wall Range", "wallrange", "wr", "walldist");
    private final Property<Float> targetRange = new Property<>(12.0f, 6.0f, 20.0f, "Target Range", "trange", "targetrange");

    private final Property<Boolean> place = new Property<>(true, "Place", "placedown");
    private final Property<Double> placeSpeed = new Property<>(20.0, 0.1, 20.0, "Place Speed", "placespeed")
            .setVisibility(place::getValue);
    private final Property<Boolean> explode = new Property<>(true, "Explode", "destroy", "break");
    private final Property<Double> explodeSpeed = new Property<>(20.0, 0.1, 20.0, "Explode Speed", "explodespeed")
            .setVisibility(explode::getValue);

    private final Property<Boolean> swing = new Property<>(true, "Swing", "swingarm");
    private final Property<Boolean> rotate = new Property<>(true, "Rotate", "rot", "face");

    private final Property<Float> minDamage = new Property<>(6.0f, 0.0f, 19.0f, "Min Damage", "minimumdamage", "mindamage");
    private final Property<Boolean> suicide = new Property<>(false, "Suicide", "kms");
    private final Property<Float> maxLocal = new Property<>(12.0f, 0.0f, 20.0f, "Max Local", "maxlocal")
            .setVisibility(() -> !suicide.getValue());

    private final Property<Swap> swap = new Property<>(Swap.CLIENT, "Swap", "s", "switch");

    private final Property<Integer> existed = new Property<>(0, 0, 10, "Existed", "ticksexisted");
    private final Property<Boolean> await = new Property<>(false, "Await", "wait");

    private EntityPlayer target = null;
    private Vec3 placePos = null;
    private float placeDamage = 0.0f;

    private int entityTarget = -1;

    private final int[] crystals = new int[10];
    private int crystalIndex = 0;
    private int currentCrystals = 0;
    private boolean attacked = false;

    private final float[] responseTimes = new float[10];
    private int responseIndex = 0;
    private float responseTime = 0.0f;

    private final Timer placeTimer = new Timer();
    private final Timer explodeTimer = new Timer();
    private final Timer crystalCounterTimer = new Timer();
    private long responseTimeAt = -1L;

    private final Map<Integer, EntityEnderCrystal> crystalMap = new ConcurrentHashMap<>();

    public AutoCrystal() {
        super("Auto Crystal", new String[]{"autocrystal", "crystalaura", "ac", "ca"}, ModuleCategory.COMBAT);
        offerProperties(range, wallRange, targetRange, place, placeSpeed, explode, explodeSpeed, swing, rotate, minDamage, suicide, maxLocal, swap, existed, await);
    }

    @Override
    protected void onDisable() {
        super.onDisable();

        currentCrystals = 0;
        for (int i = 0; i < 10; ++i) {
            crystals[i] = 0;
        }

        responseTime = 0.0f;
        for (int i = 0; i < 10; ++i) {
            responseTimes[i] = 0.0f;
        }

        target = null;
        placePos = null;
        placeDamage = 0.0f;

        responseTimeAt = -1L;
        responseIndex = 0;

        crystalIndex = 0;
        attacked = false;
    }

    @Override
    public String getTag() {

        String str = "";

        float total = 0.0f;
        int count = 0;
        for (int i = 0; i < 10; ++i) {
            float t = responseTimes[i];
            if (t > 0.0f) {
                total += t;
                count += 1;
            }
        }

        if (count > 0 && total > 0.0f) {
            str += String.format("%.2f", total / (float) count);
        }

        total = 0.0f;
        count = 0;
        for (int i = 0; i < 10; ++i) {
            float t = crystals[i];
            if (t > 0.0f) {
                total += t;
                count += 1;
            }
        }

        if (!str.isEmpty()) {
            str += " ";
        }

        if (total > 0 && count > 0.0f) {
            str += (int) (total / count);
        } else {
            str += currentCrystals;
        }

        return str;
    }

    @EventListener
    public void onMotionUpdate(EventMotionUpdate event) {
        if (mc.thePlayer.getHealth() < maxLocal.getValue() || mc.thePlayer.isDead || mc.thePlayer.ticksExisted == 0) {
            return;
        }

        if (target != null && !checkTargetValidity(target)) {
            target = null;
        }

        calculate();

        int crystalSlot = -1;
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
            if (stack == null || !(stack.getItem().equals(Items.spawn_egg))) {
                continue;
            }

            if (stack.getItemDamage() == 200) {
                crystalSlot = i;
                break;
            }
        }

        if (crystalSlot == -1) {
            return;
        }

        if (crystalCounterTimer.hasPassed(1000L, true)) {
            crystals[crystalIndex % 10] = currentCrystals;
            currentCrystals = 0;
            ++crystalIndex;
        }

        if (placePos != null) {

            if (explode.getValue() && !await.getValue()) {

                if (entityTarget == -1) {
                    for (Entity entity : new ArrayList<>(mc.theWorld.loadedEntityList)) {
                        if (!(entity instanceof EntityEnderCrystal) || entity.isDead) {
                            continue;
                        }

                        if (entity.ticksExisted < existed.getValue()) {
                            continue;
                        }

                        if (entity.posX - 0.5 == placePos.xCoord && entity.posY == placePos.yCoord && entity.posZ - 0.5 == placePos.zCoord) {
                            entityTarget = entity.getEntityId();
                            break;
                        }
                    }
                }

                if (entityTarget != -1 && explodeTimer.getTimePassedMs() / 50.0 >= 20.0 - explodeSpeed.getValue()) {
                    if (rotate.getValue() && event.getEra().equals(Era.PRE)) {
                        Nebula.getInstance().getRotationManager().setRotations(RotationUtils.calcAngles(placePos.addVector(0.5, 0.0, 0.5).offset(EnumFacing.UP)));
                        return;
                    }

                    attackEntityId(entityTarget);
                    attacked = true;

                    explodeTimer.resetTime();
                    if (swing.getValue()) {
                        mc.thePlayer.swingItem();
                    } else {
                        mc.thePlayer.swingItemSilent();
                    }
                }


            }

            if (place.getValue()) {
                switch (swap.getValue()) {
                    case PACKET: {

                        if (Nebula.getInstance().getInventoryManager().serverSlot != crystalSlot) {
                            mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(crystalSlot));
                        }

                        break;
                    }

                    case CLIENT: {
                        mc.thePlayer.inventory.currentItem = crystalSlot;
                        break;
                    }

                    case NONE: {
                        if (mc.thePlayer.inventory.currentItem != crystalSlot) {
                            return;
                        }
                        break;
                    }
                }

                if (placeTimer.getTimePassedMs() / 50.0 >= 20.0 - placeSpeed.getValue() && canPlaceAt(placePos)) {

                    if (rotate.getValue() && event.getEra().equals(Era.PRE)) {
                        Nebula.getInstance().getRotationManager().setRotations(RotationUtils.calcAngles(placePos.addVector(0.5, 0.0, 0.5).offset(EnumFacing.DOWN)));
                        return;
                    }

                    boolean result = mc.playerController.onPlayerRightClick(
                            mc.thePlayer,
                            mc.theWorld,
                            Nebula.getInstance().getInventoryManager().getHeld(),
                            (int) placePos.xCoord,
                            (int) placePos.yCoord - 1,
                            (int) placePos.zCoord,
                            EnumFacing.UP.order_a,
                            placePos.addVector(0.5, 0.5, 0.5)
                    );

                    if (result) {
                        responseTimeAt = System.nanoTime();
                        placeTimer.resetTime();

                        if (swing.getValue()) {
                            mc.thePlayer.swingItem();
                        } else {
                            mc.thePlayer.swingItemSilent();
                        }

                        if (swap.getValue().equals(Swap.PACKET)) {
                            Nebula.getInstance().getInventoryManager().sync();
                        }
                    }

                    // mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(px, py - 1, pz, EnumFacing.UP.order_a, Nebula.getInstance().getInventoryManager().getHeld(), 0.5f, 0.5f, 0.5f));


                }
            }
        }
    }

    @EventListener(recieveCancelled = true)
    public void onPacket(EventPacket event) {
        if (event.getEra().equals(Era.PRE)) {

            if (event.getPacket() instanceof S0EPacketSpawnObject) {
                S0EPacketSpawnObject packet = event.getPacket();
                if (packet.getType() == 51 && placePos != null) {

                    if (existed.getValue() > 0) {
                        return;
                    }

                    int x = (int) ((packet.func_148997_d() / 32.0) - 0.5);
                    int y = (int) (packet.func_148998_e() / 32.0);
                    int z = (int) ((packet.func_148994_f() / 32.0) - 0.5);

                    if (placePos.xCoord == x && placePos.yCoord == y && placePos.zCoord == z) {

                        if (responseTimeAt != -1L) {
                            float diff = (System.nanoTime() - responseTimeAt) / 1000000.0f;
                            responseTimes[responseIndex % 10] = diff;
                            ++responseIndex;
                            responseTimeAt = -1L;
                        }

                        entityTarget = packet.getEntityId();

                        if (await.getValue()) {
                            if (rotate.getValue()) {
                                Nebula.getInstance().getRotationManager().setRotations(RotationUtils.calcAngles(placePos.offset(EnumFacing.UP)));
                                return;
                            }

                            attackEntityId(entityTarget);
                            attacked = true;
                            explodeTimer.resetTime();

                            if (swing.getValue()) {
                                mc.thePlayer.swingItem();
                            } else {
                                mc.thePlayer.swingItemSilent();
                            }

                            entityTarget = -1;
                        }
                    }
                }
            } else if (event.getPacket() instanceof S13PacketDestroyEntities) {
                S13PacketDestroyEntities packet = event.getPacket();

                for (int id : packet.func_149098_c()) {
                    Entity entity = mc.theWorld.getEntityByID(id);

                    if (id == entityTarget) {

                        if (attacked) {
                            attacked = false;
                            currentCrystals += 1;
                        }

                        entityTarget = -1;
                    }

                    if (entity != null && entity instanceof EntityEnderCrystal && !entity.isDead) {
//                        mc.theWorld.removeEntity(entity);
//                        mc.theWorld.removePlayerEntityDangerously(entity);
                    }
                }
            } else if (event.getPacket() instanceof S27PacketExplosion) {
                S27PacketExplosion packet = event.getPacket();

                double x = packet.getX();
                double y = packet.getY();
                double z = packet.getZ();

                float size = packet.func_149146_i();

                for (Entity entity : new ArrayList<>(mc.theWorld.loadedEntityList)) {

                    if (entity.getEntityId() == entityTarget) {

                        if (attacked) {
                            attacked = false;
                            currentCrystals += 1;
                        }

                        entityTarget = -1;
                    }

                    if (entity instanceof EntityEnderCrystal && !entity.isDead) {
                        if (entity.getEntityId() == entityTarget) {
                            entityTarget = -1;
                        }

                        double dist = mc.thePlayer.getDistanceSq(x, y, z);
                        if (dist < size * size) {
//                            mc.theWorld.removeEntity(entity);
//                            mc.theWorld.removePlayerEntityDangerously(entity);
                        }
                    }
                }
            } else if (event.getPacket() instanceof S29PacketSoundEffect) {
                S29PacketSoundEffect packet = event.getPacket();

                if (!packet.func_149212_c().contains("random.explode")) {
                    return;
                }

                double size = 6.0;

                for (Entity entity : new ArrayList<>(mc.theWorld.loadedEntityList)) {
                    if (entity instanceof EntityEnderCrystal && !entity.isDead) {

                        if (entity.getEntityId() == entityTarget) {
                            if (attacked) {
                                attacked = false;
                                currentCrystals += 1;
                            }

                            entityTarget = -1;
                        }

                        double dist = mc.thePlayer.getDistanceSq(packet.x, packet.y, packet.z);
                        if (dist < size * size) {
//                            mc.theWorld.removeEntity(entity);
//                            mc.theWorld.removePlayerEntityDangerously(entity);
                        }
                    }
                }
            }
        }
    }

    @EventListener
    public void onRender3D(EventRender3D event) {
        if (placePos != null) {
            RenderEngine.of(Dimension.THREE)
                    .add(new FilledBox(new AxisAlignedBB(placePos).offset(-RenderManager.renderPosX, -RenderManager.renderPosY, -RenderManager.renderPosZ), Colors.getClientColor(77)))
                    .render();
        }
    }

    private void attackEntityId(int id) {
        C02PacketUseEntity packet = new C02PacketUseEntity();
        packet.action = C02PacketUseEntity.Action.ATTACK;
        packet.entityId = id;
        mc.thePlayer.sendQueue.addToSendQueue(packet);
    }

    private boolean checkTargetValidity(EntityPlayer t) {
        return t != null
                && !mc.thePlayer.equals(t)
                && t.getHealth() > 0.0f
                && !Nebula.getInstance().getFriendManager().isFriend(t.getCommandSenderName())
                && !(mc.thePlayer.getDistanceSqToEntity(t) > targetRange.getValue() * targetRange.getValue());
    }

    private void calculate() {
        if (placePos != null && target != null) {

            double dist = canSeePos(placePos) ? range.getValue() : wallRange.getValue();
            if (mc.thePlayer.getDistanceSq(placePos.xCoord + 0.5, placePos.yCoord + 1.0, placePos.zCoord + 0.5) > dist) {
                placePos = null;
                placeDamage = 0.0f;
            }

            if (placePos != null) {

                float damageAt = calculateAt(target, placePos);
                if (damageAt > minDamage.getValue()) {

                    float localDamageAt = calculateAt(mc.thePlayer, placePos);
                    if (localDamageAt > damageAt || localDamageAt > maxLocal.getValue()) {
                        placePos = null;
                        placeDamage = 0.0f;
                    }
                }
            }
        }

        EntityPlayer t = target;
        Vec3 pos = placePos;
        float dmg = placePos == null ? minDamage.getValue() : placeDamage;

        int r = range.getValue().intValue();
        for (int x = -r; x <= r; ++x) {
            for (int y = -r; y <= r; ++y) {
                for (int z = -r; z <= r; ++z) {

                    //Vec3 at = PlayerUtils.getPosUnder().addVector(x, y, z);

                    double xCoord = Math.floor(mc.thePlayer.posX) + x;
                    double yCoord = mc.thePlayer.boundingBox.minY + y;
                    double zCoord = Math.floor(mc.thePlayer.posZ) + z;

                    Vec3 at = new Vec3(Vec3.fakePool, xCoord, yCoord, zCoord);
                    if (!canPlaceAt(at)) {
                        continue;
                    }

                    float localDamageAt = calculateAt(mc.thePlayer, at);

                    if (target != null) {
                        float damageAt = calculateAt(target, at);
                        if (damageAt > minDamage.getValue()) {
                            if (localDamageAt > damageAt || localDamageAt > maxLocal.getValue()) {
                                continue;
                            }

                            if (damageAt > dmg) {
                                pos = at;
                                dmg = damageAt;
                            }
                        }
                    }

                    for (EntityPlayer player : new ArrayList<>(mc.theWorld.playerEntities)) {

                        if (checkTargetValidity(player)) {
                            float damageAt = calculateAt(player, at);
                            if (damageAt > minDamage.getValue()) {
                                if (localDamageAt > damageAt || localDamageAt > maxLocal.getValue()) {
                                    continue;
                                }

                                if (damageAt > dmg) {
                                    pos = at;
                                    dmg = damageAt;
                                }
                            }
                        }

                    }
                }
            }
        }

        placePos = pos;
        placeDamage = dmg;
        target = t;
    }

    private boolean canPlaceAt(Vec3 vec) {
        double x = vec.xCoord;
        double y = vec.yCoord;
        double z = vec.zCoord;

        if (WorldUtils.isReplaceable(vec) && !WorldUtils.isReplaceable(x, y - 1, z) && WorldUtils.isReplaceable(x, y + 1, z)) {

            AxisAlignedBB bb = new AxisAlignedBB(vec);
            for (Entity entity : new ArrayList<>(mc.theWorld.loadedEntityList)) {
                if (entity.isEntityAlive() && !(entity instanceof EntityItem) && !(entity instanceof EntityEnderCrystal)) {

                    if (entity.boundingBox.copy().intersectsWith(bb)) {
                        return false;
                    }
                }
            }

            return true;
        }

        return false;
    }

    private boolean canSeePos(Vec3 pos) {
        return mc.theWorld.rayTraceBlocks(
                pos.addVector(0.0, mc.thePlayer.getEyeHeight(), 0.0),
                mc.thePlayer.getPosition(1.0f),
                false
        ) == null;
    }

    private float calculateAt(EntityPlayer t, Vec3 pos) {
        return DamageUtils.calcDamage(t,
                new Vec3(Vec3.fakePool,
                        pos.xCoord + 0.5,
                        pos.yCoord + 0.5,
                        pos.zCoord + 0.5),
                6.0f);

    }

    public enum Swap {
        NONE, CLIENT, PACKET
    }
}
