package ez.nebula.client.impl.config;

import com.google.gson.JsonObject;
import ez.nebula.client.ClientConfig;
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
        object.addProperty("useCustomSplashText", ClientConfig.USE_CUSTOM_SPLASH_TEXT);
        object.addProperty("openedGuiBefore", ClientConfig.OPENED_GUI_BEFORE);
        object.addProperty("debug", ClientConfig.DEBUG);
        return object;
    }

    @Override
    public void readJSON(JsonObject json)
    {
        ClientConfig.DEBUG = json.has("debug")
                && json.get("debug").getAsBoolean();
        ClientConfig.USE_CUSTOM_SPLASH_TEXT = json.has("useCustomSplashText")
                && json.get("useCustomSplashText").getAsBoolean();
        ClientConfig.OPENED_GUI_BEFORE = json.has("openedGuiBefore")
                && json.get("openedGuiBefore").getAsBoolean();
    }

    @Override public File getLocation()
    {
        return new File(Nebula.NEBULA_ROOT, "settings.json");
    }
}
