package us.nebula.server.server.endpoint.impl;

import us.nebula.server.file.FileManager;
import us.nebula.server.server.endpoint.Endpoint;

import java.io.IOException;
import java.util.Objects;

/**
 * @author xgraza
 * @since 1.0.0
 */
@Endpoint.Metadata("/manifest")
public final class ClientManifestEndpoint extends Endpoint
{
    @Override
    public void handle() throws IOException
    {
        writeResponse(Objects.requireNonNullElse(
                FileManager.readFile("manifest.json"), "{}"), OK);
    }
}
