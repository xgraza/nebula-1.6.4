package wtf.nebula.client.impl.module.movement;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.impl.event.base.Era;
import wtf.nebula.client.impl.event.impl.client.EventTick;
import wtf.nebula.client.impl.event.impl.move.EventMotionUpdate;
import wtf.nebula.client.impl.event.impl.move.EventSlowdown;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;

public class NoSlow extends ToggleableModule {
    private final Property<Mode> mode = new Property<>(Mode.NCP, "Mode", "m", "type");
    private final Property<Boolean> inventoryMove = new Property<>(true, "Inventory Move");
    private final Property<Float> arrowSpeed = new Property<>(5.0f, 0.0f, 15.0f, "Arrow Speed", "arrowspeed");

    private KeyBinding[] BINDS;

    public NoSlow() {
        super("No Slow", new String[]{"noslow", "noslowdown"}, ModuleCategory.MOVEMENT);
        offerProperties(mode, inventoryMove, arrowSpeed);
    }

    @Override
    protected void onEnable() {
        super.onEnable();

        BINDS = new KeyBinding[]{
                mc.gameSettings.keyBindForward,
                mc.gameSettings.keyBindRight,
                mc.gameSettings.keyBindLeft,
                mc.gameSettings.keyBindBack,
                mc.gameSettings.keyBindUseItem
        };
    }

    @EventListener
    public void onTick(EventTick event) {
        if (canInventoryMove()) {

            mc.currentScreen.allowUserInput = true;

            for (KeyBinding binding : BINDS) {
                try {
                    KeyBinding.setKeyBindState(binding.keyCode, Keyboard.isKeyDown(binding.keyCode));
                } catch (Exception ignored) {
                }
            }
        }
    }

    @EventListener
    public void onSlowdown(EventSlowdown event) {
        if (!mc.thePlayer.isRiding() && mc.thePlayer.isUsingItem()) {
            event.getInput().moveForward *= 5.0f;
            event.getInput().moveStrafe *= 5.0f;
        }
    }

    @EventListener
    public void onMotionUpdate(EventMotionUpdate event) {
        if (mc.thePlayer.isBlocking() && !mode.getValue().equals(Mode.VANILLA)) {
            if (event.getEra().equals(Era.PRE)) {
                mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(5, 0, 0, 0, 255));
            } else {
                mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(-1, -1, -1, 255, mc.thePlayer.getHeldItem(), 0.0F, 0.0F, 0.0F));
            }
        }

        if (mode.getValue().equals(Mode.NCP_UPDATED) && mc.thePlayer.isUsingItem() && !mc.thePlayer.isBlocking()) {
            mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
        }

        if (canInventoryMove() && event.getEra().equals(Era.PRE)) {
            if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
                mc.thePlayer.rotationPitch -= arrowSpeed.getValue();
            } else if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
                mc.thePlayer.rotationPitch += arrowSpeed.getValue();
            }

            if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
                mc.thePlayer.rotationYaw += arrowSpeed.getValue();
            } else if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
                mc.thePlayer.rotationYaw -= arrowSpeed.getValue();
            }

            mc.thePlayer.rotationPitch = MathHelper.clamp_float(mc.thePlayer.rotationPitch, -90.0f, 90.0f);
        }
    }

    private boolean canInventoryMove() {
        return mc.currentScreen != null
                && !(mc.currentScreen instanceof GuiRepair)
                && !(mc.currentScreen instanceof GuiContainerCreative)
                && !(mc.currentScreen instanceof GuiChat)
                && inventoryMove.getValue();
    }

    public enum Mode {
        VANILLA, NCP, NCP_UPDATED
    }
}
