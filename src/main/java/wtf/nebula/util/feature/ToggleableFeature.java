package wtf.nebula.util.feature;

import wtf.nebula.Nebula;

/**
 * A feature that can be toggled
 *
 * @author aesthetical
 * @since 06/27/22
 */
public class ToggleableFeature extends Feature {

    // this feature's state
    protected boolean state = false;

    public ToggleableFeature(String name) {
        super(name);
    }

    /**
     * Called when this feature is activated
     */
    protected void onActivated() {

    }

    /**
     * Called when this feature is deactivated
     */
    protected void onDeactivated() {

    }

    public void setState(boolean state) {
        this.state = state;

        if (state) {
            Nebula.BUS.subscribe(this);
            onActivated();
        }

        else {
            Nebula.BUS.unsubscribe(this);
            onDeactivated();
        }
    }

    public boolean getState() {
        return state;
    }
}
