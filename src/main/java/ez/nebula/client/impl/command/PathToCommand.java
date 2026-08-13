package ez.nebula.client.impl.command;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import ez.nebula.client.api.manager.command.Command;
import ez.nebula.client.api.manager.command.trait.CommandManifest;
import ez.nebula.client.api.manager.command.trait.CommandSource;
import ez.nebula.client.impl.module.movement.PathFinderModule;
import net.minecraft.src.BlockPos;

/**
 * @author xgraza
 * @since 8/7/26
 */
@CommandManifest(aliases = {"pathto", "path"}, description = "Paths to a set of coordinates")
public final class PathToCommand extends Command
{
    @Override
    public void createBuilder(LiteralArgumentBuilder<CommandSource> literal)
    {
        literal.then(argument("x", DoubleArgumentType.doubleArg())
                .then(argument("y", DoubleArgumentType.doubleArg())
                        .then(argument("z", DoubleArgumentType.doubleArg())
                                .executes((ctx) ->
                                {
                                    final double x = DoubleArgumentType.getDouble(ctx, "x");
                                    final double y = DoubleArgumentType.getDouble(ctx, "y");
                                    final double z = DoubleArgumentType.getDouble(ctx, "z");
                                    final BlockPos pos = new BlockPos((int) x, (int) y, (int) z);
                                    // PathFinderModule.INSTANCE.reset();
                                    // PathFinderModule.INSTANCE.setGoal(pos);
                                    if (!PathFinderModule.INSTANCE.isToggled())
                                    {
                                        PathFinderModule.INSTANCE.setToggled(true);
                                    }
                                    return ctx.getSource().respond("Set goal pos to %s", pos);
                                }))));
    }
}
