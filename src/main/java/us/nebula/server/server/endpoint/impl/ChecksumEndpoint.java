package us.nebula.server.server.endpoint.impl;

import us.nebula.server.file.FileManager;
import us.nebula.server.server.endpoint.Endpoint;

import java.io.IOException;

/**
 * @author xgraza
 * @since 1.0.0
 */
@Endpoint.Metadata(value = "/checksum", method = "POST")
public final class ChecksumEndpoint extends Endpoint
{
    @Override
    public void handle() throws IOException
    {
        final String body = readBody();
        final String checksum = FileManager.getChecksum(body);
        if (checksum == null)
        {
            writeResponse("invalid file name", BAD_REQUEST);
        } else
        {
            writeResponse(checksum, OK);
        }
    }
}
