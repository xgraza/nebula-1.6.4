package ez.nebula.client.impl.module.player;

import ez.nebula.client.Nebula;
import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.listener.event.input.EventMouse;
import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleInstance;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.util.minecraft.player.ChatUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;

/**
 * @author xgraza
 * @since 05/23/26
 */
@ModuleManifest(name = "MCF",
        description = "Adds or removes a player from your friend list when you middle click on them",
        category = ModuleCategory.PLAYER)
public final class MCFModule extends Module
{
    @ModuleInstance
    public static MCFModule INSTANCE;

    @Subscribe
    private final EventListener<EventMouse> mouseEventListener = event ->
    {
        if (MC.thePlayer == null || MC.objectMouseOver == null || event.getMouseButton() != 2)
        {
            return;
        }
        final MovingObjectPosition result = MC.objectMouseOver;
        if (result.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY
                || !(result.entityHit instanceof EntityPlayer))
        {
            return;
        }
        final EntityPlayer player = (EntityPlayer) result.entityHit;
        final String name = player.getCommandSenderName();
        if (Nebula.FRIENDS.has(name))
        {
            ChatUtil.sendNebula("Unfriended %s%s", EnumChatFormatting.RED, name);
            Nebula.FRIENDS.remove(name);
        } else
        {
            ChatUtil.sendNebula("Friended %s%s", EnumChatFormatting.GREEN, name);
            Nebula.FRIENDS.add(name);
        }
    };
}
