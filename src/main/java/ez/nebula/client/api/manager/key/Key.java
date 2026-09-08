package ez.nebula.client.api.manager.key;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ez.nebula.client.api.manager.key.trait.KeyAction;
import org.lwjgl.input.Keyboard;
import ez.nebula.client.util.io.IJSONSerializable;

import static org.lwjgl.input.Keyboard.KEY_NONE;

/**
 * @author xgraza
 * @since 02/14/24
 */
public final class Key implements IJSONSerializable
{
    public static final int DEFAULT_UNBOUND_KEY = -1;

    private int keyCode;
    private boolean mouseBind, state;
    private final KeyAction action;

    public Key(final KeyAction action, final boolean mouseBind, final int keyCode)
    {
        this.action = action;
        this.mouseBind = mouseBind;
        this.keyCode = keyCode;
    }

    public int getKeyCode()
    {
        return keyCode;
    }

    public void setKeyCode(final int keyCode)
    {
        this.keyCode = keyCode;
    }

    public boolean isUnbound()
    {
        return keyCode <= DEFAULT_UNBOUND_KEY;
    }

    public boolean isMouseBind()
    {
        return mouseBind;
    }

    public void setMouseBind(final boolean mouseBind)
    {
        this.mouseBind = mouseBind;
    }

    public void setState(final boolean state)
    {
        this.state = state;
        if (action != null)
        {
            action.inhibit(state);
        }
    }

    public void toggle()
    {
        setState(!state);
    }

    public boolean isToggled()
    {
        return state;
    }

    @Override
    public JsonElement toJSON()
    {
        final JsonObject object = new JsonObject();
        object.addProperty("keyCode", keyCode);
        object.addProperty("mouseBind", mouseBind);
        return object;
    }

    @Override
    public void fromJSON(final JsonElement element)
    {
        if (!element.isJsonObject())
        {
            throw new RuntimeException("element must be JsonObject");
        }
        final JsonObject object = element.getAsJsonObject();
        keyCode = object.get("keyCode").getAsInt();
        mouseBind = object.get("mouseBind").getAsBoolean();
    }

    @Override
    public String toString()
    {
        if (mouseBind)
        {
            switch (keyCode)
            {
                case 0:
                    return "M-LEFT";
                case 1:
                    return "M-RIGHT";
                case 2:
                    return "M-MIDDLE";
                case 3:
                    return "M-DOWN";
                case 4:
                    return "M-FORWARD";
                default:
                    return "MB-" + (keyCode + 1);
            }
        }
        if (keyCode <= KEY_NONE)
        {
            return "NONE";
        }
        return Keyboard.getKeyName(keyCode);
    }
}
