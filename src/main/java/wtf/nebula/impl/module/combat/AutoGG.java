package wtf.nebula.impl.module.combat;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EnumChatFormatting;
import net.minecraft.src.Packet3Chat;
import wtf.nebula.event.DeathEvent;
import wtf.nebula.event.TickEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;
import wtf.nebula.util.FileUtil;
import wtf.nebula.util.MathUtil;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AutoGG extends Module {
    public AutoGG() {
        super("AutoGG", ModuleCategory.COMBAT);
    }

    private List<String> messages = null;
    public static EntityPlayer target = null;

    @Override
    protected void onDeactivated() {
        super.onDeactivated();
        messages = null;
        target = null;
    }

    @EventListener
    public void onTick(TickEvent event) {
        if (messages == null) {
            if (!Files.exists(FileUtil.AUTOGG)) {
                FileUtil.create(FileUtil.AUTOGG);
                sendChatMessage("Created auto gg file! Look in " + EnumChatFormatting.GREEN + FileUtil.AUTOGG);

                return;
            }

            String content = FileUtil.read(FileUtil.AUTOGG);
            if (content != null && !content.isEmpty()) {
                messages = Arrays.asList(content.trim().split("\n"));
            }

            else {
                messages = new ArrayList<>();
            }
        }
    }

    @EventListener
    public void onDeath(DeathEvent event) {
        if (event.getPlayer().equals(target)) {


            String message = "gg %player%!";
            try {
                message = messages.get(MathUtil.RNG.nextInt(messages.size() - 1));
            } catch (IndexOutOfBoundsException ignored) {

            }

            message = message.replaceAll("%player%", target.getEntityName());
            mc.thePlayer.sendQueue.addToSendQueue(new Packet3Chat(message));

            target = null;
        }
    }
}
