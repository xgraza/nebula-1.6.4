package wtf.nebula.client.impl.command.impl;

import wtf.nebula.client.core.Nebula;
import wtf.nebula.client.impl.command.Command;
import wtf.nebula.client.impl.command.arg.CommandContext;
import wtf.nebula.client.impl.command.arg.impl.StringArgument;
import wtf.nebula.client.impl.module.ModuleManager;

public class Configs extends Command {
    public Configs() {
        super(new String[]{"config", "configs", "cfg", "preset"},
                new StringArgument("action"),
                new StringArgument("configName").setRequired(false));
    }

    @Override
    public String dispatch(CommandContext ctx) {
        String action = (String) ctx.get("action").getValue();
        ModuleManager manager = Nebula.getInstance().getModuleManager();

        switch (action.toLowerCase()) {
            case "save":
            case "s": {
                String name = (String) ctx.get("configName").getValue();
                if (name != null) {
                    manager.saveConfig(name);
                    return "Saved config with name " + name;
                }
                break;
            }

            case "load":
            case "l": {
                String name = (String) ctx.get("configName").getValue();
                if (name != null) {
                    boolean s = manager.loadConfig(name);
                    return s
                            ? ("Loaded config with name \"" + name + "\".")
                            : ("Failed to load config with name \"" + name + "\".");
                }
                break;
            }

            case "del":
            case "remove":
            case "rm":
            case "delete": {
                String name = (String) ctx.get("configName").getValue();
                if (name != null) {
                    boolean s = manager.deleteConfig(name);
                    return s
                            ? ("Deleted config \"" + name + "\".")
                            : ("Failed to delete config with name \"" + name + "\".");
                }
                break;
            }

            default:
            case "ls":
            case "list": {
                if (manager.moduleConfigs.isEmpty()) {
                    return "There are no configs.";
                }

                return "Configs (" + manager.moduleConfigs.size() + "): " + String.join(", ", manager.moduleConfigs.keySet()) + ".";
            }
        }

        return "lol";
    }
}
