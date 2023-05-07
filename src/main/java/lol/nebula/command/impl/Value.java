package lol.nebula.command.impl;

import lol.nebula.Nebula;
import lol.nebula.bind.Bind;
import lol.nebula.command.Command;
import lol.nebula.module.Module;
import lol.nebula.setting.Setting;

import java.util.StringJoiner;

/**
 * @author aesthetical
 * @since 05/07/23
 */
public class Value extends Command {
    public Value() {
        super(new String[]{"value", "setvalue", "setting"}, "Sets a setting value for a module", "<module> <setting> [value]");
    }

    @Override
    public String dispatch(String[] args) {
        if (args.length < 2) return getSyntax();

        Module module = null;
        for (Module m : Nebula.getInstance().getModules().getModules()) {
            if (stripWhitespaces(m.getTag()).equalsIgnoreCase(args[0])) {
                module = m;
                break;
            }
        }

        if (module == null) return "Invalid module name";

        Setting<?> setting = null;
        for (Setting<?> s : module.getSettings()) {
            if (stripWhitespaces(s.getTag()).equalsIgnoreCase(args[1])) {
                setting = s;
                break;
            }
        }

        if (setting == null) return "Invalid setting name";

        if (args.length == 2) {
            String toString = setting.getValue().toString();
            if (setting.getValue() instanceof Enum<?>) {
                toString = Setting.formatEnumName((Enum<?>) setting.getValue());
            }

            String options = "None";
            if (setting.getValue() instanceof Boolean) {
                options = "\"true\" or \"false\"";
            } else if (setting.getValue() instanceof Number) {
                options = "Min: " + setting.getMin().toString() + ", Max: " + setting.getMax().toString();
            } else if (setting.getValue() instanceof Enum<?>) {
                StringJoiner joiner = new StringJoiner(", ");
                for (Object obj : ((Enum<?>) setting.getValue()).getDeclaringClass().getEnumConstants()) {
                    if (obj instanceof Enum<?>) {
                        joiner.add(Setting.formatEnumName(((Enum<?>) obj)));
                    }
                }

                options = joiner.toString();
            } else if (setting.getValue() instanceof Bind) {
                options = "<keybind> [device type]";
            }

            return setting.getTag() + ": " + toString + ", Options: " + options;
        } else {

            // TODO

        }

        return "hi";
    }

    private String stripWhitespaces(String t) {
        return t.trim().replaceAll("\\s*", "");
    }
}
