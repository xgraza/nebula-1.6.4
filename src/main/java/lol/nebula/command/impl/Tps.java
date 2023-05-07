package lol.nebula.command.impl;

import lol.nebula.Nebula;
import lol.nebula.command.Command;

import java.util.StringJoiner;

/**
 * @author aesthetical
 * @since 05/07/23
 */
public class Tps extends Command {
    public Tps() {
        super(new String[]{"tps", "tickspersecond"}, "Views the server TPS", "<-verbose>");
    }

    @Override
    public String dispatch(String[] args) {
        String text = "Server TPS is " + String.format("%.1f", Nebula.getInstance().getTick().getTps());
        if (args.length == 0 || !args[0].equalsIgnoreCase("-verbose")) return text;

        StringJoiner tickJoined = new StringJoiner(", ");
        for (double x : Nebula.getInstance().getTick().getTpsMap()) {
            if (x > 0.0) {
                tickJoined.add(String.format("%.1f", x));
            }
        }

        return text + ", [" + tickJoined + "]";
    }
}
