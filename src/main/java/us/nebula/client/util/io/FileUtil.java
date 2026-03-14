package us.nebula.client.util.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * @author xgraza
 * @since 02/14/25
 */
public final class FileUtil
{
    public static final Gson GSON = new GsonBuilder()
            .serializeNulls()
            .setPrettyPrinting()
            .create();
    public static final JsonParser JSON_PARSER = new JsonParser();

    public static InputStream getResourceStream(final String location)
    {
        return FileUtil.class.getResourceAsStream(location);
    }

    public static String read(final File file) throws IOException
    {
        final StringBuilder buffer = new StringBuilder();
        try (final FileInputStream fis = new FileInputStream(file))
        {
            int b;
            while ((b = fis.read()) != -1)
            {
                buffer.append((char) b);
            }
        }
        return buffer.toString();
    }

    public static void save(final File file, final String data) throws IOException
    {
        try (final FileOutputStream fos = new FileOutputStream(file))
        {
            final byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
            fos.write(bytes, 0, bytes.length);
        }
    }
}
