package ez.nebula.client.impl.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import ez.nebula.client.api.manager.command.Command;
import ez.nebula.client.api.manager.command.arg.ModuleArgumentType;
import ez.nebula.client.api.manager.command.trait.CommandManifest;
import ez.nebula.client.api.manager.command.trait.CommandSource;
import ez.nebula.client.api.manager.module.Module;
import net.minecraft.util.EnumChatFormatting;

@CommandManifest(aliases = { "toggle", "t", "settoggled" })
public final class ToggleCommand extends Command
{
    @Override
    public void createBuilder(final LiteralArgumentBuilder<CommandSource> literal)
    {
        literal.then(argument("module", ModuleArgumentType.module())
                .executes((ctx) ->
                {
                    final Module module = ModuleArgumentType.get(ctx, "module");
                    module.toggle();
                    boolean toggled = module.isToggled();
                    return ctx.getSource().respond("Toggled %s %s%s",
                            module.getManifest().name(),
                            toggled
                                    ? EnumChatFormatting.GREEN
                                    : EnumChatFormatting.RED,
                            toggled
                                    ? "on"
                                    : "off");
                }));
    }
}
