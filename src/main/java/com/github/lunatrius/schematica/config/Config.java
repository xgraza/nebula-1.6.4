package com.github.lunatrius.schematica.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ez.nebula.client.Nebula;
import ez.nebula.client.api.config.IConfig;
import ez.nebula.client.api.setting.NumberSetting;
import ez.nebula.client.api.setting.Setting;
import ez.nebula.client.util.io.FileUtil;
import ez.nebula.client.util.io.IJSONSerializable;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Config implements IJSONSerializable, IConfig
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

    public Config(File file)
    {
        this.file = file;

        this.propEnableAlpha = new Setting.Builder<>("alphaEnabled", false)
                .build();
        this.propAlpha = new NumberSetting.Builder<>("alpha", 1.0f)
                .setMin(0.0f)
                .setMax(1.0f)
                .setScale(0.1f)
                .build();
        this.propHighlight = new Setting.Builder<>("highlight", true)
                .build();
        this.propHighlightAir = new Setting.Builder<>("highlightAir", true)
                .build();
        this.propBlockDelta = new NumberSetting.Builder<>("blockDelta", 0.005f)
                .setMin(0.0f)
                .setMax(0.5f)
                .setScale(0.001f)
                .build();
        this.propPlaceDelay = new NumberSetting.Builder<>("placeDelay", 1)
                .setMin(0)
                .setMax(20)
                .setScale(1)
                .build();
        this.propTimeout = new NumberSetting.Builder<>("timeout", 10)
                .setMin(0)
                .setMax(100)
                .setScale(1)
                .build();
        this.propPlaceInstantly = new Setting.Builder<>("placeInstantly", false)
                .build();
        this.propPlaceAdjacent = new Setting.Builder<>("placeAdjacent", true)
                .build();
        this.propDrawQuads = new Setting.Builder<>("drawQuads", true)
                .build();
        this.propDrawLines = new Setting.Builder<>("drawLines", true)
                .build();

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

        Nebula.CONFIGS.register(this);
    }

    @Override
    public void read(final String data)
    {
        if (data == null || data.isEmpty())
        {
            return;
        }
        final JsonElement element = FileUtil.JSON_PARSER.parse(data);
        fromJSON(element);
    }

    @Override
    public String write()
    {
        return FileUtil.GSON.toJson(toJSON());
    }

    @Override
    public File getLocation()
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
