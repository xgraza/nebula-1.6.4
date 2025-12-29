package us.nebula.server.server.endpoint.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import us.nebula.server.file.FileManager;
import us.nebula.server.server.endpoint.Endpoint;

import java.io.IOException;
import java.util.*;

/**
 * @author xgraza
 * @since 1.0.0
 */
@Endpoint.Metadata(value = "/download", method = "POST", headers = { "X-Nebula-Version" })
public final class DownloadEndpoint extends Endpoint
{
    private static final Map<String, List<String>> VERSION_DOWNLOAD_WHITELIST_MAP = new LinkedHashMap<>();

    static
    {
        try
        {
            final JsonObject manifestObject = JsonParser.parseString(
                            Objects.requireNonNull(FileManager.readFile("manifest.json")))
                    .getAsJsonObject();
            for (final String key : manifestObject.keySet())
            {
                final JsonObject object = manifestObject.get(key).getAsJsonObject();
                final List<String> whitelist = new LinkedList<>();
                whitelist.add(object.get("client_jar").getAsString());
                final JsonArray libArray = object.get("libraries").getAsJsonArray();
                for (final JsonElement element : libArray)
                {
                    whitelist.add(element.getAsString());
                }
                VERSION_DOWNLOAD_WHITELIST_MAP.put(key, whitelist);
            }
        } catch (final Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void handle() throws IOException
    {
        final String version = getRequiredHeader("X-Nebula-Version");
        final List<String> allowedDownloads = VERSION_DOWNLOAD_WHITELIST_MAP.get(version);
        if (allowedDownloads == null || allowedDownloads.isEmpty())
        {
            writeResponse("bad version", BAD_REQUEST);
            return;
        }
        final String fileName = readBody();
        if (!allowedDownloads.contains(fileName))
        {
            writeResponse("bad file", BAD_REQUEST);
            return;
        }
        // this is allowed because of the above check
        writeFile(FileManager.getFile(fileName));
    }
}
