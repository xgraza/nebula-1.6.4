package ez.nebula.client.api.manager.module;

import ez.nebula.client.api.setting.Setting;

import java.util.HashMap;
import java.util.Map;

public class ModuleWithModes<T extends Enum<T>> extends Module
{
    private final Setting<T> modeSetting;

    private final Map<T, ModuleMode<Module>> moduleModeMap = new HashMap<>();
    private ModuleMode<Module> currentMode;

    public ModuleWithModes(final T defaultValue)
    {
        modeSetting = enumBuilder("Mode", defaultValue)
                .setDescription("The mode for this module")
                .onValueChanged((value) ->
                {
                    if (currentMode != null)
                    {
                        currentMode.onDisable();
                        currentMode = null;
                    }
                    final ModuleMode<Module> mode = moduleModeMap.get(value);
                    if (mode == null)
                    {
                        return;
                    }
                    currentMode = mode;
                    currentMode.onEnable();
                })
                .build();
    }

    public void registerMode(final T modeEnum, final ModuleMode<Module> mode)
    {
        moduleModeMap.put(modeEnum, mode);
    }

    @Override
    public void onEnable()
    {
        super.onEnable();
        if (currentMode != null)
        {
            currentMode.onEnable();
        }
    }

    @Override
    public void onDisable()
    {
        super.onDisable();
        if (currentMode != null)
        {
            currentMode.onDisable();
        }
    }

    public T getCurrentMode()
    {
        return modeSetting.getValue();
    }
}
