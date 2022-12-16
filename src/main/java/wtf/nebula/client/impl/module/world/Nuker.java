package wtf.nebula.client.impl.module.world;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.core.Nebula;
import wtf.nebula.client.impl.event.impl.client.EventTick;
import wtf.nebula.client.impl.event.impl.render.EventRender3D;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;
import wtf.nebula.client.utils.player.PlayerUtils;
import wtf.nebula.client.utils.player.RotationUtils;
import wtf.nebula.client.utils.render.RenderEngine;
import wtf.nebula.client.utils.render.enums.Dimension;
import wtf.nebula.client.utils.render.renderers.impl.three.FilledBox;
import wtf.nebula.client.utils.world.WorldUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Nuker extends ToggleableModule {
    private final Property<Mode> mode = new Property<>(Mode.PACKET, "Mode", "m", "type");
    private final Property<Double> range = new Property<>(4.0, 1.0, 6.0, "Range", "distance", "reach", "dist");
    private final Property<Boolean> autoSwitch = new Property<>(true, "Auto Switch", "autoswitch", "swap");
    private final Property<Boolean> rotate = new Property<>(true, "Rotate", "rot", "face");
    private final Property<Boolean> swing = new Property<>(true, "Swing", "s", "swingarm");
    private final Property<Boolean> render = new Property<>(true, "Render", "draw");

    private int x, y, z, face;
    private boolean breaking = false;
    private boolean broken = false;
    private double progress = 0.0;

    public Nuker() {
        super("Nuker", new String[]{"nukeys", "hiroshima"}, ModuleCategory.WORLD);
        offerProperties(mode, range, autoSwitch, rotate, swing, render);
    }

    @Override
    protected void onDisable() {
        super.onDisable();

        if (breaking && !isNull()) {
            if (mode.getValue().equals(Mode.PACKET)) {
                mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(1, x, y, z, -1));
            } else {
                mc.playerController.resetBlockRemoving();
            }
        }

        breaking = false;
        broken = false;
        progress = 0.0;
        x = -1;
        y = -1;
        z = -1;
        face = -1;
    }

    @EventListener
    public void onRender3D(EventRender3D event) {
        if (render.getValue() && x != -1 && y != -1 && z != -1 && breaking) {
            if (mode.getValue().equals(Mode.PACKET)) {
                AxisAlignedBB bb = WorldUtils.getBlock(x, y, z).getCollisionBoundingBoxFromPool(mc.theWorld, x, y, z);
                if (bb == null) {
                    bb = new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1);
                }

                Vec3 center = bb.getCenter();
                AxisAlignedBB box = new AxisAlignedBB(center, 0.0);

                double factor = MathHelper.clamp_double(progress, 0.0, 1.0);
                box = box.expand(factor * 0.5, factor * 0.5, factor * 0.5);

                RenderEngine.of(Dimension.THREE)
                        .add(new FilledBox(box
                                .offset(-RenderManager.renderPosX, -RenderManager.renderPosY, -RenderManager.renderPosZ),
                                (factor >= 0.95 ? new Color(0, 255, 0, 80) : new Color(255, 0, 0, 80)).getRGB()))
                        .render();
            } else {
                AxisAlignedBB box = WorldUtils.getBlock(x, y, z).getCollisionBoundingBoxFromPool(mc.theWorld, x, y, z);
                if (box == null) {
                    box = new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1);
                }

                box = box.offset(-RenderManager.renderPosX, -RenderManager.renderPosY, -RenderManager.renderPosZ);

                RenderEngine.of(Dimension.THREE)
                        .add(new FilledBox(box
                                .offset(-RenderManager.renderPosX, -RenderManager.renderPosY, -RenderManager.renderPosZ),
                                new Color(0, 255, 0, 80).getRGB()))
                        .render();
            }
        }
    }

    @EventListener
    public void onTick(EventTick event) {
        if (x != -1 && y != -1 && z != -1) {

            if (WorldUtils.isReplaceable(x, y, z)) {

                if (mode.getValue().equals(Mode.PACKET)) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(1, x, y, z, -1));
                } else {
                    mc.playerController.resetBlockRemoving();
                }

                progress = 0.0;
                x = -1;
                y = -1;
                z = -1;
                face = -1;
                breaking = false;
                broken = false;

                return;
            }

            if (rotate.getValue()) {
                float[] angles = RotationUtils.calcAngles(new Vec3(Vec3.fakePool, x + 0.5, y + 0.5, z + 0.5));
                Nebula.getInstance().getRotationManager().setRotations(angles);
            }

            int slot = Nebula.getInstance().getInventoryManager().serverSlot;

            if (autoSwitch.getValue()) {
                int nSlot = AutoTool.getBestToolAt(x, y, z);
                if (slot != nSlot && nSlot != -1) {
                    slot = nSlot;

                    if (mode.getValue().equals(Mode.PACKET)) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(slot));
                    } else if (mode.getValue().equals(Mode.VANILLA)) {
                        mc.thePlayer.inventory.currentItem = slot;
                    }
                }
            }

            if (mode.getValue().equals(Mode.PACKET)) {

                if (!breaking) {
                    breaking = true;
                    mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(0, x, y, z, face));

                    if (swing.getValue()) {
                        mc.thePlayer.swingItem();
                    } else {
                        mc.thePlayer.swingItemSilent();
                    }
                }

                progress += FastBreak.getStrength(x, y, z, slot);
                if (progress >= 0.9 && breaking && !broken) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(2, x, y, z, face));
                    broken = true;
                }
            } else if (mode.getValue().equals(Mode.VANILLA)) {

                mc.playerController.onPlayerDamageBlock(x, y, z, face);
                if (mc.thePlayer.isCurrentToolAdventureModeExempt(x, y, z))
                {
                    mc.effectRenderer.addBlockHitEffects(x, y, z, face);
                    mc.thePlayer.swingItem();
                }

                breaking = true;

            } else if (mode.getValue().equals(Mode.CREATIVE)) {
                mc.playerController.onPlayerDamageBlock(x, y, z, face);
            }
        }

        updateNextRemoval();
    }

    private void updateNextRemoval() {
        double r = range.getValue();

        List<Vec3> vecList = new ArrayList<>();

        for (double x = -r; x <= r; ++x) {
            for (double y = -r; y <= r; ++y) {
                for (double z = -r; z <= r; ++z) {
                    Vec3 vec = PlayerUtils.getPosAt().addVector(x, y, z);

                    if (mc.thePlayer.getDistanceSq(vec.xCoord + 0.5, vec.yCoord + 1.0, vec.zCoord + 0.5) >= r * r) {
                        continue;
                    }

                    Block at = WorldUtils.getBlock(vec);
                    if (at.blockHardness == -1 || at.equals(Blocks.air)) {
                        continue;
                    }

                    vecList.add(vec);
                }
            }
        }

        Vec3 closest = vecList.stream()
                .min(Comparator.comparingDouble((s) -> mc.thePlayer.getDistanceSq(s.xCoord, s.yCoord, s.zCoord)))
                .orElse(null);

        if (closest != null) {
            x = (int) closest.xCoord;
            y = (int) closest.yCoord;
            z = (int) closest.zCoord;
            face = 0;
        } else {
            x = -1;
            y = -1;
            z = -1;
            face = -1;
        }
    }

    public enum Mode {
        PACKET, VANILLA, CREATIVE
    }
}
