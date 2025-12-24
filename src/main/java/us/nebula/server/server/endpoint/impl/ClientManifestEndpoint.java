package us.nebula.server.server.endpoint.impl;

import io.fusionauth.http.server.HTTPRequest;
import io.fusionauth.http.server.HTTPResponse;
import us.nebula.server.file.FileManager;
import us.nebula.server.server.endpoint.IEndpoint;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * @author xgraza
 * @since 1.0.0
 */
public final class ClientManifestEndpoint implements IEndpoint
{
    private final File manifestFile;
    private String content;

    public ClientManifestEndpoint()
    {
        this.manifestFile = FileManager.getFile("manifest.json");
    }

    @Override
    public void handle(final HTTPRequest req, final HTTPResponse res) throws IOException
    {
        if (content == null || content.isEmpty())
        {
            content = null;
            cacheContent();
        }
        writeResponse(res, content, 200);
    }

    private void cacheContent() throws IOException
    {
        final StringBuilder builder = new StringBuilder();
        try (final InputStream is = Files.newInputStream(manifestFile.toPath()))
        {
            int b;
            while ((b = is.read()) != -1)
            {
                builder.append((char) b);
            }
        }
        content = builder.toString();
    }

    @Override
    public String getEndpoint()
    {
        return "/manifest";
    }
}
