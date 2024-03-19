package yzy.szn.api.module;

import yzy.szn.launcher.YZY;

import static org.lwjgl.input.Keyboard.KEY_NONE;

/**
 * @author graza
 * @since 02/17/24
 */
public class Module {

    private final ModuleManifest manifest;
    private int keyCode = KEY_NONE;
    private boolean toggled;

    public Module() {
        final ModuleManifest moduleManifest = getClass().getDeclaredAnnotation(ModuleManifest.class);
        if (moduleManifest == null) {
            throw new RuntimeException("@ModuleManifest required at top of " + getClass().getName());
        }

        manifest = moduleManifest;
    }

    protected void onEnable() {
        YZY.BUS.subscribe(this);
    }

    protected void onDisable() {
        YZY.BUS.unsubscribe(this);
    }

    public ModuleManifest getManifest() {
        return manifest;
    }

    public int getKeyCode() {
        return keyCode;
    }

    public void setKeyCode(int keyCode) {
        this.keyCode = keyCode;
    }

    public void toggle() {
        setToggled(!toggled);
    }

    public void setToggled(boolean toggled) {
        if (this.toggled != toggled) {
            this.toggled = toggled;

            if (toggled) {
                onEnable();
            } else {
                onDisable();
            }
        }
    }

    public boolean isToggled() {
        return toggled;
    }
}
