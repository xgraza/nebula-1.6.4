package ez.nebula.client.api.manager.key;

import ez.nebula.client.Nebula;
import ez.nebula.client.impl.config.KeyConfig;
import net.minecraft.client.Minecraft;
import ez.nebula.client.api.listener.EventBus;
import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.manager.ITypedManager;
import ez.nebula.client.api.listener.event.input.EventKey;
import ez.nebula.client.api.listener.event.input.EventMouse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.input.Keyboard.KEY_NONE;

/**
 * @author xgraza
 * @since 02/14/25
 */
public final class KeyManager implements ITypedManager<Key>
{
    private static final Minecraft MC = Minecraft.getMinecraft();

    private final Map<String, Key> keyIdMap = new HashMap<>();
    private final List<Key> keyList = new ArrayList<>();

    @Subscribe
    private final EventListener<EventKey> keyEventListener = event ->
    {
        if (event.getKeyCode() == KEY_NONE || MC.currentScreen != null)
        {
            return;
        }
        for (final Key key : keyList)
        {
            if (key.getKeyCode() == event.getKeyCode() && !key.isMouseBind())
            {
                key.toggle();
            }
        }
    };

    @Subscribe
    private final EventListener<EventMouse> mouseEventListener = event ->
    {
        if (event.getMouseButton() == -1 || MC.currentScreen != null)
        {
            return;
        }
        for (final Key key : keyList)
        {
            if (key.getKeyCode() == event.getMouseButton() && key.isMouseBind())
            {
                key.toggle();
            }
        }
    };

    @Override
    public void init()
    {
        EventBus.subscribe(this);
        Nebula.CONFIGS.addConfiguration(new KeyConfig(this));
    }

    public void addKey(final String id, final Key key)
    {
        keyIdMap.put(id, key);
        keyList.add(key);
    }

    public void addRuntimeKey(final Key key)
    {
        keyList.add(key);
    }

    public Key getReference(final String id)
    {
        return keyIdMap.get(id);
    }

    @Override
    public List<Key> getAll()
    {
        return keyList;
    }

    public Map<String, Key> getKeyIdMap()
    {
        return keyIdMap;
    }
}
