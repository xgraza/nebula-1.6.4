package ez.nebula.client.api.setting;

import com.google.gson.JsonElement;
import ez.nebula.client.api.manager.key.Key;
import ez.nebula.client.api.manager.key.trait.KeyAction;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author xgraza
 * @since 6/7/26
 */
public final class BindSetting extends Setting<Key>
{
    public BindSetting(String name, String description, Predicate<Key> visibility, Consumer<Key> valueChanged, Key value)
    {
        super(name, description, visibility, valueChanged, value);
    }

    @Override
    public JsonElement toJSON()
    {
        return getValue().toJSON();
    }

    @Override
    public void fromJSON(final JsonElement element)
    {
        getValue().fromJSON(element);
    }

    public static final class Builder extends Setting.Builder<Key>
    {
        private int keyCode = Key.DEFAULT_UNBOUND_KEY;
        private KeyAction action = (state) -> {};
        private boolean mouseBind = false;

        public Builder(String name)
        {
            super(name, null);
        }

        public Builder setMouseBind(boolean mouseBind)
        {
            this.mouseBind = mouseBind;
            return this;
        }

        public Builder setKeyCode(final int keyCode)
        {
            this.keyCode = keyCode;
            return this;
        }

        public Builder setAction(final KeyAction action)
        {
            this.action = action;
            return this;
        }

        @Override
        public BindSetting build()
        {
            return new BindSetting(name, description, visibility, valueChanged, new Key(action, mouseBind, keyCode));
        }
    }
}
