package ez.nebula.client.impl.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ez.nebula.client.ClientConfig;
import ez.nebula.client.Nebula;
import ez.nebula.client.api.config.IConfig;
import ez.nebula.client.util.io.FileUtil;

import java.io.File;

/**
 * @author xgraza
 * @since 3/14/26
 */
public final class ClientSettingConfig implements IConfig
{
    @Override
    public String save()
    {
        final JsonObject object = new JsonObject();
        object.addProperty("useCustomSplashText", ClientConfig.USE_CUSTOM_SPLASH_TEXT);
        object.addProperty("openedGuiBefore", ClientConfig.OPENED_GUI_BEFORE);
        object.addProperty("debug", ClientConfig.DEBUG);
        return FileUtil.GSON.toJson(object);
    }

    @Override
    public void load(final String data)
    {
        if (data == null || data.isEmpty())
        {
            return;
        }
        final JsonElement element = FileUtil.JSON_PARSER.parse(data);
        if (!element.isJsonObject())
        {
            return;
        }
        final JsonObject object = element.getAsJsonObject();

        ClientConfig.DEBUG = object.has("debug")
                && object.get("debug").getAsBoolean();
        ClientConfig.USE_CUSTOM_SPLASH_TEXT = object.has("useCustomSplashText")
                && object.get("useCustomSplashText").getAsBoolean();
        ClientConfig.OPENED_GUI_BEFORE = object.has("openedGuiBefore")
                && object.get("openedGuiBefore").getAsBoolean();
    }

    @Override public File getFile()
    {
        return new File(Nebula.INSTANCE.getNebulaRootDir(), "settings.json");
    }
}
