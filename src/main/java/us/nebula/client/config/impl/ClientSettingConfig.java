package us.nebula.client.config.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import us.nebula.client.ClientSettings;
import us.nebula.client.Nebula;
import us.nebula.client.config.IConfiguration;
import us.nebula.client.util.io.FileUtil;

import java.io.File;

/**
 * @author xgraza
 * @since 3/14/26
 */
public final class ClientSettingConfig implements IConfiguration
{
    @Override
    public String save()
    {
        final JsonObject object = new JsonObject();
        object.addProperty("useCustomSplashText", ClientSettings.USE_CUSTOM_SPLASH_TEXT);
        object.addProperty("debug", ClientSettings.DEBUG);
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

        ClientSettings.DEBUG = object.has("debug")
                && object.get("debug").getAsBoolean();
        ClientSettings.USE_CUSTOM_SPLASH_TEXT = object.has("useCustomSplashText")
                && object.get("useCustomSplashText").getAsBoolean();
    }

    @Override public File getFile()
    {
        return new File(Nebula.INSTANCE.getNebulaRootDir(), "settings.json");
    }
}
