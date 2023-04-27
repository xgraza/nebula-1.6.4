package lol.nebula.bind;

import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.input.EventKeyInput;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.input.Keyboard.KEY_NONE;

/**
 * @author aesthetical
 * @since 04/27/23
 */
public class BindManager {

    /**
     * A list of all the registered {@link Bind}s in the client
     */
    private final List<Bind> bindList = new ArrayList<>();

    @Listener
    public void onKeyInput(EventKeyInput event) {
        // if the key is not known, do not try to handle it
        if (event.getKeyCode() == KEY_NONE) return;

        for (Bind bind : bindList) {

            // if the key pressed equals the bind key and this bind is a keyboard bind, toggle the bind
            if (bind.getKey() == event.getKeyCode() && bind.getDevice() == BindDevice.KEYBOARD) {
                bind.setState(!bind.isToggled());
            }
        }
    }

    /**
     * Adds a {@link Bind} to the bind manager
     * @param bind the {@link Bind} object
     */
    public void addBind(Bind bind) {
        bindList.add(bind);
    }
}
