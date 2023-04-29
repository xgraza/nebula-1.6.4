package lol.nebula.util.system;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * @author aesthetical
 * @since 04/28/23
 */
public class FileUtils {

    /**
     * End stream indicator
     */
    private static final int END_STREAM = -1;

    /**
     * The GSON instance
     */
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    /**
     * Reads from a file
     * @param file the file to read from
     * @return the content of the file
     * @throws IOException if the InputStream fails to read
     */
    public static String read(File file) throws IOException {
        StringBuilder content = new StringBuilder();

        InputStream is = Files.newInputStream(file.toPath());

        int b;
        while ((b = is.read()) != END_STREAM) {
            content.append((char) b);
        }

        is.close();
        return content.toString();
    }

    /**
     * Writes to a file
     * @param file the file to write to
     * @param content the content to write to the file
     * @throws IOException if the OutputStream fails to write
     */
    public static void write(File file, String content) throws IOException {
        OutputStream os = Files.newOutputStream(file.toPath());
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        os.write(bytes, 0, bytes.length);
        os.close();
    }

    /**
     * Gets the GSON instance
     * @return the GSON instance
     */
    public static Gson getGSON() {
        return GSON;
    }
}
