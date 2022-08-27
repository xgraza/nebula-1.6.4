package wtf.nebula.client.impl.module;

import org.lwjgl.input.Keyboard;
import wtf.nebula.client.core.Launcher;

public class ToggleableModule extends Module {

    private boolean running = false;
    private int bind = Keyboard.KEY_NONE;

    public ToggleableModule(String label, String[] aliases, ModuleCategory category) {
        super(label, aliases, category);
    }

    protected void onEnable() {

    }

    protected void onDisable() {

    }

    public void setRunning(boolean running) {
        this.running = running;

        if (running) {
            onEnable();
            Launcher.BUS.subscribe(this);
        } else {
            onDisable();
            Launcher.BUS.unsubscribe(this);
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void setBind(int bind) {
        this.bind = bind;
    }

    public int getBind() {
        return bind;
    }
}
