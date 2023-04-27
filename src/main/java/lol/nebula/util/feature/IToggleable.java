package lol.nebula.util.feature;

/**
 * @author aesthetical
 * @since 04/27/23
 */
public interface IToggleable {

    /**
     * Called when this object is "enabled"
     */
    void onEnable();

    /**
     * Called when this object is "disabled"
     */
    void onDisable();

    /**
     * If this object is toggled or not
     * @return the boolean state value
     */
    boolean isToggled();

    /**
     * Sets the state of this object
     * @param state the new value
     */
    void setState(boolean state);
}
