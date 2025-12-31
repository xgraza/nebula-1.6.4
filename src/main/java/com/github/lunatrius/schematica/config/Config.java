package com.github.lunatrius.schematica.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import us.nebula.client.Nebula;
import us.nebula.client.api.config.IConfiguration;
import us.nebula.client.api.config.IJSONSerializable;
import us.nebula.client.api.value.Setting;
import us.nebula.client.util.io.FileUtil;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Config implements IJSONSerializable, IConfiguration
{
	public final Setting<Boolean> propEnableAlpha;
	public final Setting<Float> propAlpha;
	public final Setting<Boolean> propHighlight;
	public final Setting<Boolean> propHighlightAir;
	public final Setting<Float> propBlockDelta;
	public final Setting<Integer> propPlaceDelay;
	public final Setting<Integer> propTimeout;
	public final Setting<Boolean> propPlaceInstantly;
	public final Setting<Boolean> propPlaceAdjacent;
	public final Setting<Boolean> propDrawQuads;
	public final Setting<Boolean> propDrawLines;

    private final List<Setting<?>> settings = new LinkedList<>();
    private final File file;

	public Config(File file) {
        this.file = file;

        this.propEnableAlpha = new Setting<>("alphaEnabled", false);
        this.propAlpha = new Setting<>("alpha", 1.0f, 0.0f, 1.0f, 0.1f);
        this.propHighlight = new Setting<>("highlight", true);
        this.propHighlightAir = new Setting<>("highlightAir", true);
        this.propBlockDelta = new Setting<>("blockDelta", 0.005f, 0.0f, 0.5f, 0.001f);
		this.propPlaceDelay = new Setting<>("placeDelay", 1, 0, 20, 1);
        this.propTimeout = new Setting<>("timeout", 10, 0, 100, 1);
		this.propPlaceInstantly = new Setting<>("placeInstantly", false);
        this.propPlaceAdjacent = new Setting<>("placeAdjacent", true);
        this.propDrawQuads = new Setting<>("drawQuads", true);
        this.propDrawLines = new Setting<>("drawLines", true);

        Collections.addAll(settings,
                propEnableAlpha,
                propAlpha,
                propHighlight,
                propHighlightAir,
                propBlockDelta,
                propPlaceDelay,
                propTimeout,
                propPlaceInstantly,
                propPlaceAdjacent,
                propDrawQuads,
                propDrawLines);

        Nebula.INSTANCE.getConfigurationManager().addConfiguration(this);
	}

    @Override
    public void load(final String data)
    {
        if (data == null || data.isEmpty())
        {
            return;
        }
        final JsonElement element = FileUtil.JSON_PARSER.parse(data);
        fromJSON(element);
    }

    @Override
    public String save()
    {
        return FileUtil.GSON.toJson(toJSON());
    }

    @Override
    public File getFile()
    {
        return file;
    }

    @Override
    public void fromJSON(final JsonElement element)
    {
        if (!element.isJsonObject())
        {
            return;
        }
        final JsonObject object = element.getAsJsonObject();
        for (final Setting<?> setting : settings)
        {
            if (!object.has(setting.getName()))
            {
                continue;
            }
            final JsonElement e = object.get(setting.getName());
            setting.fromJSON(e);
        }
    }

    @Override
    public JsonElement toJSON()
    {
        final JsonObject object = new JsonObject();
        for (final Setting<?> setting : settings)
        {
            object.add(setting.getName(), setting.toJSON());
        }
        return object;
    }
}
