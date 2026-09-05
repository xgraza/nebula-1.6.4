package ez.nebula.client.impl.command;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import ez.nebula.client.api.manager.command.Command;
import ez.nebula.client.api.manager.command.trait.CommandManifest;
import ez.nebula.client.api.manager.command.trait.CommandSource;
import ez.nebula.client.api.manager.waypoint.Waypoint;
import ez.nebula.client.Nebula;
import ez.nebula.client.util.minecraft.network.PacketUtil;
import net.minecraft.network.play.client.C01PacketChatMessage;

/**
 * @author xgraza
 * @since 6/12/26
 */
@CommandManifest(aliases = {"waypoint", "wp"}, description = "Manages waypoints")
public final class WaypointCommand extends Command
{
    @Override
    public void createBuilder(final LiteralArgumentBuilder<CommandSource> literal)
    {
        // holy balls
        literal.then(literal("view")
                .then(argument("name", StringArgumentType.greedyString())
                        .executes((ctx) ->
                        {
                            final String serverIP = getServerIP();
                            final String name = StringArgumentType.getString(ctx, "name");
                            final Waypoint waypoint = Nebula.INSTANCE.getWaypointManager().getWaypoint(serverIP, name);
                            if (waypoint == null)
                            {
                                return ctx.getSource().respond("No waypoint found with that name");
                            }
                            return ctx.getSource().respond("&z%s&r is at XYZ: &z%.1f, %.1f, %.1f",
                                    waypoint.getName(), waypoint.getX(), waypoint.getY(), waypoint.getZ());
                        })))
                .then(literal("add")
                        .then(argument("name", StringArgumentType.string())
                                .executes((ctx) ->
                                {
                                    final String serverIP = getServerIP();
                                    final String name = StringArgumentType.getString(ctx, "name");
                                    if (name.isEmpty())
                                    {
                                        return ctx.getSource().respond("Name cannot be empty");
                                    }
                                    if (Nebula.INSTANCE.getWaypointManager().waypointExists(serverIP, name))
                                    {
                                        return ctx.getSource().respond("A waypoint with that name already exists!");
                                    }
                                    final Waypoint waypoint = new Waypoint(serverIP, name, MC.thePlayer.posX, MC.thePlayer.posY, MC.thePlayer.posZ, MC.thePlayer.dimension);
                                    Nebula.INSTANCE.getWaypointManager().registerWaypoint(waypoint);
                                    return ctx.getSource().respond("Created a new waypoint with the name &z%s&r at your location", name);
                                }))
                        .then(argument("name", StringArgumentType.string())
                                .then(argument("x", DoubleArgumentType.doubleArg(-30000000, 30000000))
                                        .then(argument("y", DoubleArgumentType.doubleArg(-30000000, 30000000))
                                                .then(argument("z", DoubleArgumentType.doubleArg(-30000000, 30000000))
                                                        .executes((ctx) ->
                                                        {
                                                            final String serverIP = getServerIP();
                                                            final String name = StringArgumentType.getString(ctx, "name");
                                                            if (name.isEmpty())
                                                            {
                                                                return ctx.getSource().respond("Name cannot be empty");
                                                            }
                                                            if (Nebula.INSTANCE.getWaypointManager().waypointExists(serverIP, name))
                                                            {
                                                                return ctx.getSource().respond("A waypoint with that name already exists!");
                                                            }
                                                            final double x = DoubleArgumentType.getDouble(ctx, "x");
                                                            final double y = DoubleArgumentType.getDouble(ctx, "y");
                                                            final double z = DoubleArgumentType.getDouble(ctx, "z");
                                                            final Waypoint waypoint = new Waypoint(serverIP, name, x, y, z, MC.thePlayer.dimension);
                                                            Nebula.INSTANCE.getWaypointManager().registerWaypoint(waypoint);
                                                            return ctx.getSource().respond("Created a new waypoint &z%s&r at XYZ: &z%.1f, %.1f, %.1f",
                                                                    name, x, y, z);
                                                        }))))))
                .then(literal("remove")
                        .then(argument("name", StringArgumentType.greedyString())
                                .executes((ctx) ->
                                {
                                    final String serverIP = getServerIP();
                                    final String name = StringArgumentType.getString(ctx, "name");
                                    if (!Nebula.INSTANCE.getWaypointManager().waypointExists(serverIP, name))
                                    {
                                        return ctx.getSource().respond("No waypoint with that name found");
                                    }
                                    Nebula.INSTANCE.getWaypointManager().unregisterWaypoint(serverIP, name);
                                    return ctx.getSource().respond("Removed a waypoint with the name &z%s", name);
                                })))
                .then(literal("tp")
                        .then(argument("name", StringArgumentType.greedyString())
                                .executes((ctx) ->
                                {
                                    final String serverIP = getServerIP();
                                    final String name = StringArgumentType.getString(ctx, "name");
                                    final Waypoint waypoint = Nebula.INSTANCE.getWaypointManager().getWaypoint(serverIP, name);
                                    if (waypoint == null)
                                    {
                                        return ctx.getSource().respond("No waypoint found with that name");
                                    }
                                    final double x = waypoint.getX();
                                    final double y = waypoint.getY();
                                    final double z = waypoint.getZ();
                                    PacketUtil.send(new C01PacketChatMessage(String.format("/tp @p %.1f %.1f %.1f", x, y, z)));
                                    return ctx.getSource().respond("Sent /tp command to XYZ: &z%.1f, %.1f, %.1f", x, y, z);
                                })));
    }

    private String getServerIP()
    {
        return Nebula.INSTANCE.getServerManager().getServerIP();
    }
}
