package us.nebula.server.server.endpoint.impl;

import io.fusionauth.http.server.HTTPRequest;
import io.fusionauth.http.server.HTTPResponse;
import us.nebula.server.file.FileManager;
import us.nebula.server.server.endpoint.IEndpoint;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author xgraza
 * @since 1.0.0
 */
public final class DownloadEndpoint implements IEndpoint
{
    private final Map<String, File> exposedFilesMap = new LinkedHashMap<>();

    public DownloadEndpoint()
    {
        // bad bad bad! do not do this!
        // TODO: please change this pile of shit!!
        registerFile(FileManager.getFile("nebula-stable.jar"));
        registerFile(FileManager.getFile("nebula-latest.jar"));
        registerFile(FileManager.getFile("discord-rpc-1.0.0.jar"));
        registerFile(FileManager.getFile("xcmd-1.0.0.jar"));
    }

    @Override
    public void handle(final HTTPRequest req, final HTTPResponse res) throws IOException
    {
        final String file = readRequest(req);
        if (!exposedFilesMap.containsKey(file))
        {
            writeResponse(res, "invalid file requested", 400);
            return;
        }
        writeFile(res, exposedFilesMap.get(file));
    }

    @Override
    public String getMethod()
    {
        return "POST";
    }

    @Override
    public String getEndpoint()
    {
        return "/download";
    }

    private void registerFile(final File file)
    {
        exposedFilesMap.put(file.getName(), file);
    }
}
