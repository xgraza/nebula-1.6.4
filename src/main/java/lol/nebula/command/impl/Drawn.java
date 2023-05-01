package lol.nebula.command.impl;

import lol.nebula.Nebula;
import lol.nebula.command.Command;
import lol.nebula.module.Module;

/**
 * @author aesthetical
 * @since 05/01/23
 */
public class Drawn extends Command {
    public Drawn() {
        super(new String[]{"drawn"}, "Sets if the module provided should be drawn on the array list or not", "<module>");
    }

    @Override
    public String dispatch(String[] args) {
        if (args.length == 0) return getSyntax();

        Module module = Nebula.getInstance().getModules().get(String.join(" ", args));
        if (module == null) return "Could not find module with that name";
        module.setDrawn(!module.isDrawn());
        return module.getTag() + (module.isDrawn() ? " is now drawn" : " is now not drawn");
    }
}
