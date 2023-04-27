package lol.nebula.listener.events.input;

/**
 * @author aesthetical
 * @since 04/27/23
 */
public class EventKeyInput {

    /**
     * The key code from this event
     */
    private final int keyCode;

    /**
     * Creates a key input event
     * @param keyCode the key code pressed
     */
    public EventKeyInput(int keyCode) {
        this.keyCode = keyCode;
    }

    /**
     * Gets the key code used
     * @return the key code used or KEY_UNKNOWN
     */
    public int getKeyCode() {
        return keyCode;
    }
}
