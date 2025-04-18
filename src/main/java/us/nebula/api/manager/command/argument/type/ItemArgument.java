package us.nebula.api.manager.command.argument.type;

import net.minecraft.item.Item;
import us.nebula.api.manager.command.argument.Argument;
import us.nebula.api.manager.command.argument.Constraint;
import us.nebula.api.manager.command.exception.ArgumentResolveException;

import java.util.LinkedList;
import java.util.List;

/**
 * @author xgraza
 * @since 03/20/25
 */
public final class ItemArgument extends Argument<Item>
{
    @SafeVarargs
    public ItemArgument(final String name, final Constraint<Item>... constraints)
    {
        super(Item.class, name, constraints);
    }

    @Override
    public void resolve(String raw) throws ArgumentResolveException
    {
        raw = raw.trim().toLowerCase().replaceAll(" ", "_");

        Item resolvedItem;
        Integer itemId;
        if ((itemId = isDigit(raw)) != null)
        {
            resolvedItem = Item.getItemById(itemId);
        } else
        {
            resolvedItem = (Item)Item.itemRegistry.getObject(raw);
        }
        if (resolvedItem == null)
        {
            throw new ArgumentResolveException(this, raw);
        }
        setValue(resolvedItem);
    }

    @Override
    public List<String> computeSuggestions(String input)
    {
        if (isDigit(input) != null)
        {
            return super.computeSuggestions(input);
        }
        if (input.startsWith("minecraft:"))
        {
            input = input.substring("minecraft:".length());
        }
        input = input.trim().toLowerCase();
        final List<String> suggestionList = new LinkedList<>();
        for (final String itemName : Item.itemRegistry.objectNameMap.values())
        {
            if (itemName.toLowerCase().contains(input))
            {
                suggestionList.add(itemName);
            }
        }
        return suggestionList;
    }

    private Integer isDigit(final String raw)
    {
        try
        {
            return Integer.parseInt(raw);
        } catch (NumberFormatException ignored)
        {
            return null;
        }
    }
}
