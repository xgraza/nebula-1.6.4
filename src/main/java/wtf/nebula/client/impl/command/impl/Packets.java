package wtf.nebula.client.impl.command.impl;

import wtf.nebula.client.impl.command.Command;
import wtf.nebula.client.impl.command.arg.CommandContext;
import wtf.nebula.client.impl.command.arg.impl.StringArgument;
import wtf.nebula.client.impl.module.exploits.PacketCancel;

public class Packets extends Command {
    public Packets() {
        super(new String[]{"packets", "packetcanceller", "pkcanceller"}, new StringArgument("className"));
    }

    @Override
    public String dispatch(CommandContext ctx) {
        Class<?> clazz = null;

        String name = (String) ctx.get("className").getValue();
        name = name.trim().replaceAll("\\s*", "").replaceAll("\\.", "$");
        if (name.isEmpty()) {
            return "Invalid name";
        }

        try {
            clazz = Class.forName("net.minecraft.network.play.client." + name);
        } catch (Exception ignored) {
        }

        try {
            clazz = Class.forName("net.minecraft.network.play.server." + name);
        } catch (Exception ignored) {
        }

        if (clazz == null) {
            return "Could not find referenced class.";
        }

        boolean r = true;
        if (PacketCancel.CANCELABLE.contains(clazz)) {
            PacketCancel.CANCELABLE.remove(clazz);
        } else {
            r = false;
            PacketCancel.CANCELABLE.add(clazz);
        }

        return (r ? "Removed" : "Added") + " the packet by the name " + clazz.getSimpleName() + ".";
    }
}
