package us.nebula.api.manager.key;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.lwjgl.input.Keyboard;
import us.nebula.api.config.IJSONSerializable;

/**
 * @author xgraza
 * @since 02/14/24
 */
public final class Key implements IJSONSerializable
{
    public static int DEFAULT_UNBOUND_KEY = -1;

    private int keyCode = DEFAULT_UNBOUND_KEY;
    private boolean useMouse, state;
    private final KeyAction action;

    public Key(final KeyAction action, final boolean useMouse, final int keyCode)
    {
        this.action = action;
        this.useMouse = useMouse;
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

    public boolean isUseMouse()
    {
        return useMouse;
    }

    public void setUseMouse(final boolean useMouse)
    {
        this.useMouse = useMouse;
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
        object.addProperty("useMouse", useMouse);
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
        useMouse = object.get("useMouse").getAsBoolean();
    }

    @Override
    public String toString()
    {
        if (useMouse)
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
        return Keyboard.getKeyName(keyCode);
    }
}
