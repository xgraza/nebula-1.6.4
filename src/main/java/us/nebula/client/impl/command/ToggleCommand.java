package us.nebula.client.impl.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.util.EnumChatFormatting;
import us.nebula.client.api.manager.cheat.Cheat;
import us.nebula.client.api.manager.command.Command;
import us.nebula.client.api.manager.command.CommandManifest;
import us.nebula.client.api.manager.command.CommandSource;
import us.nebula.client.api.manager.command.arg.CheatArgument;

@CommandManifest(aliases = {"toggle", "t", "settoggled"})
public final class ToggleCommand extends Command
{
    @Override
    public void createBuilder(final LiteralArgumentBuilder<CommandSource> literal)
    {
        literal.then(argument("cheat", CheatArgument.cheat())
                .executes((ctx) -> {
                    final Cheat cheat = CheatArgument.get(ctx, "cheat");
                    cheat.toggle();
                    boolean toggled = cheat.isToggled();
                    return ctx.getSource().respond("Toggled %s %s%s",
                            cheat.getManifest().name(),
                            toggled
                                    ? EnumChatFormatting.GREEN
                                    : EnumChatFormatting.RED,
                            toggled
                                    ? "on"
                                    : "off");
                }));
    }
}
