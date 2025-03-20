package us.nebula.api.manager.command.argument.type;

import net.minecraft.item.Item;
import us.nebula.api.manager.command.argument.Argument;
import us.nebula.api.manager.command.argument.Constraint;
import us.nebula.api.manager.command.exception.ArgumentResolveException;

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
