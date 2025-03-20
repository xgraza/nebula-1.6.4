package us.nebula.impl.command;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import us.nebula.api.manager.command.Command;
import us.nebula.api.manager.command.CommandManifest;
import us.nebula.api.manager.command.CommandResult;
import us.nebula.api.manager.command.argument.type.ItemArgument;
import us.nebula.util.player.ChatUtil;

/**
 * @author xgraza
 * @since 03/20/25
 */
@CommandManifest(aliases = {"inf", "infinite", "createinfinite", "createinf"},
        description = "Creates an infinite in creative mode")
public final class InfiniteCommand extends Command
{
    @Override
    public void build()
    {
        argumentBuilder
                .argument(new ItemArgument("item"), (arg) ->
                {
                    if (MC.playerController.isNotCreative() || !MC.isSingleplayer())
                    {
                        ChatUtil.send("&cYou must be in creative and singleplayer mode");
                        return CommandResult.SUCCESS;
                    }
                    final Item item = arg.getValue();
                    int stackSize = -1;
                    if (item instanceof ItemArmor)
                    {
                        stackSize = 64;
                    }
                    final ItemStack itemStack = new ItemStack(item, stackSize, 0);
                    final EntityItem itemEntity = getSeverPlayer().dropPlayerItemWithRandomChoice(
                            itemStack, false);
                    itemEntity.delayBeforeCanPickup = 0;
                    itemEntity.setOwner(getSeverPlayer().getCommandSenderName());
                    return CommandResult.SUCCESS_DEFAULT;
                });
    }
}
