package wtf.nebula.impl.command;

import wtf.nebula.util.feature.ToggleableFeature;

import java.util.List;

public abstract class Command extends ToggleableFeature {
    private final List<String> triggers;
    private final String description;

    public Command(List<String> triggers, String description) {
        super(triggers.get(0));

        this.triggers = triggers;
        this.description = description;
    }

    /**
     * Executes this command. Meant to be overridden
     * @param args the command arguments input via chat
     */
    public abstract void execute(List<String> args);

    public List<String> getTriggers() {
        return triggers;
    }

    public String getDescription() {
        return description;
    }
}
