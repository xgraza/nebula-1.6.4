package wtf.nebula.client.impl.command.impl;

import net.minecraft.util.Vec3;
import wtf.nebula.client.core.Nebula;
import wtf.nebula.client.impl.command.Command;
import wtf.nebula.client.impl.command.arg.CommandContext;
import wtf.nebula.client.impl.command.arg.impl.StringArgument;
import wtf.nebula.client.impl.waypoint.Waypoint;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Waypoints extends Command {
    public Waypoints() {
        super(new String[]{"waypoints", "wps"},
                new StringArgument("action"),
                new StringArgument("name").setRequired(false));
    }

    @Override
    public String dispatch(CommandContext ctx) {

        String ip = null;
        if (mc.currentServerData != null && mc.currentServerData.serverIP != null) {
            ip = mc.currentServerData.serverIP;
        }

        if (ip == null || ip.isEmpty()) {
            return "No server data.";
        }

        String action = (String) ctx.get("action").getValue();
        List<Waypoint> waypoints = Nebula.getInstance().getWaypointManager().get(ip);

        switch (action.toLowerCase()) {
            case "list":
            case "ls":
            default: {
                if (waypoints.isEmpty()) {
                    return "You have no waypoints for " + ip + ".";
                }

                return "You have "
                        + waypoints.size()
                        + " waypoint"
                        + (waypoints.size() > 1 ? "s" : "")
                        + ": "
                        + waypoints.stream()
                            .map(Waypoint::getName)
                            .collect(Collectors.joining(", "))
                        + ".";
            }

            case "a":
            case "+":
            case "new":
            case "add": {
                String name = (String) ctx.get("name").getValue();
                if (name == null || name.isEmpty()) {
                    return "You need a waypoint name.";
                }

                for (Waypoint waypoint : waypoints) {
                    if (waypoint.getName().equals(name)) {
                        return "You already have a waypoint named \"" + name + "\".";
                    }
                }

                List<String> args = ctx.getRawArgs();
                args = args.subList(2, args.size());

                double x = -1.0, y = -1.0, z = -1.0;
                int dimension = mc.theWorld.provider.dimensionId;

                if (args.isEmpty()) {
                    x = mc.thePlayer.posX;
                    y = mc.thePlayer.posY;
                    z = mc.thePlayer.posZ;
                } else {

                    try {
                        x = Double.parseDouble(args.get(0));
                        y = Double.parseDouble(args.get(1));
                        z = Double.parseDouble(args.get(2));
                        dimension = Integer.parseInt(args.get(3));
                    } catch (IndexOutOfBoundsException ignored) {

                    }
                }

                if (x == -1.0 || y == -1.0 || z == -1.0) {
                    return "Please provide a position.";
                }

                Nebula.getInstance().getWaypointManager().add(
                        ip,
                        new Waypoint(new Vec3(Vec3.fakePool, x, y, z), name, dimension));

                return "Added a waypoint with the name \"" + name + "\".";

            }

            case "remove":
            case "rm":
            case "del":
            case "delete":
            case "r":
            case "d":
            case "-": {

                String name = (String) ctx.get("name").getValue();
                if (name == null || name.isEmpty()) {
                    return "You need a waypoint name.";
                }

                Waypoint wp = null;
                for (Waypoint waypoint : waypoints) {
                    if (waypoint.getName().equals(name)) {
                        wp = waypoint;
                        break;
                    }
                }

                if (wp == null) {
                    return "No waypoint saved with the name \"" + name + "\".";
                }

                return Nebula.getInstance().getWaypointManager().remove(ip, name)
                        ? "Removed the waypoint with the name \"" + name + "\"."
                        : "No waypoint was removed.";
            }
        }

    }
}
