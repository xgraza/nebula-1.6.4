/*
 * Copyright (c) xgraza 2025
 */

package us.nebula.launcher.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.LinkedList;
import java.util.List;

/**
 * @author xgraza
 * @since 12/20/25
 */
public final class NebulaMetadata
{
    private static final String METADATA_GIST_URL = "https://gist.githubusercontent.com/xgraza/bac55361538ba750f95c53cdd40ae4c7/raw/c5df3adc088bf2d7ec07e0c91b10a4ddeb8c6c50/nebula_launcher_metadata.json";

    public static final List<String> LIBRARIES = new LinkedList<>();
    public static String GIT_HASH, VERSION, BRANCH;

    public static void fetch() throws Exception
    {
        final String content = Util.makeConnection("GET", METADATA_GIST_URL, null);
        final JsonObject object = new JsonParser().parse(content).getAsJsonObject();
        load(object);
    }

    private static void load(final JsonObject object)
    {
        if (object.has("libraries"))
        {
            LIBRARIES.clear();
            final JsonArray array = object.get("libraries").getAsJsonArray();
            for (final JsonElement element : array)
            {
                if (element.isJsonPrimitive())
                {
                    LIBRARIES.add(element.getAsString());
                }
            }
        }
        if (object.has("git_hash"))
        {
            GIT_HASH = object.get("git_hash").getAsString();
        }
        if (object.has("version"))
        {
            VERSION = object.get("version").getAsString();
        }
        if (object.has("branch"))
        {
            BRANCH = object.get("branch").getAsString();
        }
    }

    private NebulaMetadata()
    {
        // no-op
    }
}
