package ez.nebula.client.impl.config;

import com.google.gson.JsonObject;
import ez.nebula.client.Nebula;
import ez.nebula.client.api.config.type.JSONConfig;

import java.io.File;

/**
 * @author xgraza
 * @since 3/14/26
 */
public final class ClientSettingConfig extends JSONConfig<JsonObject>
{
    @Override
    public JsonObject writeJSON()
    {
        final JsonObject object = new JsonObject();
        object.addProperty("useCustomSplashText", Nebula.USE_CUSTOM_SPLASH_TEXT);
        object.addProperty("openedGuiBefore", Nebula.OPENED_GUI_BEFORE);
        object.addProperty("debug", Nebula.DEBUG);
        return object;
    }

    @Override
    public void readJSON(JsonObject json)
    {
        Nebula.DEBUG = json.has("debug")
                && json.get("debug").getAsBoolean();
        Nebula.USE_CUSTOM_SPLASH_TEXT = json.has("useCustomSplashText")
                && json.get("useCustomSplashText").getAsBoolean();
        Nebula.OPENED_GUI_BEFORE = json.has("openedGuiBefore")
                && json.get("openedGuiBefore").getAsBoolean();
    }

    @Override public File getLocation()
    {
        return new File(Nebula.NEBULA_ROOT, "settings.json");
    }
}
