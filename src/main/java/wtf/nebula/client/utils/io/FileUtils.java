package wtf.nebula.client.utils.io;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {
    public static final File ROOT = Paths.get("").toFile();
    public static final File CLIENT_DIRECTORY = new File(ROOT, "nebula");

    public static String read(File file) {
        InputStream stream = null;
        try {
            stream = Files.newInputStream(file.toPath());
            StringBuilder builder = new StringBuilder();

            int i;
            while ((i = stream.read()) != -1) {
                builder.append((char) i);
            }

            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }
}
