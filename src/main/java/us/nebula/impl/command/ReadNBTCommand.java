package us.nebula.impl.command;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import us.nebula.api.manager.command.Command;
import us.nebula.api.manager.command.CommandManifest;
import us.nebula.api.manager.command.CommandResult;
import us.nebula.util.player.ChatUtil;

import java.util.Set;

/**
 * @author xgraza
 * @since 03/20/25
 */
@CommandManifest(aliases = {"readnbt"},
        description = "Reads an item's NBT data")
public final class ReadNBTCommand extends Command
{
    @Override
    public void build()
    {
        argumentBuilder.dispatchSingle(() ->
        {
            final ItemStack itemStack = MC.thePlayer.getHeldItem();
            if (itemStack == null)
            {
                ChatUtil.send("&cNo itemstack in hand");
                return CommandResult.SUCCESS;
            }

            NBTTagCompound compound = itemStack.getOriginalNBTData();
            if (compound == null)
            {
                compound = itemStack.getTagCompound();
            }
            if (compound == null || compound.hasNoTags())
            {
                ChatUtil.send("&cThis item has no NBT compound tags.");
                return CommandResult.SUCCESS;
            }

            final Set<String> tagNameList = compound.func_150296_c();
            for (final String key : tagNameList)
            {
                final NBTBase tag = compound.getTag(key);
                ChatUtil.send(key + "->" + tag.toString());
            }

            return CommandResult.SUCCESS;
        });
    }
}
