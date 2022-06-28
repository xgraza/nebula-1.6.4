package wtf.nebula.impl.module;

import org.lwjgl.input.Keyboard;
import wtf.nebula.impl.module.misc.Notifications;
import wtf.nebula.impl.value.Bind;
import wtf.nebula.impl.value.Value;
import wtf.nebula.impl.value.ValueContainer;
import wtf.nebula.repository.impl.ModuleRepository;

public class Module extends ValueContainer {
    private final ModuleCategory category;

    protected final Bind bind = register(new Bind("Keybind", Keyboard.KEY_NONE));
    public final Value<Boolean> drawn = register(new Value<>("Drawn", true));

    public Module(String name, ModuleCategory category) {
        super(name);
        this.category = category;
    }

    public ModuleCategory getCategory() {
        return category;
    }

    public void setBind(int in) {
        bind.setValue(in);
    }

    public int getBind() {
        return bind.getValue();
    }

    @Override
    public void setState(boolean state) {
        if (state == getState()) {
            return;
        }

        if (!nullCheck()) {
            Notifications notifications = ModuleRepository.get().getModule(Notifications.class);
            if (notifications.getState()) {
                notifications.toggleMessage(this);
            }
        }

        super.setState(state);
    }
}
