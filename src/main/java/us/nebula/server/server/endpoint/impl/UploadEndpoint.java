package us.nebula.server.server.endpoint.impl;

import us.nebula.server.ServerProperties;
import us.nebula.server.file.FileManager;
import us.nebula.server.server.endpoint.Endpoint;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author xgraza
 * @since 1.0.0
 */
@Endpoint.Metadata(value = "/upload", method = "PUT", headers = { "Authorization" })
public final class UploadEndpoint extends Endpoint
{
    private static final File LATEST_BUILD_DIRECTORY = new File(FileManager.DIRECTORY, "latest");

    @Override
    public void handle() throws IOException
    {
        // very nice very secure $$$
        // realistically, this doesnt matter unless someone gets on the server and reads .env
        final String authorization = getRequiredHeader("Authorization");
        if (!authorization.equals(ServerProperties.get("server_password")))
        {
            writeResponse("incorrect password", UNAUTHENTICATED);
            return;
        }

        final ZipInputStream zis = new ZipInputStream(request.getInputStream());
        logger.info("Deleting old files");
        recursiveDelete(LATEST_BUILD_DIRECTORY);

        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null)
        {
            String name = entry.getName();
            File directory = LATEST_BUILD_DIRECTORY;
            if (!entry.getName().startsWith("nebula"))
            {
                directory = new File(directory, "libraries");
                if (!directory.exists())
                {
                    directory.mkdir();
                }
                name = "nebula-latest.jar";
            }
            writeFile(new File(directory, name), zis);
            zis.closeEntry();
        }

        logger.info("Wrote new files :)");
        writeResponse("success", OK);
    }

    private void writeFile(final File file, final InputStream is) throws IOException
    {
        logger.info("Writing & caching {}", file.getName());
        try (final OutputStream os = Files.newOutputStream(file.toPath()))
        {
            int b;
            while ((b = is.read()) != -1)
            {
                os.write(b);
            }
        }
        FileManager.cacheFile(file);
    }

    private void recursiveDelete(final File directory)
    {
        final File[] files = directory.listFiles();
        if (files == null)
        {
            return;
        }
        for (final File file : files)
        {
            if (file.isDirectory())
            {
                recursiveDelete(file);
            } else
            {
                FileManager.deleteFile(file);
            }
        }
    }
}
