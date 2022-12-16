package wtf.nebula.client.impl.module.world;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraft.network.play.server.S36PacketSignEditorOpen;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import wtf.nebula.client.api.config.Config;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.core.Nebula;
import wtf.nebula.client.impl.event.base.Era;
import wtf.nebula.client.impl.event.impl.move.EventMotionUpdate;
import wtf.nebula.client.impl.event.impl.network.EventPacket;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;
import wtf.nebula.client.utils.client.Timer;
import wtf.nebula.client.utils.io.FileUtils;
import wtf.nebula.client.utils.player.PlayerUtils;
import wtf.nebula.client.utils.player.RotationUtils;
import wtf.nebula.client.utils.world.WorldUtils;

public class AutoSign extends ToggleableModule {
    public static final String[] SIGN_LINES = { "", "", "", "" };

    private final Property<Double> speed = new Property<>(15.0, 0.1, 20.0, "Speed", "s");
    private final Property<Double> range = new Property<>(4.0, 1.0, 6.0, "Range", "r", "dist", "distance");
    private final Property<Boolean> swing = new Property<>(true, "Swing");
    private final Property<Boolean> rotate = new Property<>(true, "Rotate", "rot", "face");
    private final Property<Swap> swap = new Property<>(Swap.CLIENT, "Swap", "s", "switch");
    private final Property<Boolean> sameY = new Property<>(true, "SameY");
    private final Property<Boolean> instant = new Property<>(false, "Instant");

    private final Timer timer = new Timer();
    private boolean placing = false;
    private boolean waiting = false;

    private final Config config;

    public AutoSign() {
        super("Auto Sign", new String[]{"autosign"}, ModuleCategory.WORLD);
        offerProperties(speed, range, swing, rotate, swap, sameY, instant);

        config = new Config("autosign_text.txt") {

            @Override
            public void load(String element) {
                if (element == null || element.isEmpty()) {
                    return;
                }

                String[] split = element.split("\n");
                if (split.length > 0) {

                    for (int i = 0; i < 4; ++i) {

                        try {
                            SIGN_LINES[i] = split[i].trim().substring(0, 15);
                        } catch (IndexOutOfBoundsException ignored) {

                        }
                    }
                }
            }

            @Override
            public void save() {
                FileUtils.write(getFile(), String.join("\n", SIGN_LINES));
            }
        };
    }

    @Override
    protected void onEnable() {
        super.onEnable();

        if (config != null) {

            try {
                config.load(FileUtils.read(config.getFile()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        placing = false;
        waiting = false;
    }

    @EventListener
    public void onMotionUpdate(EventMotionUpdate event) {

        int slot = -1;

        for (int i = 0; i < 9; ++i) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
            if (stack != null && stack.getItem().equals(Items.sign)) {
                slot = i;
                break;
            }
        }

        if (slot == -1) {
            placing = false;
            return;
        }

        if (swap.getValue().equals(Swap.NONE) && mc.thePlayer.inventory.currentItem != slot) {
            placing = false;
            return;
        }

        Vec3 placePos = null;

        int r = range.getValue().intValue();
        for (int x = -r; x <= r; ++x) {
            for (int z = -r; z <= r; ++z) {

                for (int y = (sameY.getValue() ? 0 : -r); (sameY.getValue() ? (y < 1) : (y <= r)); ++y) {
                    Vec3 pos = PlayerUtils.getPosAt().addVector(x, y, z);

                    if (mc.thePlayer.getDistanceSq(pos.xCoord + 0.5, pos.yCoord + 1.0, pos.zCoord + 0.5) > r * r) {
                        continue;
                    }

                    if (!WorldUtils.isReplaceable(pos)) {
                        continue;
                    }

                    if (WorldUtils.isReplaceable(pos.offset(EnumFacing.DOWN))) {
                        continue;
                    }

                    placePos = pos;
                    break;
                }

            }
        }

        if (placePos == null) {
            placing = false;
            return;
        }

        placing = true;

        if (rotate.getValue()) {
            float[] angles = RotationUtils.calcAngles(placePos);
            Nebula.getInstance().getRotationManager().setRotations(angles);
        }

        if (waiting && !instant.getValue()) {
            return;
        }

        if (timer.getTimePassedMs() / 50.0 >= 20.0 - speed.getValue()) {

            boolean needsSwap = Nebula.getInstance().getInventoryManager().serverSlot != slot;
            if (needsSwap) {
                switch (swap.getValue()) {
                    case CLIENT: {
                        mc.thePlayer.inventory.currentItem = slot;
                        break;
                    }

                    case SERVER: {
                        mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(slot));
                        break;
                    }
                }
            }

            boolean sneak = WorldUtils.SNEAK_BLOCKS.contains(WorldUtils.getBlock(placePos));
            if (sneak) {
                mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, 1));
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
                timer.resetTime();

                if (swing.getValue()) {
                    mc.thePlayer.swingItem();
                } else {
                    mc.thePlayer.swingItemSilent();
                }

                if (instant.getValue()) {
                    waiting = false;
                    mc.thePlayer.sendQueue.addToSendQueue(new C12PacketUpdateSign(
                            (int) placePos.xCoord, (int) placePos.yCoord, (int) placePos.zCoord, SIGN_LINES));
                } else {
                    waiting = true;
                }
            }

            if (needsSwap && swap.getValue().equals(Swap.SERVER)) {
                mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
            }

            if (sneak) {
                mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, 2));
            }
        }
    }

    @EventListener
    public void onPacket(EventPacket event) {
        if (event.getPacket() instanceof S36PacketSignEditorOpen && placing) {

            if (waiting) {
                S36PacketSignEditorOpen packet = event.getPacket();

                mc.thePlayer.sendQueue.addToSendQueue(new C12PacketUpdateSign(
                        packet.func_149129_c(), packet.func_149128_d(), packet.func_149127_e(), SIGN_LINES));

                waiting = false;
                timer.resetTime();
            }

            event.setCancelled(true);
        }
    }

    public enum Swap {
        NONE, CLIENT, SERVER
    }
}
