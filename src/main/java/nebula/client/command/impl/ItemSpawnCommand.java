package nebula.client.command.impl;

import nebula.client.command.Command;
import nebula.client.command.CommandMeta;
import nebula.client.command.CommandResults;
import nebula.client.command.exception.CommandFailureException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;

import static nebula.client.util.player.InventoryUtils.PLAYER_INVENTORY_SIZE;

/**
 * @author Gavin
 * @since 08/18/23
 */
@SuppressWarnings("unused")
@CommandMeta(aliases = {"itemspawn", "spawnitem"},
  description = "Spawns an item",
  syntax = "[item name] <stack size>")
public class ItemSpawnCommand extends Command {
  @Override
  public int execute(String[] args) throws Exception {

    if (mc.playerController.isNotCreative()) throw new CommandFailureException(
      "You are not in creative mode");

    if (args.length < 1) return CommandResults.INVALID_SYNTAX;

    Item item = Item.getItemFromNamespace(args[0]);
    if (item == null) throw new CommandFailureException(
      "That item does not exist");

    ItemStack itemStack = new ItemStack(item);

    if (args.length == 2) {
      int size = 1;
      try {
        size = Integer.parseInt(args[1]);
      } catch (NumberFormatException ignored) {
      }

      itemStack.stackSize = size;
    }

    mc.thePlayer.sendQueue.addToSendQueueSilent(new C10PacketCreativeInventoryAction(
      mc.thePlayer.inventory.currentItem
        + PLAYER_INVENTORY_SIZE, itemStack));

    return CommandResults.SUCCESS;
  }
}
