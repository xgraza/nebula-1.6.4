package ez.nebula.client.impl.module.combat;

import ez.nebula.client.api.listener.event.world.EventAddEntity;
import ez.nebula.client.api.setting.NumberSetting;
import ez.nebula.client.ClientConfig;
import ez.nebula.client.Nebula;
import ez.nebula.client.impl.module.player.FreecamModule;
import ez.nebula.client.impl.module.world.FakePlayerModule;
import ez.nebula.client.util.io.SoundUtil;
import ez.nebula.client.util.minecraft.player.ChatUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.setting.Setting;
import ez.nebula.client.api.listener.event.game.EventUpdate;
import net.minecraft.util.IChatComponent;

import java.util.List;

/**
 * @author xgraza
 * @since 03/30/25
 */
@ModuleManifest(name = "AutoLog",
        description = "Automatically logs you off a server on specific conditions",
        category = ModuleCategory.COMBAT)
public final class AutoLogModule extends Module
{
    private final Setting<Boolean> healthSetting = builder("Health", false)
            .setDescription("If to log off when your health goes below a value")
            .build();
    private final NumberSetting<Float> healthLevelSetting = numberBuilder("Health Level", 6.0f)
            .setMin(1.0f)
            .setMax(19.5f)
            .setScale(0.5f)
            .setDescription("At what health should you automatically be logged off")
            .setVisibility((value) -> healthSetting.getValue())
            .build();

    private final Setting<Boolean> visualRangeSetting = builder("Visual Range", false)
            .setDescription("If to log off when a player enters your visual range")
            .build();
    private final Setting<Boolean> friendsSetting = builder("Friends", false)
            .setDescription("If to log off even if the player is your friend")
            .setVisibility((value) -> visualRangeSetting.getValue())
            .build();

    @Subscribe
    private final EventListener<EventAddEntity> addEntityEventListener = event ->
    {
        if (event.getEntity() instanceof EntityPlayer && visualRangeSetting.getValue())
        {
            final EntityPlayer player = (EntityPlayer) event.getEntity();
            if (!friendsSetting.getValue() && Nebula.INSTANCE.getFriendManager().isFriend(player))
            {
                return;
            }
            // ignore fake players & freecam entity
            if (player.getEntityId() == FreecamModule.CAMERA_ENTITY_ID
                    || player.getEntityId() == FakePlayerModule.INSTANCE.getFakePlayerEntityID())
            {
                return;
            }
            closeChannel("&c%s&7 entered your visual range!", player.getCommandSenderName());
            toggle();
        }
    };

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        if (healthSetting.getValue())
        {
            if (MC.thePlayer.getHealth() > healthLevelSetting.getValue())
            {
                return;
            }
            closeChannel("Health was at &c%.1f&7!", MC.thePlayer.getHealth());
            toggle();
        }
    };

    private void closeChannel(final String text, final Object... format)
    {
        if (ClientConfig.FOLK_VALLEY)
        {
            SoundUtil.gottaLog();
        }

        final IChatComponent base = new ChatComponentText("")
                .appendSibling(new ChatComponentText("[AutoLog] ")
                        .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)))
                .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GRAY));
        final List<IChatComponent> componentList = ChatUtil.format(base, text, format);
        if (componentList.isEmpty())
        {
            return;
        }
        MC.thePlayer.sendQueue.getNetworkManager().closeChannel(componentList.get(0));
    }
}
