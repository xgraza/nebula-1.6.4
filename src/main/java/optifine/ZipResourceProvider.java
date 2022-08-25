package optifine;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipResourceProvider implements IResourceProvider
{
    private ZipFile zipFile = null;

    public ZipResourceProvider(ZipFile zipFile)
    {
        this.zipFile = zipFile;
    }

    public InputStream getResourceStream(String path) throws IOException
    {
        path = Utils.removePrefix(path, "/");
        ZipEntry zipEntry = this.zipFile.getEntry(path);

        if (zipEntry == null)
        {
            return null;
        }
        else
        {
            InputStream in = this.zipFile.getInputStream(zipEntry);
            return in;
        }
    }
}
