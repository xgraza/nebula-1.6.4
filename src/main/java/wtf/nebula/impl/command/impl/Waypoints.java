package wtf.nebula.impl.command.impl;

import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Vec3;
import wtf.nebula.impl.command.Command;
import wtf.nebula.repository.impl.WaypointRepository;
import wtf.nebula.repository.impl.waypoint.Waypoint;
import wtf.nebula.util.world.ServerUtil;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class Waypoints extends Command {
    public Waypoints() {
        super(Arrays.asList("waypoints", "waypoint", "wp", "point"), "Configures the client's waypoints");
    }

    @Override
    public void execute(List<String> args) {
        if (args.isEmpty()) {
            sendChatMessage("Please provide one of the following actions: add, remove, list");
            return;
        }

        String action = args.get(0).toLowerCase();
        args = args.subList(1, args.size());

        switch (action) {
            case "add":

                if (args.isEmpty()) {
                    sendChatMessage("Please provide a name for this waypoint");
                    return;
                }

                String name = args.get(0);
                int dimension = mc.theWorld.provider.dimensionId;

                try {
                    dimension = Integer.parseInt(args.get(1));
                } catch (IndexOutOfBoundsException ignored) {

                }

                double[] coords = { mc.thePlayer.posX, mc.thePlayer.boundingBox.minY, mc.thePlayer.posZ };
                for (int i = 0; i < 3; ++i) {

                    try {
                        coords[i] = Double.parseDouble(args.get(2 + i));
                    } catch (Exception ignored) {

                    }

                }

                WaypointRepository.get().addChild(new Waypoint(
                        ServerUtil.getServerName(), name,
                        Vec3.createVectorHelper(coords[0], coords[1], coords[2]),
                        dimension));

                sendChatMessage("Created a waypoint under the name " + EnumChatFormatting.GREEN + name);

                break;

            case "remove":

                if (args.isEmpty()) {
                    sendChatMessage("Please provide a waypoint name");
                    return;
                }

                if (WaypointRepository.get().removeChild(ServerUtil.getServerName(), args.get(0))) {
                    sendChatMessage("Removed waypoint by that name.");
                }

                else {
                    sendChatMessage("There was no waypoint with that name");
                }

                break;

            case "list":

                CopyOnWriteArrayList<Waypoint> waypoints = WaypointRepository.get()
                        .serverWaypoints
                        .getOrDefault(ServerUtil.getServerName(), null);

                if (waypoints == null || waypoints.isEmpty()) {
                    sendChatMessage("You do not have any waypoints set for this server");
                    return;
                }

                sendChatMessage("Waypoints (" + waypoints.size() + "): " +
                        waypoints.stream()
                                .map((wp) -> EnumChatFormatting.GREEN + wp.getName() + EnumChatFormatting.RESET)
                                .collect(Collectors.joining(", ")));

                break;

            default:
                sendChatMessage("Invalid operation.");
                break;
        }
    }
}
