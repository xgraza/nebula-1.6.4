package lol.nebula.listener.events.input;

/**
 * @author aesthetical
 * @since 05/01/23
 */
public class EventMouseInput {
    private final int button;

    public EventMouseInput(int button) {
        this.button = button;
    }

    public int getButton() {
        return button;
    }
}
