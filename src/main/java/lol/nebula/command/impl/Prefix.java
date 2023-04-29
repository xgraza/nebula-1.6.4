package lol.nebula.command.impl;

import lol.nebula.Nebula;
import lol.nebula.command.Command;

/**
 * @author aesthetical
 * @since 04/28/23
 */
public class Prefix extends Command {
    public Prefix() {
        super(new String[]{"prefix", "setprefix", "pfx"}, "Sets the command prefix", "<prefix>");
    }

    @Override
    public String dispatch(String[] args) {
        if (args.length == 0) return getSyntax();
        Nebula.getInstance().getCommands().setCommandPrefix(String.valueOf(args[0].charAt(0)), true);
        return "Set prefix to \"" + args[0].charAt(0) + "\"";
    }
}
