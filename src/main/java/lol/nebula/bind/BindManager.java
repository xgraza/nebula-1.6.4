package lol.nebula.bind;

import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.input.EventKeyInput;
import lol.nebula.listener.events.input.EventMouseInput;

import java.util.*;

import static org.lwjgl.input.Keyboard.KEY_NONE;

/**
 * @author aesthetical
 * @since 04/27/23
 */
public class BindManager {

    /**
     * A map of all the registered {@link Bind}s in the client with their tags
     */
    private final Map<String, Bind> bindMap = new LinkedHashMap<>();

    public BindManager() {
        new BindConfig(this);
    }

    @Listener
    public void onKeyInput(EventKeyInput event) {
        // if the key is not known, do not try to handle it
        if (event.getKeyCode() <= KEY_NONE) return;

        for (Bind bind : bindMap.values()) {

            // if the key pressed equals the bind key and this bind is a keyboard bind, toggle the bind
            if (bind.getKey() == event.getKeyCode() && bind.getDevice() == BindDevice.KEYBOARD) {
                bind.setState(!bind.isToggled());
            }
        }
    }

    @Listener
    public void onMouseInput(EventMouseInput event) {
        for (Bind bind : bindMap.values()) {

            if (bind.getKey() == event.getButton() && bind.getDevice() == BindDevice.MOUSE) {
                bind.setState(!bind.isToggled());
            }
        }
    }

    /**
     * Adds a {@link Bind} to the bind manager
     * @param bind the {@link Bind} object
     */
    public void addBind(Bind bind) {
        bindMap.put(bind.getTag(), bind);
    }

    /**
     * Gets a bind by its tag
     * @param tag the tag of the bind
     * @return the bind object or null
     */
    public Bind getBind(String tag) {
        return bindMap.getOrDefault(tag, null);
    }

    /**
     * Gets the current list of binds
     * @return the bind list
     */
    public Collection<Bind> getBindList() {
        return bindMap.values();
    }
}
