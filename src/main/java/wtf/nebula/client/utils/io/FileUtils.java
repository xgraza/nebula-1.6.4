package wtf.nebula.client.utils.io;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {
    public static final File ROOT = Paths.get("").toFile();
    public static final File CLIENT_DIRECTORY = ROOT.toPath().resolve("nebula").toFile();

    public static String read(File file) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
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

    public static String read(InputStream stream) {
        if (stream == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder();

        int i;
        try {
            while ((i = stream.read()) != -1) {
                builder.append((char) i);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return builder.toString();
    }

    public static void write(File file, String content) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        OutputStream stream = null;

        try {
            stream = Files.newOutputStream(file.toPath());
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

    public static BufferedImage getResourceAsImage(String location) {
        InputStream inputStream = FileUtils.class.getResourceAsStream(location);
        if (inputStream == null) {
            return null;
        }

        try {
            BufferedImage image = ImageIO.read(inputStream);
            inputStream.close();
            return image;
        } catch (IOException e) {
            return null;
        }
    }
}
