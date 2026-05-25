package us.nebula.client.cheat.impl.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import us.nebula.client.Nebula;
import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatInstance;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.listener.EventListener;
import us.nebula.client.listener.Subscribe;
import us.nebula.client.listener.event.input.EventMouse;
import us.nebula.client.util.player.ChatUtil;

/**
 * @author xgraza
 * @since 05/23/26
 */
@CheatManifest(name = "MCF",
        description = "Adds or removes a player from your friend list when you middle click on them",
        category = CheatCategory.PLAYER)
public final class MCFCheat extends Cheat
{
    @CheatInstance
    public static MCFCheat INSTANCE;

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
        if (Nebula.INSTANCE.getFriendManager().isFriend(name))
        {
            ChatUtil.sendNebula("Unfriended %s%s", EnumChatFormatting.RED, name);
            Nebula.INSTANCE.getFriendManager().removeFriend(name);
        } else
        {
            ChatUtil.sendNebula("Friended %s%s", EnumChatFormatting.GREEN, name);
            Nebula.INSTANCE.getFriendManager().addFriend(name);
        }
    };
}
