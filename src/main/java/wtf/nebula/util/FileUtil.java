package wtf.nebula.util;

import wtf.nebula.Nebula;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtil {
    public static final Path ROOT = Paths.get("");
    public static final Path CLIENT_PATH = ROOT.resolve("nebula-1.6.4");

    public static final Path MODULES = CLIENT_PATH.resolve("modules");
    public static final Path BOOKBOT = CLIENT_PATH.resolve("bookbot");

    public static final Path FRIENDS = CLIENT_PATH.resolve("friends.json");
    public static final Path WAYPOINTS = CLIENT_PATH.resolve("waypoints.json");
    public static final Path XRAY_BLOCKS = CLIENT_PATH.resolve("xrayblocks.json");
    public static final Path AUTOGG = CLIENT_PATH.resolve("autogg.txt");

    public static String read(Path path) {
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(path.toFile()));

            StringBuilder builder = new StringBuilder();

            int i;
            while ((i = reader.read()) != -1) {
                builder.append((char) i);
            }

            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    public static void create(Path path) {
        if (!Files.exists(path)) {
            try {
                Files.createFile(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void write(Path path, String content) {
        create(path);

        OutputStream stream = null;

        try {
            stream = new FileOutputStream(path.toFile());
            stream.write(content.getBytes(StandardCharsets.UTF_8), 0, content.length());
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
    }

    static {
        if (!Files.exists(CLIENT_PATH)) {
            try {
                Files.createDirectory(CLIENT_PATH);
                Nebula.log.logInfo("Created client directory");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
