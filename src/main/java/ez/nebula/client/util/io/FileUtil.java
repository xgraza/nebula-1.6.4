package ez.nebula.client.util.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

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
        final byte[] bytes = Files.readAllBytes(file.toPath());
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static void save(final File file, final String data) throws IOException
    {
        Files.write(file.toPath(), data.getBytes(StandardCharsets.UTF_8));
    }
}
